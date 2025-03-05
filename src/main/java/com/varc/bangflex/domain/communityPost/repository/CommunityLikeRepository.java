package com.varc.bangflex.domain.communityPost.repository;

import com.varc.bangflex.domain.communityPost.entity.CommunityLike;
import com.varc.bangflex.domain.communityPost.entity.CommunityLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityLikeRepository extends JpaRepository<CommunityLike, CommunityLikeId> {

    boolean existsByMemberCodeAndCommunityPostCodeAndActiveTrue(int memberCode, int communityPostCode);

    int countByCommunityPostCodeAndActiveTrue(int communityPostCode);
}
