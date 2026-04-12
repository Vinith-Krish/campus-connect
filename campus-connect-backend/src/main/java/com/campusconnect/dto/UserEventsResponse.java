package com.campusconnect.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEventsResponse {
    private List<EventResponse> registered;
    private List<EventResponse> interested;
}