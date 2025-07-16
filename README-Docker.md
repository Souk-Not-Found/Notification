# 🐳 Docker Setup for Event Management Application

This guide explains how to containerize and run the Event Management application using Docker.

## 📋 Prerequisites

- Docker Desktop installed and running
- Docker Compose (usually included with Docker Desktop)
- At least 4GB of available RAM

## 🚀 Quick Start

### Option 1: Using the Build Script (Recommended)

```bash
# Make the script executable (Linux/Mac)
chmod +x docker-build.sh

# Build and start everything
./docker-build.sh full

# Or run commands individually:
./docker-build.sh build    # Build the image
./docker-build.sh start    # Start services
./docker-build.sh stop     # Stop services
./docker-build.sh logs     # View logs
./docker-build.sh status   # Check status
```

### Option 2: Manual Docker Commands

```bash
# Build the Docker image
docker build -t eventmanagement:latest .

# Start services with docker-compose
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

## 🏗️ Docker Architecture

### Services

1. **MySQL Database** (Port 3306)
   - Database: `EventManagement`
   - User: `eventuser`
   - Password: `eventpass`

2. **Spring Boot Application** (Port 8099)
   - WebSocket endpoint: `/ws`
   - REST API: `/api/*`
   - Health check: `/actuator/health`

### Network

- All services run on a custom bridge network: `eventmanagement-network`
- Services can communicate using service names (e.g., `mysql`)

## 🔧 Configuration

### Environment Variables

The application uses these environment variables (set in `docker-compose.yml`):

```yaml
SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/EventManagement?...
SPRING_DATASOURCE_USERNAME: eventuser
SPRING_DATASOURCE_PASSWORD: eventpass
SERVER_PORT: 8099
SPRING_PROFILES_ACTIVE: docker
```

### Database Configuration

- **Host**: `mysql` (service name)
- **Port**: `3306`
- **Database**: `EventManagement`
- **Username**: `eventuser`
- **Password**: `eventpass`

## 📊 Monitoring

### Health Checks

Both services include health checks:

- **MySQL**: Checks if database is responding
- **Application**: Checks if Spring Boot actuator health endpoint is accessible

### Logs

```bash
# View all logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f app
docker-compose logs -f mysql

# View last 100 lines
docker-compose logs --tail=100 app
```

## 🛠️ Development

### Building for Development

```bash
# Build with development settings
docker build -t eventmanagement:dev .

# Run with development profile
docker run -p 8099:8099 \
  -e SPRING_PROFILES_ACTIVE=dev \
  eventmanagement:dev
```

### Building for Production

```bash
# Build production image
docker build -f Dockerfile.prod -t eventmanagement:prod .

# Run production image
docker run -p 8099:8099 \
  -e SPRING_PROFILES_ACTIVE=prod \
  eventmanagement:prod
```

## 🔍 Troubleshooting

### Common Issues

1. **Port Already in Use**
   ```bash
   # Check what's using port 8099
   netstat -tulpn | grep 8099
   
   # Stop conflicting service or change port in docker-compose.yml
   ```

2. **Database Connection Issues**
   ```bash
   # Check if MySQL is running
   docker-compose ps mysql
   
   # Check MySQL logs
   docker-compose logs mysql
   ```

3. **Application Won't Start**
   ```bash
   # Check application logs
   docker-compose logs app
   
   # Check if database is ready
   docker-compose exec mysql mysqladmin ping -h localhost
   ```

### Debugging

```bash
# Access application container
docker-compose exec app sh

# Access MySQL container
docker-compose exec mysql mysql -u eventuser -p

# Check container resources
docker stats

# Inspect container
docker inspect eventmanagement-app
```

## 🧹 Cleanup

### Remove Everything

```bash
# Stop and remove containers, networks, and volumes
docker-compose down -v

# Remove images
docker rmi eventmanagement:latest

# Clean up unused Docker resources
docker system prune -f
```

### Using the Build Script

```bash
./docker-build.sh cleanup
```

## 📈 Performance Optimization

### Production Settings

For production deployment, use `Dockerfile.prod`:

```bash
# Build production image
docker build -f Dockerfile.prod -t eventmanagement:prod .

# Run with production settings
docker run -d \
  --name eventmanagement-prod \
  -p 8099:8099 \
  -e JAVA_OPTS="-Xms1g -Xmx2g" \
  eventmanagement:prod
```

### Resource Limits

Add resource limits to `docker-compose.yml`:

```yaml
services:
  app:
    # ... other settings ...
    deploy:
      resources:
        limits:
          memory: 2G
          cpus: '1.0'
        reservations:
          memory: 1G
          cpus: '0.5'
```

## 🔐 Security

### Security Features

- Non-root user (`appuser`) runs the application
- Minimal base image (Alpine Linux)
- Health checks for monitoring
- Network isolation

### Security Best Practices

1. **Change default passwords** in production
2. **Use secrets management** for sensitive data
3. **Regular security updates** of base images
4. **Network segmentation** for different environments

## 📚 Additional Resources

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Spring Boot Docker Guide](https://spring.io/guides/gs/spring-boot-docker/)

## 🆘 Support

If you encounter issues:

1. Check the logs: `docker-compose logs -f`
2. Verify Docker is running: `docker info`
3. Check service status: `docker-compose ps`
4. Review this documentation
5. Check the application logs for specific error messages 