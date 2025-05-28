package com.myong.backend.repository;

import com.myong.backend.domain.entity.chatting.ChatRoom;
import com.myong.backend.domain.entity.chatting.Message;
import com.myong.backend.domain.entity.chatting.SenderType;
import com.myong.backend.domain.entity.user.User;
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

    @Query("select me " +
            "from Message me " +
            "where me.chatRoom = :chatRoom and me.read = false " +
            "and ( me.senderEmail != :requestEmail or me.senderType != :requestRole )")
    List<Message> findUnreadMessageIds(@Param("chatRoom") ChatRoom chatRoom,
                                      @Param("requestEmail") String requestEmail,
                                       @Param("requestRole") SenderType requestRole);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.chatRoom = :chatRoom AND m.read = false AND NOT (m.senderEmail = :email AND m.senderType = :type)")
    int countUnreadExcludingSender(@Param("chatRoom") ChatRoom chatRoom,
                                   @Param("email") String email,
                                   @Param("type") SenderType type);


}
