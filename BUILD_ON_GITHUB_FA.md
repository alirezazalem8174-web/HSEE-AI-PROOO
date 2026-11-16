# ساخت APK واقعی برای HSE AI PRO

این پروژه برای Build واقعی Android آماده شده است.

Workflow موجود در `.github/workflows/android-apk.yml` به‌صورت خودکار:

1. Java 17 را آماده می‌کند.
2. Android SDK و Android 35 / Build Tools 35 را نصب می‌کند.
3. Gradle 8.7 را آماده می‌کند.
4. پروژه را با `:app:assembleDebug` Build می‌کند.
5. وجود APK را بررسی می‌کند.
6. APK را به‌عنوان Artifact قابل دریافت ذخیره می‌کند.

این روش نیازمند اینترنت در محیط CI است و هیچ API پولی یا سرویس اختصاصی HSE استفاده نمی‌کند. پس از Build، فایل APK از بخش Artifacts همان اجرای Workflow قابل دریافت است.

## نکته

این محیط ChatGPT دسترسی به Android SDK/Gradle برای اجرای Build واقعی ندارد؛ بنابراین Build نهایی باید در یک runner واقعی Android/Gradle انجام شود. Workflow فوق برای همین مرحله آماده شده است.
