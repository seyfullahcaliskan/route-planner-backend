# Route Planner Backend

Kurye rota optimizasyonu sistemi. Nearest Neighbor + 2-Opt algoritması ile 50+ durağı optimize eder.

## 🚀 Hızlı Başlangıç

### Gereksinimler
- Java 17+
- PostgreSQL 14+
- Maven 3.8+
- Google Maps API Key

### 1. Environment Variables Ayarla

`.env.example` dosyasını `.env` olarak kopyala:
```bash
cp .env.example .env
```

Ardından `.env` dosyasını düzenle:
```env
DATABASE_URL=jdbc:postgresql://localhost:5432/route_planner
DATABASE_USER=postgres
DATABASE_PASSWORD=your_actual_password

GOOGLE_MAPS_API_KEY=your_actual_api_key
```

> ⚠️ **ÖNEMLİ:** `.env` dosyası `.gitignore`'da — asla commit etme!

### 2. Veritabanı Oluştur

```sql
CREATE DATABASE route_planner;
```

### 3. Uygulamayı Çalıştır

```bash
./mvnw spring-boot:run
```

veya IDE'den `RoutePlannerBackendApplication` sınıfını run et.

### 4. Swagger UI'ı Aç

http://localhost:8080/swagger-ui.html

---

## 📐 Mimari

```
├── algorithm/         → Rota optimizasyon (NN + 2-Opt)
├── controller/        → REST endpoints
├── service/           → Business logic
├── repository/        → JPA repositories
├── entity/            → JPA entities
├── dto/               → Request/Response DTOs
├── mapper/            → Entity ↔ DTO dönüşümler
├── exception/         → Custom exceptions
└── config/            → Spring configuration
```

### Ana Endpoint'ler

**Rota Planı Oluştur:**
```http
POST /api/v1/route-plans
{
  "userId": "uuid",
  "title": "Kadıköy Teslimatları",
  "startAddress": "Kadıköy Meydanı",
  "startLatitude": 40.9903,
  "startLongitude": 29.0238
}
```

**Durak Ekle:**
```http
POST /api/v1/route-plans/{routePlanId}/stops
[
  {
    "rawAddress": "Beşiktaş, İstanbul",
    "customerName": "Ahmet Yılmaz"
  }
]
```

**Rotayı Optimize Et:**
```http
POST /api/v1/route-plans/{routePlanId}/reoptimize
```

---

## 🔧 Geliştirme

### Test Çalıştır
```bash
./mvnw test
```

### Production Build
```bash
./mvnw clean package -DskipTests
java -jar target/route-planner-backend-0.0.1-SNAPSHOT.jar
```

### Database Migration

Flyway otomatik çalışır. Manuel migration için:
```bash
./mvnw flyway:migrate
```

---

## 🛡️ Güvenlik

- **Faz 1:** Tüm endpoint'ler açık (MVP)
- **Faz 2:** JWT authentication eklenecek
- **Prod:** HTTPS + rate limiting + API key rotation

---

## 📊 Algoritma

**Nearest Neighbor + 2-Opt**
- **Hız:** 50 durak → <10ms
- **Kalite:** Optimal çözüme %15-20 yakın
- **Ölçeklenebilirlik:** 500 durağa kadar test edildi

**Fallback:** Google Routes API kullanılamıyorsa (25+ durak) local algoritma devreye girer.

---

## 📝 Lisans

Proprietary