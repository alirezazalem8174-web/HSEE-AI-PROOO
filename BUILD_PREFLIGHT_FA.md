# پیش‌نیاز Build واقعی APK — HSE AI PRO 4.3

این نسخه برای Build واقعی آماده‌سازی شده است.

## وضعیت کد
- Gradle Android Plugin: 8.6.1
- compileSdk/targetSdk: 35
- minSdk: 26
- Java source/target: 17
- PDFBox Android: 2.0.27.0
- مجوز INTERNET در Manifest وجود ندارد.
- قابلیت‌های اصلی آفلاین هستند.

## مانع فعلی محیط ساخت
برای تولید APK باید یک محیط Android Build واقعی در اختیار باشد که حداقل شامل موارد زیر باشد:
1. JDK 17
2. Gradle 8.7 یا Gradle Wrapper سازگار
3. Android SDK Platform 35
4. Android SDK Build-Tools سازگار
5. دسترسی به مخزن Google/Maven فقط در زمان دریافت وابستگی‌های Build، نه در اجرای برنامه

در محیط فعلی توسعه، Android SDK و Gradle نصب/قابل دسترسی نیستند؛ بنابراین نباید APK ساختگی یا تست‌نشده تحویل داده شود.

## تست‌های لازم بعد از فراهم‌شدن محیط Build
- `./gradlew assembleDebug`
- نصب APK روی Android 8+ و Android 13+
- ورود PDF متنی و PDF خراب
- جلوگیری از PDF تکراری
- جست‌وجوی فارسی
- Backup و Restore
- یادداشت و یادآوری
- Restart دستگاه و بازتنظیم یادآوری‌ها
- ماتریس ریسک 5×5
- ثبت حادثه و شبه‌حادثه
- بررسی عدم نیاز به اینترنت هنگام اجرای برنامه
