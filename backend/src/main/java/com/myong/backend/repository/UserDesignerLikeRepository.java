package com.myong.backend.repository;

import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.domain.entity.user.User;
import com.myong.backend.domain.entity.userdesigner.UserDesignerLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDesignerLikeRepository extends JpaRepository<UserDesignerLike,Long> {

    Optional<UserDesignerLike> findByDesignerAndUser(Designer designer, User user);
}
