package com.mitra.repository;

import com.mitra.model.Rating;
import com.mitra.model.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByProvider(ServiceProvider provider);
    
    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.provider = ?1")
    Double getAverageRatingByProvider(ServiceProvider provider);
    
    @Query("SELECT COUNT(r) FROM Rating r WHERE r.provider = ?1")
    Long getTotalRatingsByProvider(ServiceProvider provider);
}