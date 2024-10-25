package com.swcamp9th.bangflixbackend.domain.communityPost.command.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CommunityPostUpdateDTO {

    private String title;                   // 제목
    private String content;                 // 내용

    // 수정할 첨부파일 URL 리스트
//    private List<String> imageUrls;         // 첨부파일들
}
