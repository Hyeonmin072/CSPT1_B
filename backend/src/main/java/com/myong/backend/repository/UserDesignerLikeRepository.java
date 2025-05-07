package com.myong.backend.repository;

import com.myong.backend.domain.dto.user.response.LikeDesignerPageResponseDto;
import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.domain.entity.user.User;
import com.myong.backend.domain.entity.userdesigner.UserDesignerLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserDesignerLikeRepository extends JpaRepository<UserDesignerLike,Long> {

    Optional<UserDesignerLike> findByDesignerAndUser(Designer designer, User user);

    @Query("select new com.myong.backend.domain.dto.user.response.LikeDesignerPageResponseDto" +
            "(d.email, d.nickName, d.desc, d.shop.name, d.image ) " +
            "from UserDesignerLike udl JOIN udl.designer d where udl.user.email = :email")
    List<LikeDesignerPageResponseDto> findLikedDesignersByEmail(@Param("email")String email);
}
