package com.varc.bangflex.domain.comment.repository;

import com.varc.bangflex.domain.comment.entity.Comment;
import com.varc.bangflex.domain.communityPost.entity.CommunityPost;
import com.varc.bangflex.domain.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findByCommunityPostAndActiveTrue(CommunityPost foundPost);

    List<Object> findByMemberAndActiveTrue(Member member);

    int countByCommunityPostAndActiveTrue(CommunityPost communityPost);
}
