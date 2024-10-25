package com.varc.bangflex.domain.theme.repository;

import com.varc.bangflex.domain.theme.entity.ReactionType;
import com.varc.bangflex.domain.theme.entity.ThemeReaction;
import com.varc.bangflex.domain.theme.entity.ThemeReactionId;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ThemeReactionRepository extends JpaRepository<ThemeReaction, ThemeReactionId> {

    @Query("SELECT tr FROM ThemeReaction tr JOIN FETCH tr.theme JOIN FETCH tr.member "
        + "WHERE tr.theme.themeCode = :themeCode AND tr.member.memberCode = :memberCode AND tr.active = true")
    Optional<ThemeReaction> findByIds(int themeCode, int memberCode);

    @Query("SELECT tr FROM ThemeReaction tr JOIN FETCH tr.member JOIN FETCH tr.theme "
        + "WHERE tr.memberCode = :memberCode AND tr.reaction = :#{#reaction.name()} AND tr.theme.active = true "
        + "ORDER BY tr.createdAt desc")
    List<ThemeReaction> findThemeByMemberReaction(Pageable pageable,
        @Param("memberCode") int memberCode, @Param("reaction") ReactionType reaction);

    @Query("SELECT tr FROM ThemeReaction tr JOIN FETCH tr.member JOIN FETCH tr.theme "
        + "WHERE tr.memberCode = :memberCode "
        + "AND tr.theme.active = true "
        + "AND (tr.reaction = com.varc.bangflex.domain.theme.entity.ReactionType.LIKE "
        + "OR tr.reaction = com.varc.bangflex.domain.theme.entity.ReactionType.SCRAPLIKE) "
        + "ORDER BY tr.createdAt desc")
    List<ThemeReaction> findThemeByMemberLike(Pageable pageable, @Param("memberCode") int memberCode);

    @Query("SELECT tr FROM ThemeReaction tr JOIN FETCH tr.member JOIN FETCH tr.theme "
        + "WHERE tr.memberCode = :memberCode "
        + "AND tr.theme.active = true "
        + "AND (tr.reaction = com.varc.bangflex.domain.theme.entity.ReactionType.SCRAP "
        + "OR tr.reaction = com.varc.bangflex.domain.theme.entity.ReactionType.SCRAPLIKE) "
        + "ORDER BY tr.createdAt desc")
    List<ThemeReaction> findThemeByMemberScrap(Pageable pageable, @Param("memberCode") int memberCode);


    @Query("SELECT tr FROM ThemeReaction tr JOIN FETCH tr.member JOIN FETCH tr.theme "
            + "WHERE tr.memberCode = :memberCode "
            + "AND tr.theme.active = true "
            + "AND tr.reaction = com.varc.bangflex.domain.theme.entity.ReactionType.SCRAP "
            + "ORDER BY tr.createdAt desc")
    List<ThemeReaction> findThemeByMemberCode(@Param("memberCode") int memberCode);
}
