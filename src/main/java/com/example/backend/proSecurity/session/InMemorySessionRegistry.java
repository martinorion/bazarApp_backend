package com.example.backend.proSecurity.session;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.UUID;

@Component
public class InMemorySessionRegistry {
    //Hash map, ktorý vlastne v RAM pamäti drží token prihlásnei a username
    private static final HashMap<String, String> SESSIONS = new HashMap<>();

    public String registerSession(final String username) {
        if (username == null) {
            throw new RuntimeException("Username needs to be provided");
        }
        //generovanie tokenu dole
        final String sessionId = generateSessionId();
        //nastavenie hashmap
        SESSIONS.put(sessionId, username);
        return sessionId;
    }

    public String getUsernameForSession(final String sessionId) {
        //odstrihol som nadbytočný chybný text bearer
        String sessionId2 = sessionId.substring(7);

        return SESSIONS.get(sessionId2);
    }

    private String generateSessionId() {
        return new String(
                Base64.getEncoder().encode(
                        UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8)
                )
        );
    }
}
