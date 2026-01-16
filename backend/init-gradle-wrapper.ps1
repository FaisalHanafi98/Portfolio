# PowerShell script to download Gradle Wrapper
$gradleVersion = "8.5"
$wrapperUrl = "https://services.gradle.org/distributions/gradle-$gradleVersion-bin.zip"
$wrapperJarUrl = "https://raw.githubusercontent.com/gradle/gradle/v$gradleVersion/gradle/wrapper/gradle-wrapper.jar"

Write-Host "Downloading Gradle Wrapper..." -ForegroundColor Green

# Create wrapper directory
$wrapperDir = "gradle\wrapper"
New-Item -ItemType Directory -Force -Path $wrapperDir | Out-Null

# Download gradle-wrapper.jar
Write-Host "Downloading gradle-wrapper.jar..."
Invoke-WebRequest -Uri $wrapperJarUrl -OutFile "$wrapperDir\gradle-wrapper.jar"

Write-Host "Gradle wrapper downloaded successfully!" -ForegroundColor Green
Write-Host "You can now run: .\gradlew.bat build -x test" -ForegroundColor Yellow
