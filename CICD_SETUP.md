# CI/CD Setup Guide - Automated Deployment

## Overview
This setup enables automatic deployment to your server (65.2.171.45) whenever you push code to GitHub.

## 🚀 Quick Setup

### Step 1: Prepare Your Server (One-time setup)

1. **Upload and run server setup script:**
```bash
# Upload setup-server.sh to your server
scp setup-server.sh ubuntu@65.2.171.45:/home/ubuntu/

# Run on server
ssh ubuntu@65.2.171.45
chmod +x setup-server.sh
./setup-server.sh
```

### Step 2: Generate SSH Key for GitHub Actions

1. **On your server, generate SSH key:**
```bash
ssh ubuntu@65.2.171.45
ssh-keygen -t rsa -b 4096 -f ~/.ssh/github_actions -N ""
cat ~/.ssh/github_actions.pub >> ~/.ssh/authorized_keys
cat ~/.ssh/github_actions  # Copy this private key
```

### Step 3: Setup GitHub Repository

1. **Create GitHub repository** (if not already done)
2. **Add GitHub Secrets** (Repository Settings → Secrets and variables → Actions):
   - `SERVER_HOST`: `65.2.171.45`
   - `SERVER_USER`: `ubuntu`
   - `SERVER_SSH_KEY`: (paste the private key from step 2)

### Step 4: Push Your Code

```bash
# Initialize git (if not done)
git init
git add .
git commit -m "Initial commit with CI/CD setup"

# Add remote and push
git remote add origin https://github.com/yourusername/mitra.git
git branch -M main
git push -u origin main
```

## 🔄 How It Works

### Automatic Deployment Triggers:
- **Push to main/master branch** → Automatic deployment
- **Manual trigger** → Via GitHub Actions tab

### Deployment Process:
1. **Build** - Maven builds the JAR file
2. **Test** - Runs tests (currently skipped)
3. **Deploy** - Copies JAR to server
4. **Restart** - Stops old app, starts new app
5. **Verify** - Checks if app is running

### Deployment Steps:
```
GitHub Push → GitHub Actions → Build JAR → Deploy to Server → Restart App
```

## 📁 File Structure

### Local (GitHub Repository):
```
mitra/
├── .github/workflows/deploy.yml    # CI/CD pipeline
├── setup-server.sh                 # Server setup script
├── CICD_SETUP.md                   # This guide
├── src/                            # Application source
├── pom.xml                         # Maven config
└── ...
```

### Server (/home/ubuntu/mitra/):
```
mitra/
├── mitra-0.0.1-SNAPSHOT.jar       # Application JAR
├── application.properties          # Production config
├── start.sh                        # Start script
├── stop.sh                         # Stop script
├── mitra.pid                       # Process ID
├── logs/mitra.log                  # Application logs
└── data/mitra.mv.db                # H2 database
```

## 🔧 Management Commands

### View Deployment Status:
- **GitHub**: Repository → Actions tab
- **Server logs**: `ssh ubuntu@65.2.171.45 'tail -f /home/ubuntu/mitra/logs/mitra.log'`

### Manual Server Management:
```bash
ssh ubuntu@65.2.171.45
cd /home/ubuntu/mitra

# Start application
./start.sh

# Stop application  
./stop.sh

# View logs
tail -f logs/mitra.log

# Check status
ps aux | grep mitra
```

### Manual Deployment Trigger:
1. Go to GitHub repository
2. Actions tab → Deploy to Server
3. Click "Run workflow"

## 🛠️ Troubleshooting

### Deployment Fails:
1. **Check GitHub Actions logs** in repository
2. **Check server logs**: `tail -f /home/ubuntu/mitra/logs/mitra.log`
3. **Verify SSH access**: Test SSH connection manually
4. **Check server resources**: `df -h` and `free -h`

### Application Won't Start:
1. **Check Java version**: `java -version` (should be 17+)
2. **Check port availability**: `netstat -tlnp | grep 8080`
3. **Check permissions**: `ls -la /home/ubuntu/mitra/`
4. **Manual start**: `cd /home/ubuntu/mitra && ./start.sh`

### SSH Issues:
1. **Verify SSH key** in GitHub secrets
2. **Check authorized_keys** on server
3. **Test connection**: `ssh -i ~/.ssh/github_actions ubuntu@65.2.171.45`

## 🔒 Security Notes

- SSH key is used only for deployment
- Application runs on HTTP (consider HTTPS for production)
- Database is file-based H2 (consider PostgreSQL for production)
- Firewall allows only port 8080

## 🎯 Benefits

✅ **Automatic deployment** on every code push
✅ **Zero-downtime deployment** with backup and rollback
✅ **Build verification** before deployment
✅ **Deployment history** in GitHub Actions
✅ **Easy rollback** if deployment fails
✅ **Consistent environment** across deployments

## 📈 Next Steps (Optional)

1. **Add tests** to the pipeline
2. **Setup staging environment** for testing
3. **Add database migrations** handling
4. **Configure HTTPS** with SSL certificate
5. **Add monitoring** and alerting
6. **Setup log aggregation** (ELK stack)

---

**After setup, every `git push` will automatically deploy to http://65.2.171.45:8080!**