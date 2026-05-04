@echo off
if exist "%~dp0gradle\wrapper\gradle-wrapper.jar" (
  java -jar "%~dp0gradle\wrapper\gradle-wrapper.jar" %*
) else (
  echo El gradle-wrapper.jar no esta incluido. Abra el proyecto en Android Studio y sincronice Gradle.
  exit /b 1
)
