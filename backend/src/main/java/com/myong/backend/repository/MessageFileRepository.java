package com.myong.backend.repository;

import com.myong.backend.domain.entity.chatting.MessageFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MessageFileRepository extends JpaRepository<MessageFile, UUID> {
}
