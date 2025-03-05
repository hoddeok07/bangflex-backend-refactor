package com.varc.bangflex.domain.comment.controller;

import com.varc.bangflex.common.ResponseMessage;
import com.varc.bangflex.domain.comment.dto.CommentDTO;
import com.varc.bangflex.domain.comment.dto.CommentCreateDTO;
import com.varc.bangflex.domain.comment.dto.CommentUpdateDTO;
import com.varc.bangflex.domain.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("commentController")
@Slf4j
@RequestMapping("api/v1/community/post/{communityPostCode}/comments")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /* 댓글 등록 */
    @PostMapping("")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "커뮤니티 게시글의 댓글 등록 API")
    public ResponseEntity<ResponseMessage<Object>> createComment(
                                            @RequestAttribute("loginId") String loginId,
                                            @PathVariable("communityPostCode") Integer communityPostCode,
                                            @RequestBody CommentCreateDTO newComment) {

        commentService.createComment(loginId, communityPostCode, newComment);
        return ResponseEntity.ok(new ResponseMessage<>(200, "댓글 등록 성공", null));
    }

    /* 댓글 수정 */
    @PutMapping("/{commentCode}")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "커뮤니티 게시글의 댓글 수정 API")
    public ResponseEntity<ResponseMessage<Object>> updateComment(
                                            @RequestAttribute("loginId") String loginId,
                                            @PathVariable("communityPostCode") Integer communityPostCode,
                                            @PathVariable("commentCode") Integer commentCode,
                                            @RequestBody CommentUpdateDTO modifiedComment) {

        commentService.updateComment(loginId, communityPostCode, commentCode, modifiedComment);
        return ResponseEntity.ok(new ResponseMessage<>(200, "댓글 수정 성공", null));
    }

    /* 댓글 삭제 */
    @DeleteMapping("/{commentCode}")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "커뮤니티 게시글의 댓글 삭제 API")
    public ResponseEntity<ResponseMessage<Object>> deleteComment(
                                            @RequestAttribute("loginId") String loginId,
                                            @PathVariable("communityPostCode") Integer communityPostCode,
                                            @PathVariable("commentCode") Integer commentCode) {

        commentService.deleteComment(loginId, communityPostCode, commentCode);
        return ResponseEntity.ok(new ResponseMessage<>(200, "댓글 삭제 성공", null));
    }

    /* 게시글의 댓글 목록 조회 */
    @GetMapping("")
    @Operation(summary = "커뮤니티 게시글의 댓글 목록 조회 API")
    public ResponseEntity<ResponseMessage<List<CommentDTO>>> getAllComments(
                                            @PathVariable("communityPostCode") Integer communityPostCode) {

        List<CommentDTO> commentsOfPost = commentService.getAllCommentsOfPost(communityPostCode);
        return ResponseEntity.ok(new ResponseMessage<>(200, "댓글 조회 성공", commentsOfPost));
    }
}
