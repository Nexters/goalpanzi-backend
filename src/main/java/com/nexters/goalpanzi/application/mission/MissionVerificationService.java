package com.nexters.goalpanzi.application.mission;

import com.nexters.goalpanzi.application.mission.dto.request.CreateMissionVerificationCommand;
import com.nexters.goalpanzi.application.mission.dto.request.MissionVerificationQuery;
import com.nexters.goalpanzi.application.mission.dto.request.MyMissionVerificationQuery;
import com.nexters.goalpanzi.application.mission.dto.request.ViewMissionVerificationCommand;
import com.nexters.goalpanzi.application.mission.dto.response.MissionVerificationResponse;
import com.nexters.goalpanzi.application.mission.dto.response.MissionVerificationsResponse;
import com.nexters.goalpanzi.application.upload.ObjectStorageClient;
import com.nexters.goalpanzi.domain.common.BaseEntity;
import com.nexters.goalpanzi.domain.member.Member;
import com.nexters.goalpanzi.domain.member.repository.MemberRepository;
import com.nexters.goalpanzi.domain.mission.MissionMember;
import com.nexters.goalpanzi.domain.mission.MissionMembers;
import com.nexters.goalpanzi.domain.mission.MissionVerification;
import com.nexters.goalpanzi.domain.mission.MissionVerificationView;
import com.nexters.goalpanzi.domain.mission.repository.MissionMemberRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionVerificationRepository;
import com.nexters.goalpanzi.domain.mission.repository.MissionVerificationViewRepository;
import com.nexters.goalpanzi.exception.ErrorCode;
import com.nexters.goalpanzi.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MissionVerificationService {

    private final MissionVerificationRepository missionVerificationRepository;
    private final MissionMemberRepository missionMemberRepository;
    private final MissionVerificationViewRepository missionVerificationViewRepository;
    private final MemberRepository memberRepository;

    private final ObjectStorageClient objectStorageClient;
    
    private final MissionVerificationValidator missionVerificationValidator;
    private final MissionVerificationResponseSorter missionVerificationResponseSorter;

    public MissionVerificationsResponse getVerifications(final MissionVerificationQuery query) {
        LocalDate date = query.date() == null ? LocalDate.now() : query.date();

        Member member = memberRepository.getMember(query.memberId());
        MissionMembers missionMembers = new MissionMembers(missionMemberRepository.findAllByMissionId(query.missionId()));
        missionMembers.verifyMissionMember(member);
        List<MissionVerification> missionVerifications = missionVerificationRepository.findAllByMissionIdAndDate(query.missionId(), date);

        return new MissionVerificationsResponse(missionVerificationResponseSorter.sort(member, query.sortType(), query.direction(), missionVerifications, missionMembers.getMissionMembers()));
    }

    public MissionVerificationResponse getMyVerification(final MyMissionVerificationQuery query) {
        MissionVerification verification = missionVerificationRepository.getMyVerification(query.memberId(), query.missionId(), query.number());

        return MissionVerificationResponse.verified(verification.getMember(), verification, null);
    }

    @Transactional
    public void createVerification(final CreateMissionVerificationCommand command) {
        MissionMember missionMember = missionMemberRepository.getMissionMember(command.memberId(), command.missionId());

        missionVerificationValidator.validateVerificationSubmission(missionMember);

        String imageUrl = objectStorageClient.uploadFile(command.imageFile());
        missionMember.verify();
        missionVerificationRepository.save(new MissionVerification(missionMember.getMember(), missionMember.getMission(), imageUrl, missionMember.getVerificationCount()));
    }

    @Transactional
    public void deleteAllByMemberId(final Long memberId) {
        missionVerificationRepository.findAllByMemberId(memberId)
                .forEach(BaseEntity::delete);
    }

    @Transactional
    public void deleteAllByMissionId(final Long missionId) {
        missionVerificationRepository.findAllByMissionId(missionId)
                .forEach(BaseEntity::delete);
    }

    @Transactional
    public void viewMissionVerification(final ViewMissionVerificationCommand command) {
        Member member = memberRepository.getMember(command.memberId());
        MissionVerification missionVerification = missionVerificationRepository.findById(command.missionVerificationId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_VERIFICATION));

        missionVerificationViewRepository.save(new MissionVerificationView(missionVerification, member));
    }
}
