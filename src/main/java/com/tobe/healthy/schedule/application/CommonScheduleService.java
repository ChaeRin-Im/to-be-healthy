package com.tobe.healthy.schedule.application;

import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.schedule.domain.dto.out.ScheduleIdInfo;
import com.tobe.healthy.schedule.domain.entity.Schedule;
import com.tobe.healthy.schedule.domain.entity.ScheduleWaiting;
import com.tobe.healthy.schedule.repository.CommonScheduleRepository;
import com.tobe.healthy.schedule.repository.schedule_waiting.ScheduleWaitingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static com.tobe.healthy.config.error.ErrorCode.*;
import static java.time.LocalTime.NOON;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CommonScheduleService {
    private final MemberRepository memberRepository;
    private final CommonScheduleRepository commonScheduleRepository;
    private final ScheduleWaitingRepository scheduleWaitingRepository;

    public ScheduleIdInfo reserveSchedule(Long scheduleId, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        Schedule schedule = commonScheduleRepository.findAvailableScheduleById(scheduleId)
                .orElseThrow(() -> new CustomException(NOT_RESERVABLE_SCHEDULE));

        LocalDateTime before30Minutes = LocalDateTime.of(schedule.getLessonDt(), schedule.getLessonStartTime().minusMinutes(30));
        if (LocalDateTime.now().isAfter(before30Minutes)) throw new CustomException(RESERVATION_NOT_VALID);

        schedule.registerSchedule(member);
        return ScheduleIdInfo.create(schedule, getScheduleTimeText(schedule.getLessonStartTime()));
    }

    public ScheduleIdInfo cancelMemberSchedule(Long scheduleId, Long memberId) {
        Schedule schedule = commonScheduleRepository.findScheduleByApplicantId(memberId, scheduleId)
                .orElseThrow(() -> new CustomException(SCHEDULE_NOT_FOUND));

        LocalDateTime before24Hour = LocalDateTime.of(schedule.getLessonDt().minusDays(1), schedule.getLessonStartTime());
        if (LocalDateTime.now().isAfter(before24Hour)) throw new CustomException(RESERVATION_CANCEL_NOT_VALID);

        // 대기 테이블에 인원이 있으면 수정하기
        Optional<ScheduleWaiting> waitingScheduleOpt = scheduleWaitingRepository.findByScheduleId(scheduleId);
        if (waitingScheduleOpt.isPresent()) {
            ScheduleWaiting scheduleWaiting = waitingScheduleOpt.get();
            changeApplicantAndDeleteWaiting(scheduleWaiting, schedule);
            return ScheduleIdInfo.create(memberId, schedule, scheduleWaiting.getMember().getId(), getScheduleTimeText(schedule.getLessonStartTime()));
        } else {
            ScheduleIdInfo idInfo = ScheduleIdInfo.create(schedule, getScheduleTimeText(schedule.getLessonStartTime()));
            schedule.cancelMemberSchedule();
            return idInfo;
        }
    }

    private void changeApplicantAndDeleteWaiting(ScheduleWaiting scheduleWaiting, Schedule schedule) {
        schedule.changeApplicantInSchedule(scheduleWaiting.getMember());
        scheduleWaitingRepository.delete(scheduleWaiting);
    }

    private String getScheduleTimeText(LocalTime lessonStartTime){
        return NOON.isAfter(lessonStartTime) ? "오전 " + lessonStartTime : "오후 " + lessonStartTime;
    }
}
