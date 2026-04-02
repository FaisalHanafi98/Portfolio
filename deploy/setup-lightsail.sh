#!/bin/bash
# Portfolio Lightsail Server Setup Script
# Run this once on a fresh Amazon Linux 2023 instance via browser SSH
# Usage: bash setup-lightsail.sh

set -e

echo "=== Portfolio Server Setup ==="

# 1. System updates
echo "[1/8] Updating system packages..."
sudo dnf update -y

# 2. Install Java 21
echo "[2/8] Installing Java 21..."
sudo dnf install -y java-21-amazon-corretto-headless

# 3. Install PostgreSQL
echo "[3/9] Installing PostgreSQL..."
sudo dnf install -y postgresql15-server
sudo postgresql-setup --initdb
sudo systemctl enable postgresql
sudo systemctl start postgresql

# Create portfolio database and user
echo "Setting up portfolio database..."
sudo -u postgres psql -c "CREATE USER portfolio WITH PASSWORD 'CHANGE_ME';" 2>/dev/null || true
sudo -u postgres psql -c "CREATE DATABASE portfolio OWNER portfolio;" 2>/dev/null || true
echo "  -> Remember to change the password and update /home/ec2-user/.portfolio-env"

# 4. Install Nginx
echo "[4/9] Installing Nginx..."
sudo dnf install -y nginx
sudo systemctl enable nginx

# 5. Install Certbot for HTTPS
echo "[5/9] Installing Certbot..."
sudo dnf install -y certbot python3-certbot-nginx

# 6. Create swap space (1GB — prevents OOM on t3.micro)
echo "[6/9] Setting up swap space..."
if [ ! -f /swapfile ]; then
    sudo dd if=/dev/zero of=/swapfile bs=1M count=1024
    sudo chmod 600 /swapfile
    sudo mkswap /swapfile
    sudo swapon /swapfile
    echo '/swapfile swap swap defaults 0 0' | sudo tee -a /etc/fstab
    echo "Swap created."
else
    echo "Swap already exists, skipping."
fi

# 7. Create web directory
echo "[7/9] Creating web directory..."
sudo mkdir -p /var/www/portfolio
sudo chown -R nginx:nginx /var/www/portfolio

# 8. Deploy Nginx config
echo "[8/9] Configuring Nginx..."
# Remove conflicting configs if they exist
sudo rm -f /etc/nginx/conf.d/default.conf
# The portfolio.conf should be deployed separately via CI/CD or manually

# 9. Deploy systemd service and env file
echo "[9/9] Setting up systemd service..."
# The portfolio.service should be copied to /etc/systemd/system/

echo ""
echo "=== Setup Complete ==="
echo ""
echo "Next steps:"
echo "  1. Create env file with DB credentials:"
echo "     echo 'DB_URL=jdbc:postgresql://localhost:5432/portfolio' > /home/ec2-user/.portfolio-env"
echo "     echo 'DB_USERNAME=portfolio' >> /home/ec2-user/.portfolio-env"
echo "     echo 'DB_PASSWORD=<your-secure-password>' >> /home/ec2-user/.portfolio-env"
echo "     chmod 600 /home/ec2-user/.portfolio-env"
echo "  2. Update PostgreSQL password to match: sudo -u postgres psql -c \"ALTER USER portfolio PASSWORD '<your-secure-password>';\""
echo "  3. Copy deploy/nginx-portfolio.conf to /etc/nginx/conf.d/portfolio.conf"
echo "  4. Copy deploy/portfolio.service to /etc/systemd/system/portfolio.service"
echo "  5. Run: sudo systemctl daemon-reload && sudo systemctl enable portfolio"
echo "  6. Run: sudo nginx -t && sudo systemctl start nginx"
echo "  7. Point DNS A record: faisalhanafi.com -> $(curl -s http://169.254.169.254/latest/meta-data/public-ipv4)"
echo "  8. Run: sudo certbot --nginx -d faisalhanafi.com -d www.faisalhanafi.com"
echo "  9. Set GitHub secrets: LIGHTSAIL_SSH_KEY and LIGHTSAIL_IP"
echo ""
