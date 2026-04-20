#!/bin/bash
# One-time server setup for Portfolio deployment
# Run this once to configure systemd service and Nginx

set -e

LIGHTSAIL_IP="54.169.32.54"
SSH_KEY="../LightsailDefaultKey-ap-southeast-1.pem"

echo "⚙️  Setting up Portfolio on Lightsail..."

# Step 1: Create backend directory
echo "📁 Creating backend directory..."
ssh -i "$SSH_KEY" ec2-user@$LIGHTSAIL_IP "mkdir -p /home/ec2-user"

# Step 2: Copy and enable systemd service
echo "🔧 Installing systemd service..."
scp -i "$SSH_KEY" portfolio-backend.service ec2-user@$LIGHTSAIL_IP:/tmp/
ssh -i "$SSH_KEY" ec2-user@$LIGHTSAIL_IP << 'EOF'
    sudo cp /tmp/portfolio-backend.service /etc/systemd/system/
    sudo systemctl daemon-reload
    sudo systemctl enable portfolio-backend
    echo "✅ Systemd service installed"
EOF

# Step 3: Update Nginx configuration
echo "🌐 Updating Nginx configuration..."
scp -i "$SSH_KEY" nginx-faisalhanafi.conf ec2-user@$LIGHTSAIL_IP:/tmp/
ssh -i "$SSH_KEY" ec2-user@$LIGHTSAIL_IP << 'EOF'
    sudo cp /tmp/nginx-faisalhanafi.conf /etc/nginx/conf.d/faisalhanafi.conf
    sudo nginx -t
    sudo systemctl reload nginx
    echo "✅ Nginx configuration updated"
EOF

echo ""
echo "✅ Server setup complete!"
echo ""
echo "Next steps:"
echo "1. Deploy your code:"
echo "   - Push to GitHub (triggers automatic deployment)"
echo "   - OR run: ./deploy.sh (manual deployment)"
echo ""
echo "2. Useful commands:"
echo "   - Check backend: ssh -i $SSH_KEY ec2-user@$LIGHTSAIL_IP 'sudo systemctl status portfolio-backend'"
echo "   - View logs: ssh -i $SSH_KEY ec2-user@$LIGHTSAIL_IP 'sudo journalctl -u portfolio-backend -f'"
echo "   - Restart backend: ssh -i $SSH_KEY ec2-user@$LIGHTSAIL_IP 'sudo systemctl restart portfolio-backend'"
