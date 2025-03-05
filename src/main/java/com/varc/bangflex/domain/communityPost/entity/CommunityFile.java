package com.varc.bangflex.domain.communityPost.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Table(name = "community_file")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CommunityFile {

    @Id
    @Column(name = "community_file_code")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int communityFileCode;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "active", nullable = false)
    @ColumnDefault("TRUE")
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_post_code", nullable = false)
    private CommunityPost communityPost;
}
