# StreetSafe – Street Food Trust & Hygiene Platform

Spring Boot 3 · Java 17 · MySQL · Thymeleaf + Bootstrap 5 · ZXing QR codes

## What it does
1. Vendors register a stall with a 14-digit FSSAI number.
2. `LicenseService` checks the format (`^[1-2][0-9]{13}$`) and awards the **FSSAI Verified** badge.
3. Every stall gets a QR code (generated with ZXing, embedded as a Base64 PNG) pointing to `/scan/{id}`.
4. Customers scan it, see the verification status and community rating, and leave a 1-5 star review.

## Prerequisites
- JDK 17 or newer
- Maven 3.9+
- MySQL 8+ running on `localhost:3306`

## Run
```bash
# Windows PowerShell:  $env:DB_USERNAME="root"; $env:DB_PASSWORD="your_password"
export DB_USERNAME=root
export DB_PASSWORD=your_password

mvn spring-boot:run
```
Open http://localhost:8080. The database `streetsafe_db` is created automatically, tables are created by Hibernate,
and `data.sql` loads 10 stalls and 6 reviews (safe to re-run; it uses INSERT IGNORE).

## Scanning the QR code with a real phone
"localhost" means *the phone itself* to a phone, so do one of these:
- Put the phone on the same Wi-Fi and open the dashboard with your computer's IP, e.g. `http://192.168.1.10:8080/vendor/1`, or
- start the app with `STREETSAFE_BASE_URL=http://192.168.1.10:8080`.

## Routes
| Method | URL | Purpose |
|---|---|---|
| GET  | `/` | Home: all stalls + register form (modal) |
| POST | `/vendor/register` | Save vendor, auto-verify FSSAI, redirect to dashboard |
| GET  | `/vendor/{id}` | Vendor dashboard with QR standee |
| GET  | `/scan/{id}` | Public customer trust page (target of the QR code) |
| POST | `/scan/{id}/review` | Save a review, redirect back to `/scan/{id}` |

## Structure
```
pom.xml
src/main/java/com/streetsafe/
  StreetSafeApplication.java
  model/       Vendor, Review, RatingBar
  repository/  VendorRepository, ReviewRepository
  service/     LicenseService, QRCodeService
  controller/  StreetSafeController
src/main/resources/
  application.properties, data.sql
  static/css/streetsafe.css
  templates/   index, vendor-dashboard, customer-scan, fragments, error
```

## Note
If `mvn` reports that the Spring Boot version in `pom.xml` can't be resolved, change the parent version to any current 3.x release.
