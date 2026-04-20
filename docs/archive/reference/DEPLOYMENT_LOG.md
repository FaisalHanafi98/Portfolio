# Portfolio Deployment Log

## 2026-01-23: Initial Lightsail Deployment Setup

### Completed Tasks

**1. Server Configuration (One-Time Setup)**
- ✅ Installed systemd service for Spring Boot backend (`portfolio-backend.service`)
- ✅ Updated Nginx configuration with API proxy routing
  - `/` → React frontend (static files at `/var/www/portfolio`)
  - `/api/*` → Spring Boot backend (proxy to localhost:8080)
  - `/creams/*` → CREAMS Laravel app (existing)
- ✅ Enabled portfolio-backend service to auto-start on boot
- ✅ Configured memory limits (300MB max) for t3.micro instance

**2. Deployment Automation**
- ✅ Created GitHub Actions workflow (`.github/workflows/deploy-portfolio.yml`)
  - Builds React frontend with Vite
  - Builds Spring Boot JAR with Gradle
  - Deploys via SSH to Lightsail (54.169.32.54)
  - Restarts backend service automatically
- ✅ Configured GitHub Secrets:
  - `LIGHTSAIL_SSH_KEY`: SSH private key for deployment
  - `LIGHTSAIL_IP`: Static IP address (54.169.32.54)

**3. Deployment Scripts Created**
- ✅ `deployment/setup-server.sh` - One-time server configuration
- ✅ `deployment/deploy.sh` - Manual deployment script
- ✅ `deployment/README.md` - Complete deployment documentation
- ✅ `deployment/nginx-faisalhanafi.conf` - Nginx reverse proxy config
- ✅ `deployment/portfolio-backend.service` - Systemd service definition

**4. Frontend Deployment**
- ✅ React build deployed to `/var/www/portfolio`
- ✅ Accessible at https://faisalhanafi.com
- ✅ SSL certificate working (Let's Encrypt)

**5. Backend Deployment**
- ✅ GitHub Actions successfully builds and deploys JAR (53MB)
- ✅ Systemd service runs backend on port 8080 (proxied via Nginx)
- ✅ Service auto-restarts on failure and boots on system startup
- ⚠️ Backend requires production database configuration (currently crashing on H2 initialization)
- 📝 API endpoints will be available at https://faisalhanafi.com/api/ once database is configured

### Deployment Strategy

**Automatic Deployment (Current Setup):**
```bash
git add .
git commit -m "Your changes"
git push origin main
# GitHub Actions automatically builds and deploys both frontend and backend
```

**Manual Deployment (Backup):**
```bash
cd deployment
./deploy.sh
```

**Quick Updates:**
- **Frontend only**: Copy `frontend/dist/*` to `/var/www/portfolio/` via SCP
- **Backend only**: `sudo systemctl restart portfolio-backend` (10 sec restart)
- **No full server restart required**

### Server Details

| Component | Location | Port | Process |
|-----------|----------|------|---------|
| Frontend | `/var/www/portfolio/` | 80, 443 | Nginx static files |
| Backend JAR | `/home/ec2-user/portfolio-backend.jar` | 8080 | Java/Spring Boot |
| Nginx Config | `/etc/nginx/conf.d/faisalhanafi.conf` | 80, 443 | Nginx reverse proxy |
| Systemd Service | `/etc/systemd/system/portfolio-backend.service` | - | systemd |

### Useful Commands

```bash
# Check backend status
ssh -i LightsailDefaultKey-ap-southeast-1.pem ec2-user@54.169.32.54 "sudo systemctl status portfolio-backend"

# View backend logs (live)
ssh -i LightsailDefaultKey-ap-southeast-1.pem ec2-user@54.169.32.54 "sudo journalctl -u portfolio-backend -f"

# Restart backend only
ssh -i LightsailDefaultKey-ap-southeast-1.pem ec2-user@54.169.32.54 "sudo systemctl restart portfolio-backend"

# Check Nginx status
ssh -i LightsailDefaultKey-ap-southeast-1.pem ec2-user@54.169.32.54 "sudo systemctl status nginx"

# Reload Nginx (after config changes)
ssh -i LightsailDefaultKey-ap-southeast-1.pem ec2-user@54.169.32.54 "sudo nginx -t && sudo systemctl reload nginx"
```

### Architecture

```
Internet (https://faisalhanafi.com)
    │
    ├─ Nginx (Port 80/443) - SSL Termination
    │    │
    │    ├─ / → Static files (/var/www/portfolio/) - React SPA
    │    ├─ /api/* → Proxy to localhost:8080 - Spring Boot Backend
    │    └─ /creams/* → PHP-FPM - CREAMS Laravel App
    │
    └─ Backend Services
         ├─ portfolio-backend.service (Java, Port 8080)
         └─ php-fpm.service (PHP, Unix socket)
```

### Security Notes

**⚠️ Action Required:**
- [ ] Rotate SSH key in repository (currently exposed in commit history)
- [ ] Remove `LightsailDefaultKey-ap-southeast-1.pem` from repository
- [ ] Add new key to GitHub Secrets only
- [ ] Update `.gitignore` to exclude `*.pem` files

### Resource Monitoring

**Current Instance:** AWS Lightsail t3.micro
- **CPU:** 2 vCPU
- **RAM:** 1GB (419MB shown, very tight)
- **Storage:** 20GB SSD (28% used)
- **Memory Usage:** ~90% (298MB used, only 4MB free during build)

**Recommendations:**
- ✅ Backend builds happen on GitHub Actions (not on server)
- ✅ Memory limited to 300MB for Java process
- 🔄 Consider upgrading to t3.small (2GB RAM) if adding more services
- 🔄 Monitor memory with: `ssh ... "free -h && ps aux --sort=-%mem | head -10"`

### Next Deployment

To deploy new changes:
1. Make code changes locally
2. Commit and push to GitHub: `git push origin main`
3. GitHub Actions will automatically:
   - Build frontend (React + Vite)
   - Build backend (Spring Boot JAR)
   - Deploy both to Lightsail
   - Restart backend service
4. Verify at: https://faisalhanafi.com

---

**Live URLs:**
- Portfolio: https://faisalhanafi.com
- Portfolio API: https://faisalhanafi.com/api/
- CREAMS Demo: https://faisalhanafi.com/creams/demo/

**Instance Details:**
- Region: ap-southeast-1 (Singapore)
- Static IP: 54.169.32.54
- OS: Amazon Linux 2023
- Uptime: Restarted 2026-01-23 ~18:30 SGT

---

## Deployment Verification - 2026-01-23 18:00 SGT

### Auto-Deployment Confirmed Working ✅

**Test Commits:**
- `cddf0ff` - Fixed temp directory creation
- `a7ec45d` - Fixed Windows Java path for CI/CD
- `889ee9c` - Added deployment log

**Verification Results:**
1. ✅ GitHub Actions triggers on every push
2. ✅ Frontend builds successfully (React + Vite)
3. ✅ Backend builds successfully (Spring Boot JAR with Gradle)
4. ✅ Files deployed to Lightsail via SSH
5. ✅ Backend service restarts automatically
6. ✅ Frontend accessible at https://faisalhanafi.com
7. ✅ Systemd service managing backend lifecycle

**Deployment Workflow Time:** ~3-4 minutes per push

**Memory Usage During Deployment:**
- Frontend build: ~200MB (runs on GitHub)
- Backend build: ~1.5GB (runs on GitHub)
- Server impact: Minimal (only file copy + service restart)

**Key Success Factors:**
- Builds happen on GitHub Actions (not on 1GB Lightsail instance)
- Gradle `java.home` path commented out for CI/CD compatibility
- Systemd service with memory limits (300MB max)
- Nginx reverse proxy routing working correctly

**Next Steps (Backend Configuration):**
- Configure production database connection (PostgreSQL/MySQL)
- Update `application-production.yml` with database credentials
- Remove H2 in-memory database dependency
- Test API endpoints after database is configured

**Conclusion:**
The deployment pipeline is fully operational. Every code push now automatically builds and deploys both frontend and backend to Lightsail without manual intervention or full system restarts.
