# Mitra - Service Request Management System

A comprehensive web application for managing home service requests, built with Spring Boot and optimized for AWS Free Tier deployment.

## 🚀 Features

### For Users
- **User Registration & Authentication**: Secure email-based registration with OTP verification
- **Service Request Management**: Submit requests for plumbing, electrical, cleaning, etc.
- **Request Tracking**: Real-time monitoring with GPS tracking and notifications
- **Priority Setting**: AI-powered urgency detection and priority assignment
- **Voice Commands**: Create requests and check status using voice commands
- **Multi-Language Support**: Interface available in 12+ languages
- **IoT Integration**: Automatic service requests from smart home devices
- **Payment Integration**: Secure payments via Razorpay with multiple methods
- **Rating & Reviews**: Rate services and view provider feedback
- **AR Assistance**: Augmented reality for problem visualization

### For Admins
- **Advanced Dashboard**: Real-time analytics with predictive insights
- **AI-Powered Matching**: Intelligent provider assignment using machine learning
- **Request Management**: Comprehensive request lifecycle management
- **Provider Management**: Advanced provider onboarding and performance tracking
- **Blockchain Verification**: Immutable service history recording
- **IoT Device Management**: Monitor and manage connected smart devices
- **Multi-Language Administration**: Manage content in multiple languages
- **Advanced Analytics**: Business intelligence with predictive analytics
- **Voice Command Processing**: Handle voice-based service requests
- **Geolocation Services**: Location-based provider matching and routing

### For Service Providers
- **Provider Dashboard**: Comprehensive task and schedule management
- **AI Job Matching**: Receive perfectly matched job recommendations
- **Real-time Updates**: Update job progress with photo/video evidence
- **Skill Management**: AI-powered skill assessment and certification
- **Voice Assistance**: Voice-guided task completion and reporting
- **Blockchain Certificates**: Immutable service completion certificates
- **Performance Analytics**: Detailed performance metrics and insights
- **Multi-Language Support**: Work in preferred language
- **IoT Integration**: Receive automatic alerts from customer devices

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

### Backend Technologies
- **Framework**: Spring Boot 3.5.5, Spring Security, Spring Data JPA
- **AI/ML**: Spring AI, OpenAI GPT, DeepLearning4J, ND4J
- **IoT**: Eclipse Paho MQTT, Spring Integration
- **Blockchain**: Web3j, Ethereum integration
- **Voice Processing**: Microsoft Cognitive Services Speech SDK
- **Multi-Language**: Google Cloud Translate API
- **Analytics**: Micrometer, Prometheus metrics
- **Caching**: Redis, Spring Cache
- **Search**: Elasticsearch
- **Computer Vision**: OpenCV for AR features

### Frontend Technologies
- **Templates**: Thymeleaf with advanced templating
- **UI Framework**: Bootstrap 5 with custom responsive design
- **JavaScript**: Modern ES6+ with WebSocket support
- **Progressive Web App**: Service workers, offline support
- **Voice Interface**: Web Speech API integration
- **AR Support**: WebXR APIs for augmented reality

### Database & Storage
- **Primary DB**: H2 (development), MySQL/PostgreSQL (production)
- **Caching**: Redis for session and data caching
- **Search Engine**: Elasticsearch for advanced search
- **File Storage**: Local filesystem, AWS S3 support
- **Blockchain**: Ethereum network for service verification

### Integration & APIs
- **Payment**: Razorpay payment gateway
- **Maps**: Google Maps API for geolocation
- **Email**: SMTP with AWS SES support
- **SMS**: Twilio integration (configurable)
- **Push Notifications**: Firebase Cloud Messaging
- **Translation**: Google Translate API
- **Speech**: Azure Cognitive Services
- **IoT Platform**: MQTT broker integration

### Deployment & DevOps
- **Containerization**: Docker with multi-stage builds
- **Cloud Platform**: AWS (EC2, RDS, S3, CloudFormation)
- **CI/CD**: GitHub Actions with automated testing
- **Monitoring**: Prometheus, Grafana dashboards
- **Logging**: Structured logging with ELK stack support
- **Security**: HTTPS, JWT tokens, OAuth2 ready

### Development Tools
- **Build Tool**: Maven with advanced profiles
- **Java Version**: 17 with modern language features
- **Testing**: JUnit 5, Mockito, TestContainers
- **Code Quality**: SonarQube integration
- **API Documentation**: OpenAPI 3.0 (Swagger)

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

### Service Types & AI Categories
- **Plumbing**: Leak detection, pipe repair, installation
- **Electrical**: Wiring, appliance repair, smart home setup
- **Cleaning**: Regular, deep cleaning, post-construction
- **Carpentry**: Furniture repair, custom installations
- **Painting**: Interior, exterior, decorative
- **HVAC**: Heating, cooling, ventilation systems
- **Appliance Repair**: Kitchen, laundry, smart appliances
- **Security**: Camera installation, alarm systems
- **Landscaping**: Garden maintenance, irrigation
- **Pest Control**: Inspection, treatment, prevention
- **Smart Home**: IoT setup, automation, integration
- **Emergency Services**: 24/7 urgent repairs
- **Maintenance**: Preventive, scheduled, predictive
- **Other**: Custom services with AI categorization

## 🎯 Implementation Phases

### Phase 1: Core Functionality ✅
- [x] User registration and authentication with OTP
- [x] Service request creation and management
- [x] Admin dashboard and request assignment
- [x] Provider management with ratings
- [x] Responsive UI with Bootstrap 5
- [x] Payment integration with Razorpay
- [x] Email notifications and real-time updates
- [x] Basic geolocation services

### Phase 2: AI & Advanced Features ✅
- [x] AI-powered service matching with OpenAI
- [x] Voice assistant integration
- [x] Multi-language support (12+ languages)
- [x] IoT device integration with MQTT
- [x] Blockchain service verification
- [x] Advanced analytics dashboard
- [x] Real-time notifications with WebSocket
- [x] Redis caching for performance
- [x] Elasticsearch for advanced search

### Phase 3: Next-Generation Features ✅
- [x] Computer vision with OpenCV
- [x] Predictive analytics with machine learning
- [x] Advanced security with JWT and OAuth2 ready
- [x] Comprehensive monitoring and metrics
- [x] Progressive Web App capabilities
- [x] Automated CI/CD pipeline
- [x] Multi-tenant architecture support
- [x] API-first design with OpenAPI documentation

### Phase 4: Future Enhancements (Roadmap)
- [ ] Mobile apps (iOS/Android) with React Native
- [ ] Advanced AR features with 3D visualization
- [ ] Machine learning model training pipeline
- [ ] Kubernetes deployment with auto-scaling
- [ ] Advanced fraud detection
- [ ] Integration with major smart home platforms
- [ ] Drone integration for remote inspections
- [ ] Advanced chatbot with natural language processing

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