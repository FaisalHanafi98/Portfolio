# Portfolio Deployment Guide

## Deployment Strategies

### 1. Automatic Deployment (Recommended) ✅

**Push to GitHub → GitHub Actions builds → Deploys automatically**

```bash
git add .
git commit -m "Update portfolio"
git push origin main
```

GitHub Actions will:
- Build frontend (React)
- Build backend (Spring Boot JAR)
- Deploy to Lightsail
- Restart backend service

**Setup GitHub Secrets:**
1. Go to: https://github.com/Faisalhfdz/faisalhanafi-portfolio/settings/secrets/actions
2. Add these secrets:
   - `LIGHTSAIL_SSH_KEY`: Content of `LightsailDefaultKey-ap-southeast-1.pem`
   - `LIGHTSAIL_IP`: `54.169.32.54`

---

### 2. Manual Deployment

```bash
cd deployment
./deploy.sh
```

This script:
- Builds frontend locally
- Builds backend JAR (if Java 17 installed)
- Deploys via SSH
- Restarts backend service

---

### 3. Update Code WITHOUT Full Restart

**Frontend only (instant):**
```bash
cd frontend
npm run build
scp -i ../LightsailDefaultKey-ap-southeast-1.pem -r dist/* ec2-user@54.169.32.54:/tmp/portfolio-frontend/
ssh -i ../LightsailDefaultKey-ap-southeast-1.pem ec2-user@54.169.32.54 "sudo cp -r /tmp/portfolio-frontend/* /var/www/portfolio/"
```

**Backend only (10 second restart):**
```bash
# After building JAR locally
scp -i LightsailDefaultKey-ap-southeast-1.pem backend/build/libs/*.jar ec2-user@54.169.32.54:/home/ec2-user/portfolio-backend.jar
ssh -i LightsailDefaultKey-ap-southeast-1.pem ec2-user@54.169.32.54 "sudo systemctl restart portfolio-backend"
```

**Check backend status:**
```bash
ssh -i LightsailDefaultKey-ap-southeast-1.pem ec2-user@54.169.32.54 "sudo systemctl status portfolio-backend"
```

**View backend logs (live):**
```bash
ssh -i LightsailDefaultKey-ap-southeast-1.pem ec2-user@54.169.32.54 "sudo journalctl -u portfolio-backend -f"
```

---

## Initial Server Setup (One-Time)

Run this once to configure systemd service and Nginx:

```bash
cd deployment
chmod +x setup-server.sh
./setup-server.sh
```

---

## Architecture

```
faisalhanafi.com
│
├─ /              → React SPA (Nginx serves static files)
├─ /api/*         → Spring Boot backend (Nginx proxies to :8080)
└─ /creams/*      → CREAMS Laravel app (Nginx proxies to PHP-FPM)
```

### Services on Server

| Service | Port | Process | Control |
|---------|------|---------|---------|
| Nginx | 80, 443 | Web server | `sudo systemctl restart nginx` |
| Portfolio Backend | 8080 | Spring Boot | `sudo systemctl restart portfolio-backend` |
| PHP-FPM | unix socket | Laravel | `sudo systemctl restart php-fpm` |

---

## Troubleshooting

**Backend not starting:**
```bash
# Check logs
sudo journalctl -u portfolio-backend -n 50

# Check if JAR exists
ls -lh /home/ec2-user/portfolio-backend.jar

# Manually run JAR to see errors
java -jar /home/ec2-user/portfolio-backend.jar
```

**Frontend not updating:**
```bash
# Check Nginx config
sudo nginx -t

# Reload Nginx
sudo systemctl reload nginx

# Check frontend files
ls -la /var/www/portfolio/
```

**Memory issues:**
```bash
# Check memory usage
free -h

# Check Java process memory
ps aux | grep java

# Restart backend (frees memory)
sudo systemctl restart portfolio-backend
```

---

## Upgrading Instance (if needed)

If the t3.micro (1GB RAM) is too small:

1. Go to Lightsail console
2. Click instance → Snapshots → Create snapshot
3. Create new instance from snapshot with t3.small (2GB RAM)
4. Update DNS to new static IP

**Recommended:** t3.small ($10/month) for multiple applications

---

## Quick Reference

| Task | Command |
|------|---------|
| Deploy via GitHub | `git push origin main` |
| Deploy manually | `./deployment/deploy.sh` |
| Restart backend | `ssh ... "sudo systemctl restart portfolio-backend"` |
| View backend logs | `ssh ... "sudo journalctl -u portfolio-backend -f"` |
| Check backend status | `ssh ... "sudo systemctl status portfolio-backend"` |
| Reload Nginx | `ssh ... "sudo systemctl reload nginx"` |
| Update frontend only | See "Frontend only" above |

---

**Live Site:** https://faisalhanafi.com
**Backend API:** https://faisalhanafi.com/api/
**CREAMS Demo:** https://faisalhanafi.com/creams/demo/
