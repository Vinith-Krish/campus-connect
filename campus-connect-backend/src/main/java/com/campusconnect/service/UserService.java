package com.campusconnect.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campusconnect.dto.EventResponse;
import com.campusconnect.dto.UserDTO;
import com.campusconnect.exception.ResourceNotFoundException;
import com.campusconnect.model.Event;
import com.campusconnect.model.EventInterest;
import com.campusconnect.model.EventRegistration;
import com.campusconnect.model.User;
import com.campusconnect.repository.EventInterestRepository;
import com.campusconnect.repository.EventRegistrationRepository;
import com.campusconnect.repository.EventRepository;
import com.campusconnect.repository.UserRepository;
import com.campusconnect.dto.UserEventsResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {
	private final UserRepository userRepository;
	private final EventRepository eventRepository;
	private final EventRegistrationRepository registrationRepository;
	private final EventInterestRepository interestRepository;
	public UserEventsResponse getMyEvents(Long userId) {
	    // Verify user exists
	    User user = userRepository.findById(userId)
	        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
	    
	    // Get registered events
	    List<EventRegistration> registrations = registrationRepository.findByUserId(userId);
	    List<EventResponse> registeredEvents = registrations.stream().map((EventRegistration reg) -> {
	            Event event = reg.getEvent();
	            Long regCount = registrationRepository.countByEventId(event.getId());
	            Long intCount = interestRepository.countByEventId(event.getId());
	            return EventResponse.fromEntity(event, regCount, intCount);
	        })
	        .collect(Collectors.toList());
	    
	    // Get interested events
	    List<EventInterest> interests = interestRepository.findByUserId(userId);
	    List<EventResponse> interestedEvents = interests.stream()
	        .map((EventInterest interest) -> {  // Add explicit type here
	            Event event = interest.getEvent();
	            Long regCount = registrationRepository.countByEventId(event.getId());
	            Long intCount = interestRepository.countByEventId(event.getId());
	            return EventResponse.fromEntity(event, regCount, intCount);
	        })
	        .collect(Collectors.toList());
	    
	    return UserEventsResponse.builder()
	        .registered(registeredEvents)
	        .interested(interestedEvents)
	        .build();
	}
	public UserDTO getUserById(Long userId) {
	    
	    User user = userRepository.findById(userId)
	        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
	    
	    return UserDTO.fromEntity(user);
	}
	@Transactional
	public UserDTO updateUserProfile(Long userId, String name) {
	    // Description: Update user profile information
	    // Parameters: user ID and new name
	    // Returns: Updated UserDTO
	    // Throws: ResourceNotFoundException if user not found
	    
	    User user = userRepository.findById(userId)
	        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
	    
	    user.setName(name);
	    User updatedUser = userRepository.save(user);
	    
	    return UserDTO.fromEntity(updatedUser);
	}


}
