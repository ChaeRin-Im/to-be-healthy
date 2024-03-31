package com.tobe.healthy.gym.application;

import com.tobe.healthy.config.error.CustomException;
import com.tobe.healthy.gym.domain.dto.GymListCommandResult;
import com.tobe.healthy.gym.domain.dto.MemberInTeamCommandResult;
import com.tobe.healthy.gym.domain.dto.TrainerCommandResult;
import com.tobe.healthy.gym.domain.entity.Gym;
import com.tobe.healthy.gym.repository.GymRepository;
import com.tobe.healthy.member.domain.entity.Member;
import com.tobe.healthy.member.repository.MemberRepository;
import com.tobe.healthy.trainer.domain.entity.TrainerMemberMapping;
import com.tobe.healthy.trainer.respository.TrainerMemberMappingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static com.tobe.healthy.config.error.ErrorCode.*;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GymService {
	private final ModelMapper modelMapper;

	private final MemberRepository memberRepository;
	private final GymRepository gymRepository;
	private final TrainerMemberMappingRepository trainerMemberMappingRepository;

	public List<GymListCommandResult> findAllGym() {
		return gymRepository.findAll()
				.stream()
				.map(GymListCommandResult::new)
				.collect(toList());
	}

	public Boolean selectMyGym(Long gymId, int joinCode, Long memberId) {
		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

		Gym gym = gymRepository.findByIdAndJoinCode(gymId, joinCode)
				.orElseThrow(() -> new CustomException(GYM_NOT_FOUND));

		member.registerGym(gym);

		return true;
	}

	public Boolean registerGym(String name) {
		gymRepository.findByName(name).ifPresent(gym -> {
			throw new CustomException(GYM_DUPLICATION);
		});

		int joinCode = getJoinCode();

		Gym gym = Gym.registerGym(name, joinCode);

		gymRepository.save(gym);

		return true;
	}

	public List<TrainerCommandResult> findAllTrainersByGym(Long gymId) {
		return memberRepository.findAllTrainerByGym(gymId).stream()
			.map(TrainerCommandResult::new)
			.toList();
	}

	public Boolean selectMyTrainer(Long gymId, Long trainerId, Long memberId) {
		TrainerMemberMapping entity = TrainerMemberMapping.create(gymId, trainerId, memberId);
		trainerMemberMappingRepository.save(entity);
		return true;
	}

	public List<MemberInTeamCommandResult> findAllMyMemberInTeam(Long memberId) {
		List<Long> members = trainerMemberMappingRepository.findAllMembers(memberId).stream().map(m -> m.getMemberId()).collect(toList());
		return memberRepository.findAll(members).stream().map(m -> new MemberInTeamCommandResult(m)).collect(Collectors.toList());
	}

	private int getJoinCode() {
		Random random = new Random();
		StringBuilder buffer = new StringBuilder();
		int num = 0;

		while (buffer.length() < 6) {
			num = random.nextInt(10);
			buffer.append(num);
		}

		return Integer.parseInt(buffer.toString());
	}
}