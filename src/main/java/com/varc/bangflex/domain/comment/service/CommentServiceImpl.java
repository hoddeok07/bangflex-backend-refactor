package com.varc.bangflex.domain.comment.service;

import com.varc.bangflex.domain.comment.dto.CommentDTO;
import com.varc.bangflex.domain.comment.entity.Comment;
import com.varc.bangflex.domain.comment.repository.CommentRepository;
import com.varc.bangflex.domain.comment.dto.CommentCreateDTO;
import com.varc.bangflex.domain.comment.dto.CommentUpdateDTO;
import com.varc.bangflex.domain.communityPost.entity.CommunityPost;
import com.varc.bangflex.domain.communityPost.repository.CommunityPostRepository;
import com.varc.bangflex.domain.user.entity.Member;
import com.varc.bangflex.domain.user.repository.UserRepository;
import com.varc.bangflex.exception.InvalidUserException;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service("commentService")
public class CommentServiceImpl implements CommentService {

    private final ModelMapper modelMapper;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final CommunityPostRepository communityPostRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository,
                              ModelMapper modelMapper,
                              UserRepository userRepository,
                              CommunityPostRepository communityPostRepository) {
        this.commentRepository = commentRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.communityPostRepository = communityPostRepository;
    }

    @Transactional
    @Override
    public void createComment(String loginId, Integer communityPostCode, CommentCreateDTO newComment) {
        Comment comment = new Comment();

        // 회원이 아니면 예외 발생
        Member author = userRepository.findById(loginId)
                .orElseThrow(() -> new InvalidUserException("댓글 작성 권한이 없습니다."));

        CommunityPost post = communityPostRepository.findById(communityPostCode)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));

        comment.setActive(true);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setContent(newComment.getContent());
        comment.setMember(author);
        comment.setCommunityPost(post);

        // 댓글 저장
        commentRepository.save(comment);
    }

    @Transactional
    @Override
    public void updateComment(String loginId, Integer communityPostCode,
                              Integer commentCode, CommentUpdateDTO modifiedComment) {

        Comment originalComment = commentRepository.findById(commentCode)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 댓글입니다."));

        CommunityPost post = communityPostRepository.findById(communityPostCode)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));

        // 회원이 아니라면 예외 발생
        Member author = userRepository.findById(loginId).orElseThrow(
                () -> new InvalidUserException("로그인이 필요합니다."));

        // 게시글 작성자가 아니라면 예외 발생
        if (!originalComment.getMember().getMemberCode().equals(author.getMemberCode())) {
            throw new InvalidUserException("댓글 수정 권한이 없습니다.");
        }

        originalComment.setContent(modifiedComment.getContent());
        originalComment.setMember(author);
        originalComment.setCommunityPost(post);

        // 수정된 댓글 저장
        commentRepository.save(originalComment);
    }

    @Transactional
    @Override
    public void deleteComment(String loginId, Integer communityPostCode, Integer commentCode) {

        Comment foundComment = commentRepository.findById(commentCode)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 댓글입니다."));

        // 회원이 아니라면 예외 발생
        Member author = userRepository.findById(loginId).orElseThrow(
                () -> new InvalidUserException("로그인이 필요합니다."));

        // 게시글 작성자가 아니라면 예외 발생
        if (!foundComment.getMember().getMemberCode().equals(author.getMemberCode())) {
            throw new InvalidUserException("댓글 삭제 권한이 없습니다.");
        }

        foundComment.setActive(false);
        commentRepository.save(foundComment);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentDTO> getAllCommentsOfPost(Integer communityPostCode) {
        CommunityPost foundPost = communityPostRepository.findById(communityPostCode)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));

        List<Comment> commentList = commentRepository.findByCommunityPostAndActiveTrue(foundPost);

        List<CommentDTO> allComments = commentList.stream()
                .map(comment -> {
                    CommentDTO commentDTO = modelMapper.map(comment, CommentDTO.class);
                    commentDTO.setNickname(comment.getMember().getNickname());
                    commentDTO.setCommunityPostCode(comment.getCommunityPost().getCommunityPostCode());
                    commentDTO.setProfile(comment.getMember().getImage());

                    return commentDTO;
                }).toList();

        return allComments;
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentDTO> getCommentsById(String loginId) {

        Member member = userRepository.findById(loginId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저입니다."));

        return commentRepository.findByMemberAndActiveTrue(member).stream()
                .map(
                        comment -> modelMapper.map(comment, CommentDTO.class)
                ).toList();
    }


}
