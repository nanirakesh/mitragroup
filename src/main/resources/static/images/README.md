# Images Directory

## Founder Photo Instructions

To add your photo to the dashboard:

1. **Save your photo** as `founder.jpg` in this directory (`src/main/resources/static/images/founder.jpg`)

2. **Photo Requirements:**
   - Format: JPG, PNG, or WebP
   - Recommended size: 300x300 pixels or larger (square aspect ratio works best)
   - File size: Under 1MB for optimal loading

3. **Update the HTML templates:**
   - In `admin/dashboard.html` and `user/dashboard.html`
   - Replace the placeholder div with:
   ```html
   <img src="/images/founder.jpg" alt="Rakesh Chowdary Bungatavula" 
        style="width: 100%; height: 100%; border-radius: 50%; object-fit: cover;">
   ```

4. **Current Status:**
   - Currently showing a placeholder icon
   - The circular styling is already applied
   - Photo will display in Teams-like circular format

## File Structure:
```
src/main/resources/static/images/
├── README.md (this file)
└── founder.jpg (add your photo here)
```