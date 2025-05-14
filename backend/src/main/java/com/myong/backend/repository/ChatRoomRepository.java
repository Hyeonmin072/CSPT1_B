package com.myong.backend.repository;

import com.myong.backend.domain.dto.chating.response.ChatRoomResponseDto;
import com.myong.backend.domain.entity.chating.ChatRoom;
import com.myong.backend.domain.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, UUID> {

    @Query("select new com.myong.backend.domain.dto.chating.response.ChatRoomResponseDto(cr.id, cr.lastMessage, cr.lastSendDate)" +
            "from ChatRoom cr join cr.user u " +
            "where u = :user")
    List<ChatRoomResponseDto> findAllByUser(@Param("user") User user);

}
