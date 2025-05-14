package com.myong.backend.repository;

import com.myong.backend.domain.entity.chating.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

}
