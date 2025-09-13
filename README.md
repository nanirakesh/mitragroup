# Mitra - Service Request Management System

A comprehensive web application for managing home service requests, built with Spring Boot and optimized for AWS Free Tier deployment.

## 🚀 Features

### For Users
- **User Registration & Authentication**: Secure email-based registration
- **Service Request Management**: Submit requests for plumbing, electrical, cleaning, etc.
- **Request Tracking**: Monitor request status and assigned providers
- **Priority Setting**: Set urgency levels for requests

### For Admins
- **Dashboard Overview**: Real-time statistics and metrics
- **Request Management**: View, assign, and update all service requests
- **Provider Management**: Register and manage service providers
- **Auto-Assignment**: Intelligent provider assignment based on skills and availability
- **Manual Assignment**: Override automatic assignments when needed

### For Service Providers (Future Enhancement)
- **Provider Dashboard**: View assigned tasks and schedules
- **Status Updates**: Update job progress and completion
- **Skill Management**: Manage service capabilities

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Backend       │    │   Database      │
│   (Thymeleaf)   │◄──►│  (Spring Boot)  │◄──►│  (H2/MySQL)     │
│   Bootstrap     │    │   REST API      │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                              │
                              ▼
                    ┌─────────────────┐
                    │   AWS Services  │
                    │   EC2, RDS,     │
                    │   SES, S3       │
                    └─────────────────┘
```

## 🛠️ Technology Stack

- **Backend**: Spring Boot 3.5.5, Spring Security, Spring Data JPA
- **Frontend**: Thymeleaf, Bootstrap 5, HTML5, CSS3
- **Database**: H2 (development), MySQL (production)
- **Authentication**: Spring Security with role-based access control
- **Deployment**: Docker, AWS EC2, CloudFormation
- **Build Tool**: Maven
- **Java Version**: 17

## 📋 Prerequisites

- Java 17+
- Maven 3.6+
- Docker (for deployment)
- AWS CLI (for cloud deployment)
- AWS Account with Free Tier access

## 🚀 Quick Start

### Local Development

1. **Clone the repository**
```bash
git clone <repository-url>
cd mitra
```

2. **Run the application**
```bash
mvn spring-boot:run
```

3. **Access the application**
- URL: http://localhost:8080
- Admin Login: admin@mitra.com / admin123
- H2 Console: http://localhost:8080/h2-console

### AWS Deployment

1. **Automated Deployment**
```bash
./deploy.sh
# Select option 1 for AWS deployment
```

2. **Manual Deployment**
```bash
# Build application
mvn clean package -DskipTests

# Deploy to AWS
aws cloudformation create-stack \
  --stack-name mitra-stack \
  --template-body file://aws-deployment.yml \
  --region us-east-1
```

## 📁 Project Structure

```
mitra/
├── src/
│   ├── main/
│   │   ├── java/com/mitra/
│   │   │   ├── config/          # Security & configuration
│   │   │   ├── controller/      # Web controllers
│   │   │   ├── model/          # Entity classes
│   │   │   ├── repository/     # Data access layer
│   │   │   ├── service/        # Business logic
│   │   │   └── MitraApplication.java
│   │   ├── resources/
│   │   │   ├── templates/      # Thymeleaf templates
│   │   │   ├── static/         # CSS, JS, images
│   │   │   └── application.properties
├── aws-deployment.yml          # CloudFormation template
├── deploy.sh                   # Deployment script
├── Dockerfile                  # Container configuration
├── DEPLOYMENT_GUIDE.md         # Detailed deployment guide
└── pom.xml                     # Maven configuration
```

## 🔐 Security Features

- **Authentication**: Email-based login with encrypted passwords
- **Authorization**: Role-based access control (USER, ADMIN, PROVIDER)
- **CSRF Protection**: Built-in Spring Security CSRF protection
- **Password Encryption**: BCrypt password hashing
- **Session Management**: Secure session handling

## 💰 AWS Free Tier Optimization

### Resources Used
- **EC2 t2.micro**: 750 hours/month (free)
- **RDS db.t3.micro**: 750 hours/month (optional)
- **S3**: 5GB storage (free)
- **Data Transfer**: 1GB outbound/month (free)

### Cost Monitoring
- Automated billing alerts
- Resource usage tracking
- Optimized container memory (512MB)

## 🔧 Configuration

### Environment Variables
```bash
# Application
SPRING_PROFILES_ACTIVE=prod
ADMIN_EMAIL=admin@mitra.com
ADMIN_PASSWORD=your-secure-password
JWT_SECRET=your-jwt-secret

# Database (Optional)
RDS_HOSTNAME=your-rds-endpoint
RDS_USERNAME=admin
RDS_PASSWORD=your-db-password

# Email (Optional)
AWS_SES_USERNAME=your-ses-username
AWS_SES_PASSWORD=your-ses-password
```

## 📊 Database Schema

### Core Entities
- **Users**: User accounts with roles
- **ServiceRequests**: Service requests with status tracking
- **ServiceProviders**: Provider profiles with skills
- **Relationships**: User-Request, Provider-Request mappings

### Service Types
- Plumbing
- Electrical
- Cleaning
- Carpentry
- Painting
- Appliance Repair
- Other

## 🎯 Implementation Phases

### Phase 1: Core Functionality ✅
- [x] User registration and authentication
- [x] Service request creation and management
- [x] Admin dashboard and request assignment
- [x] Provider management
- [x] Basic UI with Bootstrap

### Phase 2: Enhanced Features (Future)
- [ ] Email notifications (SES integration)
- [ ] Real-time updates (WebSocket)
- [ ] Provider mobile app
- [ ] Payment integration
- [ ] Rating and review system
- [ ] Geolocation services

### Phase 3: Advanced Features (Future)
- [ ] Analytics dashboard
- [ ] Automated scheduling
- [ ] Multi-tenant support
- [ ] API for third-party integrations
- [ ] Advanced reporting

## 🧪 Testing

### Run Tests
```bash
# Unit tests
mvn test

# Integration tests
mvn verify

# Test coverage
mvn jacoco:report
```

### Manual Testing
1. Register new user account
2. Submit service request
3. Login as admin
4. Assign provider to request
5. Update request status

## 📈 Monitoring

### Health Checks
- Application: `/actuator/health`
- Database connectivity
- Memory usage monitoring

### Logging
- Application logs via Logback
- AWS CloudWatch integration
- Error tracking and alerting

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## 📝 API Documentation

### User Endpoints
- `GET /user/dashboard` - User dashboard
- `POST /user/request/new` - Create service request
- `GET /user/request/{id}` - View request details

### Admin Endpoints
- `GET /admin/dashboard` - Admin dashboard
- `GET /admin/requests` - All requests
- `POST /admin/request/{id}/assign` - Assign provider
- `GET /admin/providers` - Manage providers

## 🔍 Troubleshooting

### Common Issues
1. **Port 8080 already in use**: Change server.port in application.properties
2. **Database connection failed**: Check H2 console or RDS configuration
3. **Authentication issues**: Verify user roles and security configuration

### Debug Mode
```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

## 📞 Support

For support and questions:
- Check the [Deployment Guide](DEPLOYMENT_GUIDE.md)
- Review application logs
- Monitor AWS CloudWatch metrics

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

**Built with ❤️ for efficient home service management**