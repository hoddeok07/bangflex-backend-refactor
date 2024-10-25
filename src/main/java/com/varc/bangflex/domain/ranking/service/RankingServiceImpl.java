package com.varc.bangflex.domain.ranking.service;

import com.varc.bangflex.domain.ranking.dto.MemberRankingDTO;
import com.varc.bangflex.domain.ranking.dto.ReviewLikeCountDTO;
import com.varc.bangflex.domain.ranking.dto.ReviewRankingDTO;
import com.varc.bangflex.domain.ranking.dto.ReviewRankingDateDTO;
import com.varc.bangflex.domain.ranking.entity.ReviewRanking;
import com.varc.bangflex.domain.ranking.repository.ReviewRankingRepository;
import com.varc.bangflex.domain.review.dto.ReviewDTO;
import com.varc.bangflex.domain.review.entity.Review;
import com.varc.bangflex.domain.review.entity.ReviewLike;
import com.varc.bangflex.domain.review.repository.ReviewLikeRepository;
import com.varc.bangflex.domain.review.repository.ReviewRepository;
import com.varc.bangflex.domain.review.service.ReviewService;
import com.varc.bangflex.domain.user.entity.Member;
import com.varc.bangflex.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class RankingServiceImpl implements RankingService {

    private ReviewRepository reviewRepository;
    private ReviewLikeRepository reviewLikeRepository;
    private ReviewRankingRepository reviewRankingRepository;
    private ModelMapper modelMapper;
    private ReviewService reviewService;
    private UserRepository userRepository;

    @Autowired
    public RankingServiceImpl(ReviewRepository reviewRepository
                            , ReviewLikeRepository reviewLikeRepository
                            , ReviewRankingRepository reviewRankingRepository
                            , ModelMapper modelMapper
                            , ReviewService reviewService
                            , UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.reviewLikeRepository = reviewLikeRepository;
        this.reviewRankingRepository = reviewRankingRepository;
        this.modelMapper = modelMapper;
        this.reviewService = reviewService;
        this.userRepository = userRepository;
    }

    @Scheduled(cron = "0 0 1 * * SUN")
    @Override
    @Transactional
    public void createReviewRanking() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneWeekAgo = now.minusWeeks(1);  // 현재로부터 1주일 이전

        List<ReviewLikeCountDTO> reviewLikes = reviewLikeRepository.findTop5ReviewCodes(oneWeekAgo);
        List<ReviewLikeCountDTO> top5ReviewLikes = reviewLikes.size() > 5 ? reviewLikes.subList(0, 5) : reviewLikes;

        for(ReviewLikeCountDTO reviewLike : top5ReviewLikes) {
            Review review = reviewRepository.findById(reviewLike.getReviewCode()).orElseThrow();
            Member member = userRepository.findById(review.getMember().getId()).orElseThrow();
            member.setPoint(member.getPoint() + 50);
            userRepository.save(member);
            reviewRankingRepository.save(ReviewRanking.builder()
                .createdAt(LocalDateTime.now())
                .active(true)
                .review(review)
                .build());
        }
        
        // 매주 일요일 새벽 1시에 실행될 작업
        log.info("일요일 새벽 1시에 랭킹 생성");
    }

    @Override
    @Transactional
    public ReviewRankingDateDTO findReviewRankingDate(Integer year) {
        List<String> dates = reviewRankingRepository.findDistinctDatesByYear(year).orElse(null);

        if (dates == null || dates.isEmpty())
            return null;
        else
            return ReviewRankingDateDTO.builder().ReviewRankingDates(dates).build();

    }

    @Override
    @Transactional
    public List<ReviewRankingDTO> findReviewRanking(String date, String loginId) {

        if(date == null)
            date = findReviewRankingDate(LocalDateTime.now().getYear()).getReviewRankingDates().get(0);

        Member member = userRepository.findById(loginId).orElseThrow();
        List<ReviewRanking> reviewRankings = reviewRankingRepository.findReviewByCreatedAtDate(date).orElse(null);

        if(reviewRankings == null || reviewRankings.isEmpty())
            return null;

        List<Review> reviews = reviewRankings.stream()
            .map(rankingReview -> {
                    Review review = modelMapper.map(rankingReview.getReview(), Review.class);
                    return review;
                }
            ).toList();

        List<ReviewDTO> reviewDTOS = reviewService.getReviewDTOS(reviews, member.getMemberCode());

        String finalDate = date;

        return reviewDTOS.stream().map(reviewDTO -> {
            ReviewRankingDTO reviewRankingDTO = modelMapper.map(reviewDTO, ReviewRankingDTO.class);
            reviewRankingDTO.setRankingDate(finalDate);
            return reviewRankingDTO;
        }).sorted(Comparator.comparingInt(ReviewRankingDTO::getLikes).reversed()).toList();
    }

    @Override
    @Transactional
    public List<ReviewDTO> findAllReviewRanking(Pageable pageable, String loginId) {

        Member member = userRepository.findById(loginId).orElseThrow();
        Page<ReviewLike> reviewLikes = reviewLikeRepository.findReviewByReviewLikes(pageable);

        List<Review> reviews = reviewLikes.stream()
            .map(reviewLike -> {
                    Review review = modelMapper.map(reviewLike.getReview(), Review.class);
                    return review;
                }
            ).toList();

        return reviewService.getReviewDTOS(reviews, member.getMemberCode());
    }

    @Override
    @Transactional
    public List<MemberRankingDTO> findAllMemberRanking(Pageable pageable) {
        List<Member> members = reviewRankingRepository.findTopRankingMember(pageable);

        return members.stream().map(member -> modelMapper.map(member, MemberRankingDTO.class)).toList();
    }
}
