# مسیر نهایی Build — HSE AI PRO 4.4

این بسته برای Build واقعی APK آماده شده است.

## مسیر ترجیحی
GitHub Actions فایل `.github/workflows/android-build.yml` را اجرا می‌کند و:
- JDK 17
- Android SDK Platform 35
- Build Tools 35.0.0
- Gradle 8.7
را آماده کرده و سپس `:app:assembleDebug` را اجرا می‌کند.

خروجی مورد انتظار:
`app/build/outputs/apk/debug/app-debug.apk`

## نکته مهم
اجرای این Workflow نیازمند قرارگرفتن پروژه در یک مخزن GitHub و اجرای Workflow است. این بسته خودش APK از پیش ساخته‌شده نیست.
