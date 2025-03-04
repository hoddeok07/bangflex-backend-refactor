package com.varc.bangflex.domain.communityPost.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CommunityPostDTO {

    private int communityPostCode;          // 게시글 코드
    private String title;                   // 제목
    private String content;                 // 내용
    private String createdAt;               // 생성일시(작성일시)
    private boolean active;                 // 활성화 여부
    private String nickname;                // 회원 닉네임(작성자)
    private String profile;                 // 회원 프로필 사진
    private boolean liked;                  // 좋아요 여부
    private int likeCount;                  // 좋아요 개수

    // 첨부파일 URL 리스트
    private List<String> imageUrls;         // 첨부파일들
}
