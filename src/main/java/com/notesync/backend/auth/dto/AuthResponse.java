package com.notesync.backend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String accessToken;
    private String tokenType;
    private String username;
    private Long userId;

    public static AuthResponse of(String token, String username, Long userId) {
        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .username(username)
                .userId(userId)
                .build();
    }
}
