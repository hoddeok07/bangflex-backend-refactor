package com.varc.bangflex.domain.communityPost.controller;

import com.varc.bangflex.common.ResponseMessage;
import com.varc.bangflex.domain.communityPost.dto.CommunityLikeCreateDTO;
import com.varc.bangflex.domain.communityPost.service.CommunityPostService;
import com.varc.bangflex.domain.communityPost.dto.CommunityPostCreateDTO;
import com.varc.bangflex.domain.communityPost.dto.CommunityPostDTO;
import com.varc.bangflex.domain.communityPost.dto.CommunityPostUpdateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController("communityPostController")
@Slf4j
@RequestMapping("api/v1/community")
public class CommunityPostController {

    private final CommunityPostService communityPostService;

    @Autowired
    public CommunityPostController(CommunityPostService communityPostService) {
        this.communityPostService = communityPostService;
    }

    /* 게시글 등록 */
    @PostMapping(value = "/post", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "커뮤니티 게시글 등록 API")
    public ResponseEntity<ResponseMessage<Object>> createCommunityPost(
            @RequestAttribute("loginId") String loginId,
            @RequestPart CommunityPostCreateDTO newPost,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {

        communityPostService.createPost(loginId, newPost, images);
        return ResponseEntity.ok(new ResponseMessage<>(200, "게시글 등록 성공", null));
    }

    /* 게시글 수정 */
    @PutMapping(value = "/post/{communityPostCode}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "커뮤니티 게시글 수정 API")
    public ResponseEntity<ResponseMessage<Object>> updateCommunityPost(
            @RequestAttribute("loginId") String loginId,
            @PathVariable int communityPostCode,
            @Valid @RequestPart CommunityPostUpdateDTO modifiedPost,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {

        communityPostService.updatePost(loginId, communityPostCode, modifiedPost, images);
        return ResponseEntity.ok(new ResponseMessage<>(200, "게시글 수정 성공", null));
    }

    /* 게시글 삭제 */
    @DeleteMapping("/post/{communityPostCode}")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "커뮤니티 게시글 삭제 API")
    public ResponseEntity<ResponseMessage<Object>> deleteCommunityPost(
            @RequestAttribute("loginId") String loginId,
            @PathVariable int communityPostCode) {

        communityPostService.deletePost(loginId, communityPostCode);
        return ResponseEntity.ok(new ResponseMessage<>(200, "게시글 삭제 성공", null));
    }

//    /* 게시글 목록 조회(페이지네이션) */
//    @GetMapping("")
//    public ResponseEntity<ResponseMessage<Page<CommunityPostDTO>>> findPostList(
//                            @PageableDefault(size = 10) Pageable pageable) {
//
//        Page<CommunityPostDTO> postList = communityPostService.findPostList(pageable);
//        if (postList.hasContent()) {
//            return ResponseEntity.ok(new ResponseMessage<>(200, "게시글 목록 조회 성공", postList));
//        } else {
//            return ResponseEntity.noContent().build();
//        }
//    }

    /* 게시글 목록 조회 */
    @GetMapping("")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "커뮤니티 게시글 목록 조회 API")
    public ResponseEntity<ResponseMessage<List<CommunityPostDTO>>> getAllPosts(
            @RequestAttribute("loginId") String loginId) {

        List<CommunityPostDTO> posts = communityPostService.getAllPosts(loginId);
        return ResponseEntity.ok(new ResponseMessage<>(200, "게시글 목록 조회 성공", posts));
    }

    /* 게시글 상세 조회 */
    @GetMapping("/post/{communityPostCode}")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "커뮤니티 게시글 상세 조회 API")
    public ResponseEntity<ResponseMessage<CommunityPostDTO>> findPost(@RequestAttribute("loginId") String loginId,
                                                                      @PathVariable int communityPostCode) {

        CommunityPostDTO post = communityPostService.findPostByCode(loginId, communityPostCode);
        return ResponseEntity.ok(new ResponseMessage<>(200, "게시글 조회 성공", post));
    }

//    /* 게시글 구독 */
//    @GetMapping("/post/subscribe/{communityPostCode}")
//    public ResponseEntity<ResponseMessage<String>> subscribe(
//            @PathVariable Integer communityPostCode,
//            @RequestAttribute("loginId") String loginId
//    ) {
//        log.info("Community post code is {}", communityPostCode);
//        log.info("loginId is {}", loginId);
//        return ResponseEntity.ok(
//                new ResponseMessage<>(200, "구독 완료", "helloworld")
//        );
//    }

    /* 내가 작성한 게시글 목록 조회 */
    @GetMapping("/my")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "내가 작성한 커뮤니티 게시글 목록 조회 API")
    public ResponseEntity<ResponseMessage<List<CommunityPostDTO>>> getMyPosts(
            @RequestAttribute("loginId") String loginId) {

        List<CommunityPostDTO> myPostList = communityPostService.getMyPosts(loginId);
        return ResponseEntity.ok(new ResponseMessage<>(200, "내가 작성한 게시글 목록 조회 성공", myPostList));
    }

    /* 게시글 좋아요 등록 및 취소 */
    @PostMapping("/like")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "커뮤니티 게시글 좋아요 / 좋아요 취소 API")
    public ResponseEntity<ResponseMessage<Object>> addLike(@RequestAttribute("loginId") String loginId,
                                                           @RequestBody CommunityLikeCreateDTO newLike) {

        communityPostService.addLike(loginId, newLike);
        return ResponseEntity.ok(new ResponseMessage<>(200, "좋아요 또는 좋아요 취소 성공", null));
    }
}
