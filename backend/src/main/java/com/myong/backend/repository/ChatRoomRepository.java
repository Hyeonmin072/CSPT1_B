package com.myong.backend.repository;

import com.myong.backend.domain.dto.chatting.response.ChatRoomResponseDto;
import com.myong.backend.domain.entity.chatting.ChatRoom;
import com.myong.backend.domain.entity.designer.Designer;
import com.myong.backend.domain.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, UUID> {


    List<ChatRoom> findAllByUser(User user);

    List<ChatRoom> findAllByDesigner(Designer designer);

    Optional<ChatRoom> findByUserAndDesigner(User user, Designer designer);


}
