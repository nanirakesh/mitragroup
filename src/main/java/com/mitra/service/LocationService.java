package com.mitra.service;

import com.mitra.model.Location;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class LocationService {
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Get location from IP address using free ipapi.co service
     */
    public Location getLocationFromIP(String ipAddress) {
        try {
            if (ipAddress == null || ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
                // Default location for localhost (Mumbai)
                return new Location(19.0760, 72.8777, "Mumbai", "Maharashtra", "400001");
            }
            
            String url = "http://ipapi.co/" + ipAddress + "/json/";
            String response = restTemplate.getForObject(url, String.class);
            
            JsonNode json = objectMapper.readTree(response);
            
            Double latitude = json.get("latitude").asDouble();
            Double longitude = json.get("longitude").asDouble();
            String city = json.get("city").asText();
            String region = json.get("region").asText();
            String postal = json.get("postal").asText();
            
            return new Location(latitude, longitude, city, region, postal);
            
        } catch (Exception e) {
            // Fallback to Mumbai coordinates
            return new Location(19.0760, 72.8777, "Mumbai", "Maharashtra", "400001");
        }
    }
    
    /**
     * Get location from pincode using free API
     */
    public Location getLocationFromPincode(String pincode) {
        try {
            String url = "http://postalpincode.in/api/pincode/" + pincode;
            String response = restTemplate.getForObject(url, String.class);
            
            JsonNode json = objectMapper.readTree(response);
            
            if (json.get("Status").asText().equals("Success")) {
                JsonNode postOffice = json.get("PostOffice").get(0);
                String district = postOffice.get("District").asText();
                String state = postOffice.get("State").asText();
                
                // Get coordinates for the district
                return getCoordinatesForCity(district, state, pincode);
            }
            
        } catch (Exception e) {
            // Fallback
        }
        
        return new Location(19.0760, 72.8777, "Mumbai", "Maharashtra", "400001");
    }
    
    /**
     * Calculate distance between two locations using Haversine formula
     */
    public double calculateDistance(Location loc1, Location loc2) {
        if (loc1 == null || loc2 == null) return Double.MAX_VALUE;
        
        double lat1 = Math.toRadians(loc1.getLatitude());
        double lon1 = Math.toRadians(loc1.getLongitude());
        double lat2 = Math.toRadians(loc2.getLatitude());
        double lon2 = Math.toRadians(loc2.getLongitude());
        
        double dlat = lat2 - lat1;
        double dlon = lon2 - lon1;
        
        double a = Math.sin(dlat/2) * Math.sin(dlat/2) + 
                   Math.cos(lat1) * Math.cos(lat2) * 
                   Math.sin(dlon/2) * Math.sin(dlon/2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        
        // Earth radius in kilometers
        return 6371 * c;
    }
    
    /**
     * Get coordinates for a city (simplified mapping)
     */
    private Location getCoordinatesForCity(String city, String state, String pincode) {
        // Major Indian cities coordinates
        switch (city.toLowerCase()) {
            case "mumbai": return new Location(19.0760, 72.8777, city, state, pincode);
            case "delhi": return new Location(28.7041, 77.1025, city, state, pincode);
            case "bangalore": return new Location(12.9716, 77.5946, city, state, pincode);
            case "hyderabad": return new Location(17.3850, 78.4867, city, state, pincode);
            case "chennai": return new Location(13.0827, 80.2707, city, state, pincode);
            case "kolkata": return new Location(22.5726, 88.3639, city, state, pincode);
            case "pune": return new Location(18.5204, 73.8567, city, state, pincode);
            case "ahmedabad": return new Location(23.0225, 72.5714, city, state, pincode);
            case "jaipur": return new Location(26.9124, 75.7873, city, state, pincode);
            case "lucknow": return new Location(26.8467, 80.9462, city, state, pincode);
            default: return new Location(19.0760, 72.8777, city, state, pincode);
        }
    }
}