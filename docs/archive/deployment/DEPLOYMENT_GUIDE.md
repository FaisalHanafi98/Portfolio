# Deployment Guide: Vercel + AWS + Custom Domain + Figma

This guide covers the complete deployment setup for the Portfolio Platform using:
- **Frontend:** Vercel
- **Backend:** AWS (App Runner or Elastic Beanstalk)
- **Database:** AWS RDS PostgreSQL
- **Domain:** Custom domain configuration
- **Design:** Figma integration

---

## Table of Contents

1. [Prerequisites](#1-prerequisites)
2. [Local Development Setup](#2-local-development-setup)
3. [AWS Backend Deployment](#3-aws-backend-deployment)
4. [Vercel Frontend Deployment](#4-vercel-frontend-deployment)
5. [Custom Domain Configuration](#5-custom-domain-configuration)
6. [Figma Integration](#6-figma-integration)
7. [CI/CD Pipeline](#7-cicd-pipeline)
8. [Troubleshooting](#8-troubleshooting)

---

## 1. Prerequisites

### Required Accounts
- [ ] **AWS Account** - [Create AWS Account](https://aws.amazon.com/)
- [ ] **Vercel Account** - [Create Vercel Account](https://vercel.com/signup)
- [ ] **Figma Account** - [Create Figma Account](https://figma.com/)
- [ ] **GitHub Account** - For repository hosting and CI/CD

### Required Tools
```bash
# Java 17+ (for backend)
java -version  # Should show 17 or higher

# Node.js 18+ (for frontend)
node -version  # Should show 18 or higher

# AWS CLI
aws --version

# Vercel CLI
npm install -g vercel

# Docker (optional, for containerized deployment)
docker --version
```

### Install AWS CLI (if not installed)
```bash
# Windows (PowerShell as Administrator)
msiexec.exe /i https://awscli.amazonaws.com/AWSCLIV2.msi

# macOS
brew install awscli

# Linux
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install
```

### Configure AWS CLI
```bash
aws configure
# Enter your:
# - AWS Access Key ID
# - AWS Secret Access Key
# - Default region (e.g., ap-southeast-1 for Singapore)
# - Default output format (json)
```

---

## 2. Local Development Setup

### Backend Setup
```bash
cd backend

# Run with H2 in-memory database
./gradlew bootRun

# Verify it's running
curl http://localhost:8080/api/v1/health
# Should return: {"success":true,"data":{"status":"ok"},...}
```

### Frontend Setup
```bash
cd frontend

# Install dependencies
npm install

# Create local environment file
cp .env.example .env

# Start development server
npm run dev

# Open http://localhost:5173
```

---

## 3. AWS Backend Deployment

You have two recommended options for deploying the Spring Boot backend:

### Option A: AWS App Runner (Recommended - Simplest)

App Runner is the easiest way to deploy containerized applications on AWS.

#### Step 1: Create ECR Repository
```bash
# Create ECR repository for Docker images
aws ecr create-repository \
  --repository-name portfolio-api \
  --region ap-southeast-1

# Get the repository URI (save this)
aws ecr describe-repositories --repository-name portfolio-api
```

#### Step 2: Build and Push Docker Image
```bash
cd backend

# Login to ECR
aws ecr get-login-password --region ap-southeast-1 | \
  docker login --username AWS --password-stdin \
  <YOUR_ACCOUNT_ID>.dkr.ecr.ap-southeast-1.amazonaws.com

# Build Docker image
docker build -t portfolio-api .

# Tag and push
docker tag portfolio-api:latest \
  <YOUR_ACCOUNT_ID>.dkr.ecr.ap-southeast-1.amazonaws.com/portfolio-api:latest

docker push \
  <YOUR_ACCOUNT_ID>.dkr.ecr.ap-southeast-1.amazonaws.com/portfolio-api:latest
```

#### Step 3: Create RDS PostgreSQL Database
```bash
# Create a PostgreSQL database (Free Tier eligible)
aws rds create-db-instance \
  --db-instance-identifier portfolio-db \
  --db-instance-class db.t3.micro \
  --engine postgres \
  --engine-version 15.4 \
  --master-username portfolioadmin \
  --master-user-password YOUR_SECURE_PASSWORD \
  --allocated-storage 20 \
  --publicly-accessible \
  --backup-retention-period 7

# Wait for database to be available (takes 5-10 minutes)
aws rds wait db-instance-available --db-instance-identifier portfolio-db

# Get the endpoint
aws rds describe-db-instances --db-instance-identifier portfolio-db \
  --query 'DBInstances[0].Endpoint.Address' --output text
```

#### Step 4: Create App Runner Service

1. Go to [AWS App Runner Console](https://console.aws.amazon.com/apprunner)
2. Click **Create service**
3. Choose **Container registry** → **Amazon ECR**
4. Select your `portfolio-api` repository
5. Configure:
   - **Service name:** `portfolio-api`
   - **CPU:** 0.25 vCPU (minimum)
   - **Memory:** 0.5 GB (minimum)
   - **Port:** 8080
6. Add Environment Variables:
   ```
   SPRING_PROFILES_ACTIVE = prod
   DATABASE_URL = jdbc:postgresql://<RDS_ENDPOINT>:5432/postgres?user=portfolioadmin&password=YOUR_PASSWORD
   CORS_ALLOWED_ORIGINS = https://yourdomain.com,https://www.yourdomain.com
   ```
7. Click **Create & deploy**

#### Step 5: Note Your App Runner URL
After deployment, you'll get a URL like:
```
https://xxxxxxxxxx.ap-southeast-1.awsapprunner.com
```

### Option B: AWS Elastic Beanstalk (More Control)

#### Step 1: Create Application Bundle
```bash
cd backend

# Build the JAR
./gradlew build -x test

# The JAR will be at: build/libs/portfolio-0.0.1-SNAPSHOT.jar
```

#### Step 2: Create Elastic Beanstalk Application

1. Go to [AWS Elastic Beanstalk Console](https://console.aws.amazon.com/elasticbeanstalk)
2. Click **Create application**
3. Configure:
   - **Application name:** `portfolio-api`
   - **Platform:** Java 17 running on 64bit Amazon Linux 2023
   - **Application code:** Upload your JAR file
4. Configure environment:
   - Choose **Web server environment**
   - Instance type: `t3.micro` (Free Tier)
5. Add Environment Properties:
   ```
   SPRING_PROFILES_ACTIVE = prod
   DATABASE_URL = jdbc:postgresql://<RDS_ENDPOINT>:5432/postgres
   CORS_ALLOWED_ORIGINS = https://yourdomain.com
   ```

---

## 4. Vercel Frontend Deployment

### Step 1: Install Vercel CLI
```bash
npm install -g vercel
```

### Step 2: Login to Vercel
```bash
vercel login
```

### Step 3: Deploy
```bash
cd frontend

# First deployment (will ask questions)
vercel

# For production deployment
vercel --prod
```

### Step 4: Configure Environment Variables

In [Vercel Dashboard](https://vercel.com/dashboard):

1. Go to your project → **Settings** → **Environment Variables**
2. Add:
   ```
   VITE_API_URL = https://your-aws-backend-url.awsapprunner.com/api/v1
   ```

### Step 5: Redeploy with Environment Variables
```bash
vercel --prod
```

---

## 5. Custom Domain Configuration

### Prerequisites
- A registered domain (e.g., from Namecheap, GoDaddy, Cloudflare)
- Access to DNS settings

### Architecture
```
yourdomain.com          → Vercel (Frontend)
www.yourdomain.com      → Vercel (Frontend)
api.yourdomain.com      → AWS App Runner (Backend)
```

### Step 1: Configure Vercel Domain

1. In Vercel Dashboard → Your Project → **Settings** → **Domains**
2. Add your domain: `yourdomain.com`
3. Vercel will provide DNS records to add

### Step 2: Configure DNS Records

Add these records in your domain registrar's DNS settings:

```
# For Vercel (Frontend)
Type    Name    Value                           TTL
A       @       76.76.21.21                     3600
CNAME   www     cname.vercel-dns.com            3600

# For AWS App Runner (Backend API)
CNAME   api     xxxxxxxxxx.ap-southeast-1.awsapprunner.com    3600
```

### Step 3: Configure App Runner Custom Domain

1. Go to AWS App Runner Console → Your Service
2. Click **Custom domains** → **Link domain**
3. Enter: `api.yourdomain.com`
4. AWS will provide a validation record to add to DNS
5. Add the validation CNAME record
6. Wait for validation (usually 5-30 minutes)

### Step 4: Update CORS Configuration

Update your AWS environment variables:
```
CORS_ALLOWED_ORIGINS = https://yourdomain.com,https://www.yourdomain.com
```

### Step 5: Update Frontend Environment

In Vercel, update:
```
VITE_API_URL = https://api.yourdomain.com/api/v1
```

### Step 6: Verify SSL Certificates

Both Vercel and AWS App Runner automatically provision SSL certificates. Verify by visiting:
- `https://yourdomain.com` (should show your portfolio)
- `https://api.yourdomain.com/api/v1/health` (should return JSON)

---

## 6. Figma Integration

### Step 1: Get Figma Access Token

1. Go to [Figma Account Settings](https://www.figma.com/settings)
2. Scroll to **Personal access tokens**
3. Click **Generate new token**
4. Name it: `portfolio-design-tokens`
5. Copy the token (you won't see it again!)

### Step 2: Get Figma File Key

1. Open your Figma design file
2. The URL will be: `https://www.figma.com/file/XXXXX/Your-File-Name`
3. `XXXXX` is your file key

### Step 3: Configure Project

Update `frontend/figma.config.json`:
```json
{
  "figma": {
    "fileKey": "YOUR_FIGMA_FILE_KEY",
    "accessToken": "${FIGMA_ACCESS_TOKEN}"
  }
}
```

### Step 4: Set Environment Variable
```bash
# Add to your shell profile or .env
export FIGMA_ACCESS_TOKEN=your_token_here
```

### Step 5: Sync Design Tokens
```bash
cd frontend
npm run tokens:sync
```

### Using Figma Tokens Studio (Recommended)

For a more robust integration:

1. Install [Tokens Studio for Figma](https://tokens.studio/) plugin
2. In Figma, open Tokens Studio
3. Create your design tokens (colors, typography, spacing)
4. Export as JSON
5. Replace `src/design-tokens/tokens.json` with exported file
6. Run `npm run tokens:sync` to generate CSS

### Design Token Workflow

```
Figma (Design)
    ↓
Tokens Studio Plugin
    ↓
Export tokens.json
    ↓
npm run tokens:sync
    ↓
CSS Variables + Tailwind Config
    ↓
React Components
```

---

## 7. CI/CD Pipeline

### GitHub Actions for Frontend (Vercel)

Create `.github/workflows/frontend.yml`:
```yaml
name: Frontend CI/CD

on:
  push:
    branches: [main]
    paths:
      - 'frontend/**'
  pull_request:
    branches: [main]
    paths:
      - 'frontend/**'

jobs:
  build:
    runs-on: ubuntu-latest
    defaults:
      run:
        working-directory: frontend

    steps:
      - uses: actions/checkout@v4

      - name: Setup Node.js
        uses: actions/setup-node@v4
        with:
          node-version: '20'
          cache: 'npm'
          cache-dependency-path: frontend/package-lock.json

      - name: Install dependencies
        run: npm ci

      - name: Type check
        run: npm run type-check

      - name: Lint
        run: npm run lint

      - name: Build
        run: npm run build

  deploy:
    needs: build
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'

    steps:
      - uses: actions/checkout@v4

      - name: Deploy to Vercel
        uses: amondnet/vercel-action@v25
        with:
          vercel-token: ${{ secrets.VERCEL_TOKEN }}
          vercel-org-id: ${{ secrets.VERCEL_ORG_ID }}
          vercel-project-id: ${{ secrets.VERCEL_PROJECT_ID }}
          working-directory: frontend
          vercel-args: '--prod'
```

### GitHub Actions for Backend (AWS)

Create `.github/workflows/backend.yml`:
```yaml
name: Backend CI/CD

on:
  push:
    branches: [main]
    paths:
      - 'backend/**'
  pull_request:
    branches: [main]
    paths:
      - 'backend/**'

jobs:
  build:
    runs-on: ubuntu-latest
    defaults:
      run:
        working-directory: backend

    steps:
      - uses: actions/checkout@v4

      - name: Setup Java
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'
          cache: 'gradle'

      - name: Build with Gradle
        run: ./gradlew build

      - name: Run tests
        run: ./gradlew test

  deploy:
    needs: build
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'

    steps:
      - uses: actions/checkout@v4

      - name: Configure AWS credentials
        uses: aws-actions/configure-aws-credentials@v4
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: ap-southeast-1

      - name: Login to Amazon ECR
        id: login-ecr
        uses: aws-actions/amazon-ecr-login@v2

      - name: Build and push Docker image
        working-directory: backend
        run: |
          docker build -t portfolio-api .
          docker tag portfolio-api:latest ${{ secrets.ECR_REGISTRY }}/portfolio-api:latest
          docker push ${{ secrets.ECR_REGISTRY }}/portfolio-api:latest

      # App Runner will auto-deploy from ECR with auto-deployment enabled
```

### Required GitHub Secrets

Add these in your GitHub repository → Settings → Secrets:

```
# Vercel
VERCEL_TOKEN
VERCEL_ORG_ID
VERCEL_PROJECT_ID

# AWS
AWS_ACCESS_KEY_ID
AWS_SECRET_ACCESS_KEY
ECR_REGISTRY  (e.g., 123456789.dkr.ecr.ap-southeast-1.amazonaws.com)
```

---

## 8. Troubleshooting

### CORS Errors

**Symptom:** Browser console shows CORS errors

**Solution:**
1. Verify `CORS_ALLOWED_ORIGINS` includes your frontend domain
2. Ensure no trailing slashes in origins
3. Check that the backend is actually running
4. Verify the API URL in frontend matches backend URL

### Database Connection Issues

**Symptom:** Backend fails to start with database errors

**Solution:**
1. Verify RDS security group allows inbound traffic on port 5432
2. Check DATABASE_URL format:
   ```
   jdbc:postgresql://host:5432/dbname?user=username&password=password
   ```
3. Ensure RDS is publicly accessible (or configure VPC properly)

### App Runner Deployment Failures

**Symptom:** App Runner shows deployment failed

**Solution:**
1. Check CloudWatch logs for the service
2. Verify Docker image builds locally
3. Ensure health check endpoint responds correctly
4. Check memory allocation (increase if OOM errors)

### Vercel Build Failures

**Symptom:** Vercel deployment fails

**Solution:**
1. Check build logs in Vercel dashboard
2. Ensure `npm run build` works locally
3. Verify all environment variables are set
4. Check for TypeScript errors with `npm run type-check`

### SSL Certificate Issues

**Symptom:** Browser shows certificate warnings

**Solution:**
1. Wait for certificate provisioning (up to 1 hour)
2. Verify DNS records are correct
3. Check domain validation status in AWS/Vercel

---

## Quick Reference: Environment Variables

### Backend (AWS)
```
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=jdbc:postgresql://host:5432/db?user=x&password=y
CORS_ALLOWED_ORIGINS=https://yourdomain.com,https://www.yourdomain.com
PORT=8080
```

### Frontend (Vercel)
```
VITE_API_URL=https://api.yourdomain.com/api/v1
```

### Local Development
```
# Frontend (.env)
VITE_API_URL=http://localhost:8080/api/v1

# Figma (optional)
FIGMA_ACCESS_TOKEN=your_token
```

---

## Cost Estimation (AWS Free Tier)

| Service | Free Tier | After Free Tier |
|---------|-----------|-----------------|
| App Runner | 2M requests/month | ~$5-10/month |
| RDS PostgreSQL | 750 hours/month (1 year) | ~$15/month |
| ECR | 500MB storage | ~$0.10/GB/month |
| **Total** | **$0** (first year) | **~$20-30/month** |

Vercel: Free for personal projects

---

## Next Steps

1. [ ] Create AWS account and configure CLI
2. [ ] Deploy backend to AWS App Runner
3. [ ] Deploy frontend to Vercel
4. [ ] Configure custom domain
5. [ ] Set up Figma integration (optional)
6. [ ] Configure CI/CD pipelines
7. [ ] Test end-to-end functionality

---

**Need Help?**

- AWS Documentation: https://docs.aws.amazon.com/
- Vercel Documentation: https://vercel.com/docs
- Figma API: https://www.figma.com/developers/api

Created for the Portfolio Platform project.
