package com.varc.bangflex.domain.comment.controller;

import com.varc.bangflex.common.ResponseMessage;
import com.varc.bangflex.domain.comment.dto.CommentDTO;
import com.varc.bangflex.domain.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController("tempCommentController")
@Slf4j
@RequestMapping("api/v1/comments")
public class TempCommentController {

    private final CommentService commentService;

    @Autowired
    public TempCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /* 사용자별 댓글 목록 조회 */
    @GetMapping
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "특정 사용자가 작성한 댓글 리스트 조회")
    public ResponseEntity<ResponseMessage<List<CommentDTO>>> getCommentsByMe(
            @RequestAttribute("loginId") String loginId
    ) {
        List<CommentDTO> foundComments = commentService.getCommentsById(loginId);

        if (foundComments.isEmpty()) foundComments = null;

        return ResponseEntity.ok(new ResponseMessage<>(200, "댓글 조회 성공", foundComments));
    }
}
