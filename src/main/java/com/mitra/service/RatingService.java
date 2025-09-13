package com.mitra.service;

import com.mitra.model.Rating;
import com.mitra.model.ServiceProvider;
import com.mitra.model.ServiceRequest;
import com.mitra.model.User;
import com.mitra.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingService {
    
    @Autowired
    private RatingRepository ratingRepository;
    
    public Rating createRating(User user, ServiceProvider provider, ServiceRequest request, 
                              Integer rating, String comment) {
        Rating newRating = new Rating();
        newRating.setUser(user);
        newRating.setProvider(provider);
        newRating.setServiceRequest(request);
        newRating.setRating(rating);
        newRating.setComment(comment);
        
        return ratingRepository.save(newRating);
    }
    
    public List<Rating> getProviderRatings(ServiceProvider provider) {
        return ratingRepository.findByProvider(provider);
    }
    
    public Double getProviderAverageRating(ServiceProvider provider) {
        Double avg = ratingRepository.getAverageRatingByProvider(provider);
        return avg != null ? avg : 0.0;
    }
    
    public Long getProviderTotalRatings(ServiceProvider provider) {
        return ratingRepository.getTotalRatingsByProvider(provider);
    }
}