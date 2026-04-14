package com.campusconnect.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.campusconnect.model.Category;
import com.campusconnect.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByCategory(Category category);
    @Query("SELECT e FROM Event e WHERE " +
    	       "LOWER(e.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
    	       "LOWER(e.collegename) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
    	       "LOWER(e.venue) LIKE LOWER(CONCAT('%', :search, '%'))")
    	List<Event> findByTitleContainingIgnoreCase(@Param("search") String search);

    	@Query("SELECT e FROM Event e WHERE " +
    	       "(LOWER(e.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
    	       "LOWER(e.collegename) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
    	       "LOWER(e.venue) LIKE LOWER(CONCAT('%', :search, '%'))) " +
    	       "AND e.category = :category")
    	List<Event> findByTitleContainingIgnoreCaseAndCategory(@Param("search") String search, @Param("category") Category category);
    	List<Event> findByCreatedByOrderByDateDesc(Long userId);
    	
    	@Query("""
    			SELECT e FROM Event e
    			WHERE (CAST(:search AS text) IS NULL OR
    			      LOWER(e.title) LIKE LOWER(CONCAT('%', CAST(:search AS text), '%')) OR
    			      LOWER(e.collegename) LIKE LOWER(CONCAT('%', CAST(:search AS text), '%')) OR
    			      LOWER(e.venue) LIKE LOWER(CONCAT('%', CAST(:search AS text), '%')))
    			AND (:category IS NULL OR e.category = :category)
    			ORDER BY e.date DESC
    			""")
    			Page<Event> searchEvents(@Param("search") String search,
    			                         @Param("category") Category category,
    			                         Pageable pageable);
}
