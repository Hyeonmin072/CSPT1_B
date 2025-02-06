package com.myong.backend.configuration;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageServiceConfig {

    @Bean
    DefaultMessageService messageService() {
        return NurigoApp.INSTANCE.initialize("NCS3TUHASFLVVYWL", "I6VRPL2IO1MWBBWKIHXD9CFW67SZL5R2", "https://api.coolsms.co.kr");
    }
}
