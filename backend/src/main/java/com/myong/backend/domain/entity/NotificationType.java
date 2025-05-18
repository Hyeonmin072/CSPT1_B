package com.myong.backend.domain.entity;

public enum NotificationType {
    SYSTEM,  // 이용자 모두에게 전달할 공지사항 또는 운영 메시지를 제공하는 알람
    USER, // 유저에게 제공하는 알람
    DESIGNER, // 디자이너에게 제공하는 알람
    SHOP // 사업자에게 제공하는 알람
}
