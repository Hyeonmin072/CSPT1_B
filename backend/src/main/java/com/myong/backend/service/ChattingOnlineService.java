package com.myong.backend.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class ChattingOnlineService {

    private final RedisTemplate<String, String> redisTemplate;

    public ChattingOnlineService(@Qualifier("redisTpl") RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String getUserSetKey(UUID chatRoomId){
        return "chatroom:" + chatRoomId + ":users";
    }

    /**
     * 채팅방 유저 접속 추가
     */
    public void addUserToChatRoom(UUID chatRoomId, String userEmail, String userRole){
        redisTemplate.opsForSet().add(getUserSetKey(chatRoomId), userEmail+userRole);
    }


    /**
     *  채팅방 유저 삭제
     */
    public void removeUserFromChatRoom(UUID chatRoomId, String userEmail, String userRole) {
        redisTemplate.opsForSet().remove(getUserSetKey(chatRoomId), userEmail+userRole);
    }

    /**
     *  채팅방 접속중인 유저 조회
     */
    public Set<String> getUsersInChatRoom(UUID chatRoomId) {
        return redisTemplate.opsForSet().members(getUserSetKey(chatRoomId));
    }

    /**
     *  상대방 접속 여부 체크
     */
    public boolean isPartnerOnline(UUID chatRoomId, String curUserEmail,String curUserRole){
        Set<String> users = getUsersInChatRoom(chatRoomId);
        if(users == null) return false;
        return users.stream().anyMatch(email -> !email.equals(curUserEmail+curUserRole));
    }

}
