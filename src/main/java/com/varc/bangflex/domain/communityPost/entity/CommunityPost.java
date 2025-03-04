package com.varc.bangflex.domain.communityPost.entity;

import com.varc.bangflex.domain.user.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "community_post")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CommunityPost {

    @Id
    @Column(name = "community_post_code")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int communityPostCode;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "active", nullable = false)
    @ColumnDefault("TRUE")
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_code", nullable = false)
    private Member member;

    // 첨부파일과의 관계 설정
    @OneToMany(mappedBy = "communityPost", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<CommunityFile> communityFiles = new ArrayList<>();

//    // 댓글과의 관계 설정
//    @OneToMany(mappedBy = "communityPost", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
//    private List<Comment> comments = new ArrayList<>();
}
