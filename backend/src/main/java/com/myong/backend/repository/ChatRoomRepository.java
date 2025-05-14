package com.myong.backend.repository;

import com.myong.backend.domain.entity.chating.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, UUID> {

}
