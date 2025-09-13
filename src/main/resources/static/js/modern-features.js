// Modern Features JavaScript

// Real-time notifications
function loadNotificationCount() {
    fetch('/api/notifications/unread-count')
        .then(response => response.json())
        .then(count => {
            const badge = document.getElementById('notificationCount');
            if (count > 0) {
                badge.textContent = count;
                badge.style.display = 'inline';
            } else {
                badge.style.display = 'none';
            }
        });
}

// Location services
function getCurrentLocation() {
    return new Promise((resolve, reject) => {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                position => {
                    resolve({
                        latitude: position.coords.latitude,
                        longitude: position.coords.longitude
                    });
                },
                error => reject(error)
            );
        } else {
            reject(new Error('Geolocation not supported'));
        }
    });
}

// Auto-refresh dashboard stats
function refreshDashboardStats() {
    fetch('/api/dashboard/stats')
        .then(response => response.json())
        .then(stats => {
            document.getElementById('totalRequests').textContent = stats.totalRequests;
            document.getElementById('totalProviders').textContent = stats.totalProviders;
            document.getElementById('pendingRequests').textContent = stats.pendingRequests;
            document.getElementById('completedRequests').textContent = stats.completedRequests;
        });
}

// Rating system
function submitRating(providerId, requestId, rating, comment) {
    fetch('/api/ratings', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            providerId: providerId,
            requestId: requestId,
            rating: rating,
            comment: comment
        })
    }).then(response => {
        if (response.ok) {
            alert('Rating submitted successfully!');
            location.reload();
        }
    });
}

// Initialize modern features
document.addEventListener('DOMContentLoaded', function() {
    // Load notification count on page load
    if (document.getElementById('notificationCount')) {
        loadNotificationCount();
        // Refresh every 30 seconds
        setInterval(loadNotificationCount, 30000);
    }
    
    // Auto-refresh dashboard if on dashboard page
    if (document.getElementById('totalRequests')) {
        refreshDashboardStats();
        setInterval(refreshDashboardStats, 60000);
    }
});