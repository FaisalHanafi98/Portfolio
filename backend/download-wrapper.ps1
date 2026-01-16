# Download Gradle Wrapper JAR directly
$wrapperDir = "gradle\wrapper"
New-Item -ItemType Directory -Force -Path $wrapperDir | Out-Null

Write-Host "Downloading Gradle Wrapper JAR..." -ForegroundColor Cyan

# Official Gradle wrapper jar URL (version-agnostic)
$jarUrl = "https://raw.githubusercontent.com/gradle/gradle/master/gradle/wrapper/gradle-wrapper.jar"

try {
    Invoke-WebRequest -Uri $jarUrl -OutFile "$wrapperDir\gradle-wrapper.jar"
    Write-Host "✓ gradle-wrapper.jar downloaded successfully!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Now you can run:" -ForegroundColor Yellow
    Write-Host "  .\gradlew.bat build -x test" -ForegroundColor White
} catch {
    Write-Host "✗ Failed to download. Trying alternative method..." -ForegroundColor Red

    # Alternative: Download a specific stable version
    $altUrl = "https://services.gradle.org/distributions/gradle-8.5-bin.zip"
    $zipPath = "gradle-temp.zip"

    Write-Host "Downloading Gradle distribution..." -ForegroundColor Cyan
    Invoke-WebRequest -Uri $altUrl -OutFile $zipPath

    Write-Host "Extracting..." -ForegroundColor Cyan
    Expand-Archive -Path $zipPath -DestinationPath "gradle-temp" -Force

    Copy-Item "gradle-temp\gradle-8.5\lib\gradle-wrapper-8.5.jar" "$wrapperDir\gradle-wrapper.jar"

    # Cleanup
    Remove-Item $zipPath
    Remove-Item "gradle-temp" -Recurse

    Write-Host "✓ Gradle wrapper setup complete!" -ForegroundColor Green
}
