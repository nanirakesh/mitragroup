#!/bin/bash

# Setup Nginx reverse proxy for domain access
echo "🔧 Setting up Nginx reverse proxy..."

# Install Nginx
sudo apt update
sudo apt install -y nginx

# Create Nginx configuration
sudo tee /etc/nginx/sites-available/mitra << 'EOF'
server {
    listen 80;
    server_name mitragroup.mooo.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
EOF

# Enable the site
sudo ln -sf /etc/nginx/sites-available/mitra /etc/nginx/sites-enabled/
sudo rm -f /etc/nginx/sites-enabled/default

# Test and restart Nginx
sudo nginx -t
sudo systemctl restart nginx
sudo systemctl enable nginx

echo "✅ Nginx setup complete!"
echo "🌐 Your domain should now work: http://mitragroup.mooo.com"