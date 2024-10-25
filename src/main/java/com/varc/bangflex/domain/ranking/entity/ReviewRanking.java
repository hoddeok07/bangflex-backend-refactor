package com.varc.bangflex.domain.ranking.entity;

import com.varc.bangflex.domain.review.entity.Review;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_ranking")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRanking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ranking_code")
    private Integer rankingCode;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_code", nullable = false)
    private Review review;
}

