package com.campusconnect.dto;

import java.time.LocalDateTime;

import com.campusconnect.model.Role;
import com.campusconnect.model.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String email;
    private String name;
    private String collegename;
    private Role role;
    private LocalDateTime createdAt;
    public static UserDTO fromEntity(User user) {
        return UserDTO.builder()
            .id(user.getId())
            .email(user.getEmail())
            .name(user.getName())
            .collegename(user.getCollegename())
            .role(user.getRole())
            .createdAt(user.getCreatedAt())
            .build();
    }

}