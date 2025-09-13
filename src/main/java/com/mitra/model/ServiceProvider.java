package com.mitra.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "service_providers")
public class ServiceProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    @Email
    @Column(unique = true)
    private String email;

    @NotBlank
    private String phone;

    @ElementCollection(targetClass = ServiceRequest.ServiceType.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "provider_skills")
    private Set<ServiceRequest.ServiceType> skills;

    @Enumerated(EnumType.STRING)
    private Status status = Status.AVAILABLE;

    private String address;
    private Double rating = 0.0;
    private Integer completedJobs = 0;
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "assignedProvider", cascade = CascadeType.ALL)
    private Set<ServiceRequest> assignedRequests;

    public enum Status {
        AVAILABLE, BUSY, OFFLINE
    }

    public ServiceProvider() {}

    public ServiceProvider(String name, String email, String phone, Set<ServiceRequest.ServiceType> skills) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.skills = skills;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Set<ServiceRequest.ServiceType> getSkills() { return skills; }
    public void setSkills(Set<ServiceRequest.ServiceType> skills) { this.skills = skills; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public Integer getCompletedJobs() { return completedJobs; }
    public void setCompletedJobs(Integer completedJobs) { this.completedJobs = completedJobs; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Set<ServiceRequest> getAssignedRequests() { return assignedRequests; }
    public void setAssignedRequests(Set<ServiceRequest> assignedRequests) { this.assignedRequests = assignedRequests; }

    public boolean hasSkill(ServiceRequest.ServiceType serviceType) {
        return skills != null && skills.contains(serviceType);
    }

    public boolean isAvailable() {
        return status == Status.AVAILABLE;
    }
}