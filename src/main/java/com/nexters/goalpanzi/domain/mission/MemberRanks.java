package com.nexters.goalpanzi.domain.mission;

import com.nexters.goalpanzi.domain.member.Member;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@RequiredArgsConstructor
public class MemberRanks {

    private final List<MemberRank> memberRanks;

    public static MemberRanks from(final List<MissionMember> missionMembers) {
        List<MissionMember> orderedMembersByVerification = missionMembers
                .stream()
                .sorted((m1, m2) -> m2.getVerificationCount() - m1.getVerificationCount())
                .toList();

        List<MemberRank> memberRanks = new ArrayList<>();
        for (int i = 0; i < orderedMembersByVerification.size(); i++) {
            MissionMember missionMember = orderedMembersByVerification.get(i);
            memberRanks.add(new MemberRank(missionMember.getMember(), i + 1));
        }

        return new MemberRanks(memberRanks);
    }

    public MemberRank getRankByMember(final Member member) {
        return memberRanks.stream()
                .filter(memberRank -> memberRank.member().equals(member))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No rank found for member " + member));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberRanks memberRanks1 = (MemberRanks) o;
        return Objects.equals(memberRanks, memberRanks1.memberRanks);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(memberRanks);
    }

    @Override
    public String toString() {
        return "Ranks{" +
                "ranks=" + memberRanks +
                '}';
    }
}
