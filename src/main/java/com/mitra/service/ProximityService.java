package com.mitra.service;

import com.mitra.model.Location;
import com.mitra.model.ServiceProvider;
import com.mitra.model.ServiceRequest;
import com.mitra.repository.ServiceProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProximityService {
    
    @Autowired
    private ServiceProviderRepository serviceProviderRepository;
    
    @Autowired
    private LocationService locationService;
    
    private static final double MAX_DISTANCE_KM = 50.0; // 50km radius
    
    /**
     * Find nearest available providers for a service request
     */
    public List<ServiceProvider> findNearestProviders(ServiceRequest request) {
        Location requestLocation = getRequestLocation(request);
        
        List<ServiceProvider> availableProviders = serviceProviderRepository
            .findByStatusAndSkillsContaining(
                ServiceProvider.Status.AVAILABLE, 
                request.getServiceType()
            );
        
        return availableProviders.stream()
            .filter(provider -> provider.getLocation() != null)
            .map(provider -> {
                double distance = locationService.calculateDistance(
                    requestLocation, 
                    provider.getLocation()
                );
                provider.setDistance(distance);
                return provider;
            })
            .filter(provider -> provider.getDistance() <= MAX_DISTANCE_KM)
            .sorted((p1, p2) -> Double.compare(p1.getDistance(), p2.getDistance()))
            .limit(5) // Top 5 nearest providers
            .collect(Collectors.toList());
    }
    
    /**
     * Find the best provider based on distance and rating
     */
    public ServiceProvider findBestProvider(ServiceRequest request) {
        List<ServiceProvider> nearestProviders = findNearestProviders(request);
        
        if (nearestProviders.isEmpty()) {
            return null;
        }
        
        // Score based on distance (40%) and rating (60%)
        return nearestProviders.stream()
            .max((p1, p2) -> {
                double score1 = calculateProviderScore(p1);
                double score2 = calculateProviderScore(p2);
                return Double.compare(score1, score2);
            })
            .orElse(nearestProviders.get(0));
    }
    
    /**
     * Calculate provider score based on distance and rating
     */
    private double calculateProviderScore(ServiceProvider provider) {
        double distance = provider.getDistance();
        double rating = provider.getRating();
        
        // Normalize distance (closer = higher score)
        double distanceScore = Math.max(0, (MAX_DISTANCE_KM - distance) / MAX_DISTANCE_KM);
        
        // Normalize rating (0-5 scale)
        double ratingScore = rating / 5.0;
        
        // Weighted score: 40% distance, 60% rating
        return (distanceScore * 0.4) + (ratingScore * 0.6);
    }
    
    /**
     * Get location from service request
     */
    private Location getRequestLocation(ServiceRequest request) {
        // Try to get from coordinates first
        if (request.getLatitude() != null && request.getLongitude() != null) {
            try {
                double lat = Double.parseDouble(request.getLatitude());
                double lon = Double.parseDouble(request.getLongitude());
                return new Location(lat, lon, request.getCity(), null, request.getPincode());
            } catch (NumberFormatException e) {
                // Fall through to other methods
            }
        }
        
        // Try to get from pincode
        if (request.getPincode() != null && !request.getPincode().isEmpty()) {
            return locationService.getLocationFromPincode(request.getPincode());
        }
        
        // Fallback to default location
        return new Location(19.0760, 72.8777, "Mumbai", "Maharashtra", "400001");
    }
}