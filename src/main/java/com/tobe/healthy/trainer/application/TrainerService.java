package com.tobe.healthy.trainer.application;

import com.tobe.healthy.common.RedisKeyPrefix;
import com.tobe.healthy.common.RedisService;
import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.config.error.ErrorCode;
import com.tobe.healthy.gym.application.GymMembershipService;
import com.tobe.healthy.gym.domain.dto.MemberInTeamDto;
import com.tobe.healthy.gym.domain.dto.in.MembershipAddCommand;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.domain.entity.MemberType;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.trainer.domain.dto.TrainerMemberMappingDto;
import com.tobe.healthy.trainer.domain.dto.in.MemberInviteCommand;
import com.tobe.healthy.trainer.domain.dto.in.MemberLessonCommand;
import com.tobe.healthy.trainer.domain.dto.out.MemberInviteResultCommand;
import com.tobe.healthy.trainer.domain.entity.TrainerMemberMapping;
import com.tobe.healthy.trainer.respository.TrainerMemberMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

import static com.tobe.healthy.config.error.ErrorCode.DATETIME_NOT_VALID;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TrainerService {

    private final RedisService redisService;
    private final MemberRepository memberRepository;
    private final TrainerMemberMappingRepository mappingRepository;

    public TrainerMemberMappingDto addMemberOfTrainer(Long trainerId, Long memberId, MemberLessonCommand command) {
        Member trainer = memberRepository.findByIdAndMemberTypeAndDelYnFalse(trainerId, MemberType.TRAINER)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        mappingRepository.findByTrainerIdAndMemberId(trainerId, memberId)
                .ifPresent(i -> {throw new CustomException(ErrorCode.MEMBER_ALREADY_MAPPED);});

        TrainerMemberMapping mapping = TrainerMemberMapping.create(trainer, member, command.getLessonCnt(), command.getLessonCnt());

        mappingRepository.deleteByMemberId(memberId);
        mappingRepository.flush();
        mappingRepository.save(mapping);
        member.registerGym(trainer.getGym());
        return TrainerMemberMappingDto.from(mapping);
    }

    public MemberInviteResultCommand inviteMember(MemberInviteCommand command, Member trainer) {
        memberRepository.findByIdAndMemberTypeAndDelYnFalse(trainer.getId(), MemberType.TRAINER)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        String name = command.getName();
        int lessonCnt = command.getLessonCnt();

        String uuid = System.currentTimeMillis() + "_" + UUID.randomUUID();
        String invitationKey = RedisKeyPrefix.INVITATION.getDescription() + uuid;
        String invitationLink = "https://www.to-be-healthy.site/invite?uuid=" + uuid;

        Map<String, String> invitedMapping = new HashMap<>() {{
            put("trainerId", trainer.getId().toString());
            put("name", name);
            put("lessonCnt", String.valueOf(lessonCnt));
        }};
        redisService.setValuesWithTimeout(invitationKey, JSONObject.toJSONString(invitedMapping), 24 * 60 * 60 * 1000); // 1days
        return new MemberInviteResultCommand(uuid, invitationLink);
    }

    public List<MemberInTeamDto> findAllMyMemberInTeam(Long trainerId, String searchValue, String sortValue, Pageable pageable) {
        List<MemberInTeamDto> members = memberRepository.findAllMyMemberInTeam(trainerId, searchValue, sortValue, pageable);
        return members.isEmpty() ? null : members;
    }
}
