#!/bin/bash

# Server Setup Script for CI/CD
# Usage: ./setup-server.sh [SERVER_IP]

SERVER_USER="ubuntu"
APP_DIR="/home/$SERVER_USER/mitra"

echo "🔧 Setting up server for automated deployments..."

# Create application directory
mkdir -p $APP_DIR/logs
mkdir -p $APP_DIR/data

# Create production configuration
cat > $APP_DIR/application.properties << 'EOF'
spring.application.name=mitra
server.port=8080
server.address=0.0.0.0

# Database Configuration
spring.datasource.url=jdbc:h2:file:./data/mitra;DB_CLOSE_ON_EXIT=FALSE;AUTO_RECONNECT=TRUE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=false
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# Security Configuration
server.servlet.session.timeout=30m
server.servlet.session.cookie.http-only=true
server.servlet.session.cookie.secure=false
server.servlet.session.cookie.same-site=strict
server.servlet.session.cookie.name=MITRASESSIONID
server.servlet.session.tracking-modes=cookie
spring.session.store-type=none
server.error.include-stacktrace=never
server.error.include-message=never

# Security Headers
spring.security.headers.frame=deny
spring.security.headers.content-type=nosniff
spring.security.headers.xss-protection=1; mode=block
spring.security.headers.referrer-policy=strict-origin-when-cross-origin

# Application Configuration
app.jwt.secret=${JWT_SECRET:mitra-production-secret-key-2024}
app.jwt.expiration=1800000
app.admin.email=${ADMIN_EMAIL:admin@mitra.com}
app.admin.password=${ADMIN_PASSWORD:admin123}
app.max-login-attempts=5
app.lockout-duration=15

# Logging
logging.level.com.mitra=INFO
logging.level.org.springframework.security=WARN
logging.file.name=./logs/mitra.log
logging.file.max-size=10MB
logging.file.max-history=5
EOF

# Create start script
cat > $APP_DIR/start.sh << 'EOF'
#!/bin/bash

JAR_FILE="mitra-0.0.1-SNAPSHOT.jar"
PID_FILE="mitra.pid"
LOG_FILE="logs/mitra.log"

# Create directories
mkdir -p logs data

# Check if already running
if [ -f $PID_FILE ]; then
    PID=$(cat $PID_FILE)
    if ps -p $PID > /dev/null 2>&1; then
        echo "Mitra is already running with PID $PID"
        exit 1
    else
        rm -f $PID_FILE
    fi
fi

# Start the application
echo "Starting Mitra application..."
nohup java -jar -Dspring.profiles.active=prod $JAR_FILE > $LOG_FILE 2>&1 &
echo $! > $PID_FILE

echo "Mitra started with PID $(cat $PID_FILE)"
SERVER_IP=$(curl -s ifconfig.me 2>/dev/null || curl -s ipinfo.io/ip 2>/dev/null || echo 'localhost')
echo "Access: http://$SERVER_IP:8080"
EOF

# Create stop script
cat > $APP_DIR/stop.sh << 'EOF'
#!/bin/bash

PID_FILE="mitra.pid"

if [ -f $PID_FILE ]; then
    PID=$(cat $PID_FILE)
    if ps -p $PID > /dev/null 2>&1; then
        echo "Stopping Mitra application (PID: $PID)..."
        kill $PID
        sleep 5
        if ps -p $PID > /dev/null 2>&1; then
            echo "Force killing..."
            kill -9 $PID
        fi
        rm -f $PID_FILE
        echo "Mitra stopped successfully"
    else
        echo "Mitra is not running"
        rm -f $PID_FILE
    fi
else
    echo "PID file not found. Mitra may not be running."
fi
EOF

# Make scripts executable
chmod +x $APP_DIR/start.sh $APP_DIR/stop.sh

# Install Java 17 if not present
if ! java -version 2>&1 | grep -q "17"; then
    echo "Installing Java 17..."
    sudo apt update
    sudo apt install -y openjdk-17-jdk
fi

# Configure firewall
sudo ufw allow 8080/tcp 2>/dev/null || echo "Firewall configured or not available"

echo "✅ Server setup complete!"
echo "📁 Application directory: $APP_DIR"
echo "🌐 Application will be available at: http://${1:-$(curl -s ifconfig.me 2>/dev/null || echo 'SERVER_IP')}:8080"
echo ""
echo "Next steps:"
echo "1. Push your code to GitHub"
echo "2. Set up GitHub secrets (see CICD_SETUP.md)"
echo "3. Automated deployments will happen on every push!"