package com.tobe.healthy.common;

import com.tobe.healthy.point.application.PointService;
import com.tobe.healthy.schedule.application.TrainerScheduleCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Scheduler {

    private final PointService pointService;
    private final TrainerScheduleCommandService trainerScheduleCommandService;

    //매월 1일 오전 1시
    @Scheduled(cron = "0 0 1 1 * *")
    public void updateMemberRankScheduler(){
        log.info("========== 트레이너의 학생들 랭킹 산정 스케줄러 작동 시작 ==========");
        pointService.updateMemberRank();
        log.info("========== 트레이너의 학생들 랭킹 산정 스케줄러 작동 완료 ==========");
    }

    // 매주 월요일 오전 12시
    @Scheduled(cron = "0 0 0 * * MON")
    public void deleteDisabledSchedule(){
        log.info("========== 트레이너의 DISABLED 스케줄 삭제 스케줄러 작동 시작 ==========");
        trainerScheduleCommandService.deleteDisabledSchedule();
        log.info("========== 트레이너의 DISABLED 스케줄 삭제 스케줄러 작동 완료 ==========");
    }

}
