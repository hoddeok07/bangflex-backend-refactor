package com.varc.bangflex.domain.comment.service;

import com.varc.bangflex.domain.comment.dto.CommentDTO;
import com.varc.bangflex.domain.comment.dto.CommentCreateDTO;
import com.varc.bangflex.domain.comment.dto.CommentUpdateDTO;

import java.util.List;

public interface CommentService {

    void createComment(String loginId, Integer communityPostCode, CommentCreateDTO newComment);

    void updateComment(String loginId, Integer communityPostCode, Integer commentCode, CommentUpdateDTO modifiedComment);

    void deleteComment(String loginId, Integer communityPostCode, Integer commentCode);

    List<CommentDTO> getAllCommentsOfPost(Integer communityPostCode);

    List<CommentDTO> getCommentsById(String loginId);
}
