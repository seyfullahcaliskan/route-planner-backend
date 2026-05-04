# Backend Migration Guide

Bu kılavuz mevcut backend'i optimize edilmiş versiyona yükseltmek için gereken adımları içerir.

---

## 🚨 ÖNCELİK 1: Credentials Güvenliği

### 1. Environment Variables Oluştur

`.env` dosyası oluştur (proje root'da):
```bash
cp backend-optimized/.env.example .env
```

`.env` içeriğini düzenle:
```env
DATABASE_URL=jdbc:postgresql://193.111.78.174:5432/route_planner
DATABASE_USER=postgres
DATABASE_PASSWORD=SoparDesen123.%!

GOOGLE_MAPS_API_KEY=AIzaSyBD9WT82o0msVxh9RxdKsL9nW5U7LU_uy4

PORT=8080
JPA_SHOW_SQL=false
LOG_LEVEL=INFO
```

> ⚠️ **ASLA `.env` dosyasını commit etme!** `.gitignore` zaten eklenmiş.

### 2. Eski `application.properties` SİL

```bash
rm src/main/resources/application.properties
```

### 3. Yeni `application.yml` Kopyala

```bash
cp backend-optimized/src/main/resources/application.yml src/main/resources/
```

---

## 📦 ADIM 2: Package Rename (IntelliJ IDEA)

### Mevcut Hatalı Yapı:
```
com.routeplanner.backend.Algorithm    ❌
com.routeplanner.backend.Controller   ❌
com.routeplanner.backend.DTO           ❌
```

### Hedef Doğru Yapı:
```
com.routeplanner.backend.algorithm     ✅
com.routeplanner.backend.controller    ✅
com.routeplanner.backend.dto           ✅
```

### IntelliJ'de Nasıl Yapılır:

1. **Algorithm → algorithm:**
    - `src/main/java/com/routeplanner/backend/Algorithm` klasörüne sağ tık
    - **Refactor → Rename** seç
    - `algorithm` yaz (lowercase)
    - **Refactor** butonuna tıkla

2. Aynısını şu klasörler için tekrarla:
    - `Config` → `config`
    - `Controller` → `controller`
    - `DTO` → `dto`
    - `Entity` → `entity`
    - `Enums` → `enums`
    - `Exception` → `exception`
    - `Mapper` → `mapper`
    - `Repository` → `repository`
    - `Security` → `security`
    - `Service` → `service`

3. **Build → Rebuild Project** yap

---

## 🗄️ ADIM 3: Flyway Migration Cleanup

### Silmen Gereken Migration'lar (DUPLICATE):

```bash
# Bu dosyaları SİL - duplicate'lar
rm src/main/resources/db/migration/V10__create_route_execution.sql
rm src/main/resources/db/migration/V11__create_route_reoptimization_history.sql
rm src/main/resources/db/migration/V12__create_navigation_event.sql
rm src/main/resources/db/migration/V13__create_device_token.sql
rm src/main/resources/db/migration/V14__create_notification_log.sql
rm src/main/resources/db/migration/V15_create_user_saved_place.sql
rm src/main/resources/db/migration/V16_create_initial_schema.sql
rm src/main/resources/db/migration/V7__create_address_import_batch.sql
```

### Yeni Temiz Migration'ları Kopyala:

```bash
rm -rf src/main/resources/db/migration
cp -r backend-optimized/src/main/resources/db/migration src/main/resources/
```

**Sonuç:** V1-V10 arası temiz, duplicate yok.

---

## 🔧 ADIM 4: Güncellenmiş Dosyaları Kopyala

### Exception Handling:
```bash
cp backend-optimized/src/main/java/com/routeplanner/backend/exception/*.java \
   src/main/java/com/routeplanner/backend/exception/
```

### Algorithm:
```bash
cp backend-optimized/src/main/java/com/routeplanner/backend/algorithm/RouteOptimizer.java \
   src/main/java/com/routeplanner/backend/algorithm/
```

### Service:
```bash
cp backend-optimized/src/main/java/com/routeplanner/backend/service/impl/GoogleRouteOptimizationEngineService.java \
   src/main/java/com/routeplanner/backend/service/impl/

cp backend-optimized/src/main/java/com/routeplanner/backend/service/impl/RoutePlanServiceImpl.java \
   src/main/java/com/routeplanner/backend/service/impl/
```

### Entity (Lombok):
```bash
cp backend-optimized/src/main/java/com/routeplanner/backend/entity/BaseEntity.java \
   src/main/java/com/routeplanner/backend/entity/

cp backend-optimized/src/main/java/com/routeplanner/backend/entity/UserEntity.java \
   src/main/java/com/routeplanner/backend/entity/

cp backend-optimized/src/main/java/com/routeplanner/backend/entity/RouteStopEntity.java \
   src/main/java/com/routeplanner/backend/entity/
```

---

## ✅ ADIM 5: Test & Verify

### 1. Compile Check
```bash
./mvnw clean compile
```

**Beklenen:** Compile hatası olmamalı.

### 2. Migration Check
```bash
./mvnw flyway:info
```

**Beklenen:** V1-V10 migration'lar görünmeli, duplicate yok.

### 3. Run Application
```bash
./mvnw spring-boot:run
```

**Beklenen:** Uygulama başlamalı, hata logları olmamalı.

### 4. Swagger Test
http://localhost:8080/swagger-ui.html

**Test endpoint:**
- POST `/api/v1/route-plans` → Rota planı oluştur
- POST `/api/v1/route-plans/{id}/stops` → Durak ekle
- POST `/api/v1/route-plans/{id}/reoptimize` → Optimize et

---

## 🎯 Değişiklik Özeti

### ✅ Yapılanlar:

1. **Güvenlik:**
    - Credentials environment variable'a taşındı
    - `.env.example` şablonu oluşturuldu
    - `.gitignore` güncellendi

2. **Mimari:**
    - Package isimleri Java convention'ına uygun (lowercase)
    - Custom exception'lar eklendi (ResourceNotFoundException, OptimizationException, GeocodingException)
    - GlobalExceptionHandler iyileştirildi

3. **Algoritma:**
    - Google API 25+ durak için local fallback eklendi
    - API key yoksa otomatik local optimizer devreye girer

4. **Database:**
    - Flyway migration'lar temizlendi (V1-V10)
    - Duplicate'lar silindi
    - ON DELETE CASCADE/SET NULL eksileri düzeltildi

5. **Entity:**
    - Lombok eklendi (@Getter, @Setter)
    - BaseEntity @PrePersist/@PreUpdate ile otomatik timestamp
    - Manuel getter/setter'lar silindi

6. **Service:**
    - RuntimeException → Custom exceptions
    - @RequiredArgsConstructor ile constructor injection

---

## 🚫 YAPMA!

- ❌ `.env` dosyasını commit etme
- ❌ Eski migration'ları geri getirme
- ❌ `application.properties` kullanma
- ❌ Package isimlerini PascalCase'e çevirme

---

## ❓ Sorun Çözümleme

### "Cannot find symbol: ResourceNotFoundException"
→ Exception dosyalarını kopyalamayı unuttun, ADIM 4'e dön.

### "Flyway found more than one migration with version X"
→ Eski duplicate migration'ları silmedin, ADIM 3'e dön.

### "Cannot autowire RouteOptimizer"
→ Package rename yapmadın, ADIM 2'ye dön.

### "Could not resolve placeholder 'DATABASE_PASSWORD'"
→ `.env` dosyası yok veya yanlış konumda, ADIM 1'e dön.

---

Tüm adımlar tamamlandıktan sonra backend temiz, güvenli, ve ölçeklenebilir hale gelecek.