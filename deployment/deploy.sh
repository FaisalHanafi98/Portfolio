#!/bin/bash
# Manual deployment script for Portfolio
# Usage: ./deploy.sh

set -e

LIGHTSAIL_IP="54.169.32.54"
SSH_KEY="../LightsailDefaultKey-ap-southeast-1.pem"

echo "🚀 Starting Portfolio Deployment..."

# Step 1: Build frontend
echo "📦 Building frontend..."
cd ../frontend
npm run build
cd ../deployment

# Step 2: Build backend JAR locally (if you have Java 17)
echo "📦 Building backend JAR..."
cd ../backend
if command -v ./gradlew &> /dev/null; then
    ./gradlew clean bootJar --no-daemon
    echo "✅ Backend JAR built successfully"
else
    echo "⚠️  Gradle not found. Skipping backend build."
    echo "   Backend JAR should be built via GitHub Actions."
fi
cd ../deployment

# Step 3: Deploy frontend
echo "🌐 Deploying frontend..."
ssh -i "$SSH_KEY" ec2-user@$LIGHTSAIL_IP "mkdir -p /tmp/portfolio-frontend"
scp -i "$SSH_KEY" -r ../frontend/dist/* ec2-user@$LIGHTSAIL_IP:/tmp/portfolio-frontend/
ssh -i "$SSH_KEY" ec2-user@$LIGHTSAIL_IP "sudo rm -rf /var/www/portfolio/* && sudo cp -r /tmp/portfolio-frontend/* /var/www/portfolio/ && sudo chown -R nginx:nginx /var/www/portfolio"
echo "✅ Frontend deployed"

# Step 4: Deploy backend JAR (if exists)
if [ -f "../backend/build/libs/portfolio-backend-0.0.1-SNAPSHOT.jar" ]; then
    echo "☕ Deploying backend JAR..."
    scp -i "$SSH_KEY" ../backend/build/libs/*.jar ec2-user@$LIGHTSAIL_IP:/home/ec2-user/portfolio-backend.jar
    ssh -i "$SSH_KEY" ec2-user@$LIGHTSAIL_IP "sudo systemctl restart portfolio-backend"
    echo "✅ Backend deployed and restarted"
else
    echo "⚠️  Backend JAR not found. Skipping backend deployment."
fi

echo ""
echo "✅ Deployment complete!"
echo "🌐 Visit: https://faisalhanafi.com"
echo ""
echo "📝 To check backend status: ssh -i $SSH_KEY ec2-user@$LIGHTSAIL_IP 'sudo systemctl status portfolio-backend'"
echo "📝 To view backend logs: ssh -i $SSH_KEY ec2-user@$LIGHTSAIL_IP 'sudo journalctl -u portfolio-backend -f'"
