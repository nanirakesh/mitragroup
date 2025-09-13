// Common JavaScript functions for Mitra application

// Logout function that can be called from anywhere
function logout() {
    // Create a form dynamically
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = '/logout';
    
    // Add CSRF token
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content') || 
                     document.querySelector('input[name="_csrf"]')?.value;
    
    if (csrfToken) {
        const csrfInput = document.createElement('input');
        csrfInput.type = 'hidden';
        csrfInput.name = '_csrf';
        csrfInput.value = csrfToken;
        form.appendChild(csrfInput);
    }
    
    // Submit the form
    document.body.appendChild(form);
    form.submit();
}

// Alternative: Convert logout links to POST forms on page load
document.addEventListener('DOMContentLoaded', function() {
    // Find all logout links and convert them to POST forms
    const logoutLinks = document.querySelectorAll('a[href="/logout"]');
    
    logoutLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            logout();
        });
    });
});