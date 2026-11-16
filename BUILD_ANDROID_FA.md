# راهنمای Build واقعی APK

این پروژه برای Android Gradle Plugin 8.6.1 و Gradle 8.7 تنظیم شده و Java 17 را برای source/target می‌خواهد.

در محیط دارای Android SDK و اینترنت، ابتدا Gradle 8.7 دریافت/فعال شود و SDK Platform 35 و Build Tools مناسب نصب باشند. سپس:

```text
gradle :app:assembleDebug
```

خروجی مورد انتظار:

```text
app/build/outputs/apk/debug/app-debug.apk
```

سپس APK باید روی حداقل یک دستگاه Android واقعی نصب و تست شود.

توجه: این بسته عمداً gradle-wrapper.jar ندارد چون در محیط فعلی قابل تولید/دریافت نبود؛ بنابراین ادعای Build انجام‌شده نمی‌شود.
