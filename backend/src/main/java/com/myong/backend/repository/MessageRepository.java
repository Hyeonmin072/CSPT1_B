package com.myong.backend.repository;

import com.myong.backend.domain.entity.chatting.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    @Query("select me from Message me " +
            "where me.chatRoom.id = :chatRoomId " +
            "and me.sendDate >= :week " +
            "order by me.sendDate asc")
    List<Message> findRecentMessages(@Param("chatRoomId") UUID chatRoomId,
                                     @Param("week") LocalDateTime week);

}
