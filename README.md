# MiniCRM

Java (Spring Boot 2.7 REST API) + React (Vite) ile yazılmış küçük ve bağımsız bir CRM uygulaması.

## Özellikler

- **Müşteri yönetimi** — arama, durum (potansiyel / aktif / pasif / kayıp), CRUD
- **Kişiler (contacts)** — müşteri başına iletişim kişileri
- **Fırsat pipeline'ı** — New → Qualified → Proposal → Won / Lost kanban görünümü
- **Aktivite geçmişi** — arama / e-posta / görüşme / not kayıtları
- **Dashboard** — müşteri ve pipeline istatistikleri, son aktiviteler

## Çalıştırma

### Backend (Java 8+, Maven)

```bash
cd backend
mvn spring-boot:run
```

API `http://localhost:8080` üzerinde başlar. Hafızada H2 veritabanı kullanır ve örnek veriyle açılır.
H2 konsolu: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:crm`, kullanıcı: `sa`, şifre boş)

API uçları örneği:

| Metot   | Yol                              | Açıklama                     |
|---------|----------------------------------|------------------------------|
| GET     | `/api/customers?q=`              | Müşteri listesi / arama      |
| POST    | `/api/customers`                 | Müşteri ekle                 |
| GET     | `/api/customers/{id}`            | Müşteri detayı               |
| GET     | `/api/customers/{id}/contacts`   | Müşterinin kişileri          |
| POST    | `/api/customers/{id}/deals`      | Fırsat aç                    |
| PATCH   | `/api/deals/{id}/stage`          | Fırsat aşaması değiştir      |
| GET     | `/api/stats`                     | Dashboard istatistikleri     |

### Frontend (Node 18+)

```bash
cd frontend
npm install
npm run dev
```

Uygulama `http://localhost:5173` üzerinde açılır; `/api` isteklerini vite proxy'si 8080'e yollar.

## Teknoloji

- Java 8, Spring Boot 2.7.18 (Web, Data JPA, Validation), H2
- React 18, React Router 6, Vite 5

> Not: Demo amaçlı olduğu için kalıcı veritabanı ve kimlik doğrulama yoktur.
