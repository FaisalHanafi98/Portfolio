# AWS Lightsail Deployment Guide

Complete guide for deploying the Portfolio Platform to AWS Lightsail with Nginx.

---

## Architecture Overview

```
faisalhanafi.com (Port 443 - HTTPS)
         │
         ▼
    ┌─────────────────────────────┐
    │   Nginx Reverse Proxy       │
    │   (AWS Lightsail Server)    │
    └─────────────────────────────┘
         │                    │
         │                    │
    / (root)           /api/v1/*
         │                    │
         ▼                    ▼
    ┌──────────┐      ┌──────────────┐
    │ React    │      │ Spring Boot  │
    │ Static   │      │ JAR          │
    │ Files    │      │ (Port 8080)  │
    └──────────┘      └──────────────┘
                             │
                             ▼
                      ┌──────────────┐
                      │ PostgreSQL   │
                      │ (Port 5432)  │
                      └──────────────┘
```

---

## Part 1: Get Your SSH Key

### Option A: Download from Lightsail Console (Recommended)

1. Go to [AWS Lightsail Console](https://lightsail.aws.amazon.com/)
2. Click **Account** (top right) → **Account** → **SSH Keys**
3. Find your key pair (likely named `LightsailDefaultKey` or similar)
4. Click **Download** to save the `.pem` file
5. Save it to: `C:\Users\asbou\.ssh\lightsail-key.pem`

### Option B: If You Already Downloaded It

Search your computer for `.pem` files:
```powershell
# In PowerShell
Get-ChildItem -Path C:\ -Filter *.pem -Recurse -ErrorAction SilentlyContinue
```

### Option C: Create New Key Pair

If you can't find it:
1. In Lightsail Console → **Account** → **SSH Keys**
2. Click **Create new**
3. Download and save to `C:\Users\asbou\.ssh\lightsail-key.pem`
4. You may need to reconfigure your instance to use the new key

---

## Part 2: Set Up WinSCP

### Install WinSCP

Download from: https://winscp.net/eng/download.php

### Configure Connection

1. Open WinSCP
2. Click **New Site**
3. Configure:
   ```
   File protocol:    SFTP
   Host name:        54.169.32.54
   Port number:      22
   User name:        ec2-user
   ```
4. Click **Advanced** → **SSH** → **Authentication**
5. In **Private key file**, browse to your `.pem` file
   - WinSCP will offer to convert it to `.ppk` format - **allow it**
6. Click **OK**, then **Save**
7. Name it: `Lightsail Portfolio`

### Test Connection

1. Click **Login**
2. If prompted about host key, click **Yes**
3. You should see the server filesystem

---

## Part 3: Prepare Files for Upload

### Build Frontend for Production

```powershell
# In PowerShell
cd C:\Users\asbou\OneDrive\Desktop\Work\Portfolio\frontend

# Install dependencies (if not done)
npm install

# Create production environment file
echo "VITE_API_URL=https://faisalhanafi.com/api/v1" > .env.production

# Build for production
npm run build
```

This creates a `dist` folder with optimized static files.

### Build Backend JAR

```powershell
cd C:\Users\asbou\OneDrive\Desktop\Work\Portfolio\backend

# Build the JAR
.\gradlew.bat build -x test
```

This creates: `build/libs/portfolio-0.0.1-SNAPSHOT.jar`

---

## Part 4: Upload Files via WinSCP

### Upload Frontend (Built React App)

1. In WinSCP, connect to your server
2. Navigate to: `/var/www/`
3. Create backup of current portfolio:
   ```
   Right-click in remote panel → Open in PuTTY
   Then in terminal:
   sudo mv /var/www/portfolio /var/www/portfolio.backup
   sudo mkdir -p /var/www/portfolio
   ```
4. In WinSCP:
   - **Local (left panel):** Navigate to `C:\Users\asbou\OneDrive\Desktop\Work\Portfolio\frontend\dist`
   - **Remote (right panel):** Navigate to `/tmp/portfolio-upload`

5. Create the upload directory first:
   - Right-click in remote panel → **New** → **Directory**
   - Name it: `portfolio-upload`
   - Navigate into `/tmp/portfolio-upload`

6. Select ALL files from `dist` folder on left
7. Drag to `/tmp/portfolio-upload` on right
8. Wait for upload to complete

### Move Files and Set Permissions

Open PuTTY (or right-click in WinSCP → Open in PuTTY):

```bash
# Move files to web directory
sudo mv /tmp/portfolio-upload/* /var/www/portfolio/

# Set ownership to nginx
sudo chown -R nginx:nginx /var/www/portfolio/

# Set correct permissions
sudo find /var/www/portfolio/ -type f -exec chmod 644 {} \;
sudo find /var/www/portfolio/ -type d -exec chmod 755 {} \;

# Verify
ls -la /var/www/portfolio/
```

### Upload Backend JAR

1. In WinSCP:
   - **Local:** Navigate to `C:\Users\asbou\OneDrive\Desktop\Work\Portfolio\backend\build\libs`
   - **Remote:** Navigate to `/tmp`

2. Upload `portfolio-0.0.1-SNAPSHOT.jar` to `/tmp`

3. In terminal:
```bash
# Create app directory
sudo mkdir -p /opt/portfolio

# Move JAR
sudo mv /tmp/portfolio-0.0.1-SNAPSHOT.jar /opt/portfolio/portfolio.jar

# Create user for running the app
sudo useradd -r -s /bin/false portfolio

# Set ownership
sudo chown -R portfolio:portfolio /opt/portfolio
```

---

## Part 5: Set Up PostgreSQL Database

### Install PostgreSQL

```bash
# Install PostgreSQL
sudo dnf install -y postgresql15 postgresql15-server

# Initialize database
sudo postgresql-setup --initdb

# Start and enable PostgreSQL
sudo systemctl start postgresql
sudo systemctl enable postgresql

# Create database and user
sudo -u postgres psql << EOF
CREATE DATABASE portfoliodb;
CREATE USER portfoliouser WITH PASSWORD 'YourSecurePassword123!';
GRANT ALL PRIVILEGES ON DATABASE portfoliodb TO portfoliouser;
\q
EOF
```

### Configure PostgreSQL for Local Connection

```bash
# Edit pg_hba.conf
sudo nano /var/lib/pgsql/data/pg_hba.conf
```

Add this line after the IPv4 local connections:
```
host    portfoliodb     portfoliouser   127.0.0.1/32    md5
```

Save (Ctrl+X, Y, Enter) and restart PostgreSQL:
```bash
sudo systemctl restart postgresql
```

### Test Database Connection

```bash
psql -h localhost -U portfoliouser -d portfoliodb
# Enter password when prompted
# Type \q to exit
```

---

## Part 6: Create Spring Boot Service

### Create Application Properties

```bash
# Create config directory
sudo mkdir -p /opt/portfolio/config

# Create application properties
sudo nano /opt/portfolio/config/application-prod.yml
```

Paste this content:
```yaml
spring:
  profiles:
    active: prod
  datasource:
    url: jdbc:postgresql://localhost:5432/portfoliodb
    username: portfoliouser
    password: YourSecurePassword123!
    driver-class-name: org.postgresql.Driver
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: update
    show-sql: false

server:
  port: 8080

cors:
  allowed-origins: https://faisalhanafi.com,https://www.faisalhanafi.com

logging:
  level:
    dev.faisal.portfolio: INFO
  file:
    name: /var/log/portfolio/application.log
```

Save (Ctrl+X, Y, Enter).

### Create Log Directory

```bash
sudo mkdir -p /var/log/portfolio
sudo chown portfolio:portfolio /var/log/portfolio
```

### Create Systemd Service

```bash
sudo nano /etc/systemd/system/portfolio.service
```

Paste this content:
```ini
[Unit]
Description=Portfolio Spring Boot Application
After=syslog.target network.target postgresql.service

[Service]
User=portfolio
Group=portfolio
WorkingDirectory=/opt/portfolio
ExecStart=/usr/bin/java -jar /opt/portfolio/portfolio.jar --spring.config.location=/opt/portfolio/config/application-prod.yml
SuccessExitStatus=143
Restart=always
RestartSec=10

# Security settings
NoNewPrivileges=true
PrivateTmp=true

# Resource limits
LimitNOFILE=65536

[Install]
WantedBy=multi-user.target
```

Save (Ctrl+X, Y, Enter).

### Start the Service

```bash
# Reload systemd
sudo systemctl daemon-reload

# Enable service to start on boot
sudo systemctl enable portfolio

# Start the service
sudo systemctl start portfolio

# Check status
sudo systemctl status portfolio

# View logs
sudo journalctl -u portfolio -f
```

---

## Part 7: Configure Nginx

### Update Nginx Configuration

```bash
sudo nano /etc/nginx/conf.d/faisalhanafi.conf
```

Replace with this configuration:
```nginx
# HTTP to HTTPS redirect
server {
    listen 80;
    server_name faisalhanafi.com www.faisalhanafi.com;
    return 301 https://$server_name$request_uri;
}

# HTTPS server
server {
    listen 443 ssl http2;
    server_name faisalhanafi.com www.faisalhanafi.com;

    # SSL configuration (Let's Encrypt)
    ssl_certificate /etc/letsencrypt/live/faisalhanafi.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/faisalhanafi.com/privkey.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;

    # Security headers
    add_header X-Frame-Options "DENY";
    add_header X-Content-Type-Options "nosniff";
    add_header X-XSS-Protection "1; mode=block";

    # Root directory for React app
    root /var/www/portfolio;
    index index.html;

    # Logs
    access_log /var/log/nginx/portfolio.access.log;
    error_log /var/log/nginx/portfolio.error.log;

    # API proxy to Spring Boot
    location /api/v1/ {
        proxy_pass http://localhost:8080/api/v1/;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;

        # Timeouts
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    # Static assets caching
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # React Router - serve index.html for all routes
    location / {
        try_files $uri $uri/ /index.html;
    }

    # CREAMS Laravel app (if you want to keep it)
    location /creams/demo/ {
        alias /var/www/creams/public/;
        try_files $uri $uri/ /creams/demo/index.php?$query_string;

        location ~ \.php$ {
            include fastcgi_params;
            fastcgi_pass unix:/var/run/php-fpm/www.sock;
            fastcgi_param SCRIPT_FILENAME $request_filename;
        }
    }
}
```

Save (Ctrl+X, Y, Enter).

### Test and Reload Nginx

```bash
# Test configuration
sudo nginx -t

# If OK, reload
sudo systemctl reload nginx
```

---

## Part 8: Verify Deployment

### Check Services are Running

```bash
# Check Nginx
sudo systemctl status nginx

# Check PostgreSQL
sudo systemctl status postgresql

# Check Portfolio backend
sudo systemctl status portfolio

# View backend logs
sudo journalctl -u portfolio -n 50
```

### Test API Endpoint

```bash
# Test health endpoint
curl http://localhost:8080/api/v1/health

# Should return: {"success":true,"data":{"status":"ok"},...}
```

### Test from Browser

1. Visit: https://faisalhanafi.com
   - Should show your React portfolio

2. Open browser console and check:
   - No CORS errors
   - API calls to `/api/v1/` working

3. Test API directly:
   - https://faisalhanafi.com/api/v1/health
   - https://faisalhanafi.com/api/v1/projects

---

## Troubleshooting

### Frontend Not Loading

```bash
# Check nginx error logs
sudo tail -f /var/log/nginx/portfolio.error.log

# Verify files exist
ls -la /var/www/portfolio/

# Check nginx configuration
sudo nginx -t
```

### Backend Not Responding

```bash
# Check if service is running
sudo systemctl status portfolio

# View logs
sudo journalctl -u portfolio -n 100

# Check if port 8080 is listening
sudo netstat -tlnp | grep 8080

# Restart service
sudo systemctl restart portfolio
```

### Database Connection Issues

```bash
# Check PostgreSQL is running
sudo systemctl status postgresql

# Test connection
psql -h localhost -U portfoliouser -d portfoliodb

# View PostgreSQL logs
sudo tail -f /var/lib/pgsql/data/log/postgresql-*.log
```

### CORS Errors

Update CORS in `/opt/portfolio/config/application-prod.yml`:
```yaml
cors:
  allowed-origins: https://faisalhanafi.com,https://www.faisalhanafi.com
```

Then restart:
```bash
sudo systemctl restart portfolio
```

---

## Updating the Application

### Update Frontend

```powershell
# On Windows, rebuild
cd C:\Users\asbou\OneDrive\Desktop\Work\Portfolio\frontend
npm run build
```

Then in WinSCP:
1. Upload new `dist` folder contents to `/tmp/portfolio-update`
2. In terminal:
```bash
sudo rm -rf /var/www/portfolio/*
sudo mv /tmp/portfolio-update/* /var/www/portfolio/
sudo chown -R nginx:nginx /var/www/portfolio/
```

### Update Backend

```powershell
# On Windows, rebuild
cd C:\Users\asbou\OneDrive\Desktop\Work\Portfolio\backend
.\gradlew.bat build -x test
```

Upload new JAR via WinSCP, then:
```bash
# Stop service
sudo systemctl stop portfolio

# Replace JAR
sudo mv /tmp/portfolio-0.0.1-SNAPSHOT.jar /opt/portfolio/portfolio.jar
sudo chown portfolio:portfolio /opt/portfolio/portfolio.jar

# Start service
sudo systemctl start portfolio

# Check status
sudo systemctl status portfolio
```

---

## Quick Reference Commands

```bash
# View backend logs
sudo journalctl -u portfolio -f

# Restart backend
sudo systemctl restart portfolio

# Reload nginx
sudo systemctl reload nginx

# Check all services
sudo systemctl status nginx postgresql portfolio

# View nginx access logs
sudo tail -f /var/log/nginx/portfolio.access.log
```

---

## Security Notes

1. **Firewall:** Ensure only ports 80, 443, and 22 are open
2. **Database Password:** Use a strong password and keep it secure
3. **SSH Key:** Keep your `.pem` file safe, don't commit to Git
4. **Updates:** Regularly update packages:
   ```bash
   sudo dnf update -y
   ```

---

Created for deploying Portfolio Platform to AWS Lightsail with Nginx.
