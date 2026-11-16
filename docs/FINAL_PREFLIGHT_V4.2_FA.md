# گزارش بازبینی پیش از Build — HSE AI PRO 4.2

## اصلاحات این مرحله
- تنظیم صریح Java 17 برای سازگاری پایدارتر با Android Gradle Plugin.
- افزایش versionCode به 42 و versionName به 4.2.0.
- تقویت Restore با جلوگیری از ورودی ZIP تکراری.
- محدودیت حجم هر ورودی و حجم کل Backup برای کاهش ریسک Zip Bomb.
- کنترل بهتر ساخت مسیرهای بازیابی.
- مستندسازی کامل موانع Build واقعی.

## موارد بررسی‌شده
- Manifest بدون INTERNET permission.
- نبود وابستگی‌های OpenAI/Firebase/Retrofit/OkHttp در کد اپلیکیشن.
- دیتابیس محلی SQLite.
- PDFBox محلی برای استخراج متن.
- عملیات سنگین در Executor پس‌زمینه.
- Backup/Restore با اعتبارسنجی SQLite.
- Migration دیتابیس تا نسخه 6.
- اعلان‌های Android و دریافت BOOT_COMPLETED.

## نتیجه
کد و بسته برای ورود به مرحله Build آماده‌سازی شده‌اند، اما APK تا زمانی که Android SDK/Gradle واقعی در محیط Build موجود نباشد، قابل ادعای تست‌شده نیست.
