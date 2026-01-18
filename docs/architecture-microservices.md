# 🏗️ Architecture Microservices - Cinéma

## 🎯 Objectifs

### 1. Panier pour Utilisateurs Non Connectés (Guest Cart)
✅ Permettre la navigation et sélection de places **sans authentification**  
✅ Forcer la connexion/inscription **avant le paiement**  
✅ **Restaurer le panier** après connexion  
✅ Expiration automatique des paniers abandonnés  

### 2. Architecture Microservices
✅ Découpler les responsabilités en services autonomes  
✅ Scalabilité indépendante de chaque service  
✅ Isolation des pannes (si payment down, le reste fonctionne)  
✅ Technologies différentes par service si besoin  

---

## 🏛️ Architecture Globale

```
┌─────────────────────────────────────────────────────────────────┐
│                         API GATEWAY (KONG)                      │
│                    + Keycloak OIDC Plugin                       │
└──────────┬──────────────────┬─────────────────┬────────────────┘
           │                  │                 │
           ▼                  ▼                 ▼
┌──────────────────┐  ┌─────────────────┐  ┌────────────────────┐
│   ms-cinema      │  │   ms-payment    │  │  ms-notification   │
│   (principal)    │  │                 │  │                     │
│                  │  │                 │  │                     │
│ - Films          │  │ - Transactions  │  │ - Emails           │
│ - Projections    │  │ - Stripe/PayPal │  │ - SMS (optionnel)  │
│ - Reservations   │  │ - Webhooks      │  │ - Push notifs      │
│ - Cinemas/Salles │  │ - Remboursements│  │ - Templates        │
│ - Users          │  │                 │  │ - Tracking envois  │
│ - Guest Carts    │  │                 │  │                     │
└──────────┬───────┘  └─────────┬───────┘  └──────────┬─────────┘
           │                    │                      │
           │     FeignClient    │                      │
           ├────────────────────┼──────────────────────┤
           │                    │                      │
           ▼                    ▼                      ▼
┌──────────────────┐  ┌─────────────────┐  ┌────────────────────┐
│   PostgreSQL     │  │   PostgreSQL    │  │   PostgreSQL       │
│   db_cinema      │  │   db_payment    │  │   db_notification  │
└──────────────────┘  └─────────────────┘  └────────────────────┘

           │                    │                      │
           └────────────────────┴──────────────────────┘
                                │
                                ▼
                    ┌───────────────────────┐
                    │   Message Queue       │
                    │   (RabbitMQ/Kafka)    │
                    │   - Events async      │
                    └───────────────────────┘
```

---

## 📦 Détail des Microservices

### 1️⃣ **ms-cinema** (Service Principal)

**Responsabilités :**
- Gestion du catalogue (Films, Cinémas, Salles, Projections)
- Gestion des utilisateurs (synchronisation Keycloak)
- **Panier temporaire (Guest Cart)**
- **Réservations** (création, annulation)
- Disponibilité des places en temps réel
- Historique des réservations utilisateur

**Base de données :** `db_cinema`

**Tables :**
- `user` (sync avec Keycloak)
- `ville`, `cinema`, `salle`, `place`
- `category`, `film`, `film_projection`
- `tarif_type`
- **`guest_cart`** (nouveau)
- **`guest_cart_item`** (nouveau)
- `reservation`
- `ticket`

**API Endpoints :**
```
GET    /api/films
GET    /api/films/{id}/projections
GET    /api/projections/{id}/places
POST   /api/guest-cart              ← Créer panier anonyme
POST   /api/guest-cart/items        ← Ajouter place au panier
GET    /api/guest-cart/{sessionId}  ← Récupérer panier
POST   /api/reservations             ← Créer réservation (auth required)
GET    /api/reservations/{id}
POST   /api/reservations/{id}/confirm
DELETE /api/reservations/{id}
```

**Communication vers autres services :**
- `ms-payment` : FeignClient pour créer transaction
- `ms-notification` : FeignClient pour envoyer emails

---

### 2️⃣ **ms-payment** (Service Paiement)

**Responsabilités :**
- Gestion des transactions de paiement
- Intégration Stripe/PayPal/etc.
- Webhooks des providers
- Remboursements
- Historique des paiements
- Sécurité PCI DSS

**Base de données :** `db_payment`

**Tables :**
- `payment`
- `payment_method` (CB, PayPal, etc.)
- `refund`
- `payment_webhook_log` (traçabilité webhooks)

**API Endpoints :**
```
POST   /api/payments/intent              ← Créer intention de paiement
POST   /api/payments/{id}/confirm        ← Confirmer paiement
POST   /api/payments/{id}/refund         ← Rembourser
GET    /api/payments/{id}
GET    /api/payments/reservation/{resId}
POST   /api/webhooks/stripe              ← Webhook Stripe
POST   /api/webhooks/paypal              ← Webhook PayPal
```

**Communication vers autres services :**
- `ms-cinema` : Callback pour mettre à jour statut réservation
- `ms-notification` : Envoyer confirmation/échec paiement

---

### 3️⃣ **ms-notification** (Service Notifications)

**Responsabilités :**
- Envoi d'emails transactionnels
- Génération de tickets PDF avec QR code
- Envoi de SMS (optionnel)
- Push notifications (optionnel)
- Gestion des templates d'emails
- File d'attente des notifications
- Retry automatique en cas d'échec

**Base de données :** `db_notification`

**Tables :**
- `email_notification`
- `email_template`
- `sms_notification` (optionnel)
- `push_notification` (optionnel)

**API Endpoints :**
```
POST   /api/notifications/email          ← Envoyer email
POST   /api/notifications/email/bulk     ← Envoi en masse
GET    /api/notifications/{id}
GET    /api/notifications/user/{userId}
POST   /api/notifications/generate-ticket-pdf  ← Générer PDF ticket
```

**Intégrations externes :**
- SendGrid / AWS SES / SMTP
- Twilio (SMS)
- Firebase Cloud Messaging (Push)

---

## 🛒 Gestion du Panier Non Connecté (Guest Cart)

### 🎯 Problématique

Un utilisateur **non authentifié** veut :
1. ✅ Naviguer dans les films
2. ✅ Choisir une projection
3. ✅ Sélectionner 1 ou plusieurs places
4. ✅ Voir son panier
5. ❌ **Bloquer ici** → Redirection vers login/signup
6. ✅ Après connexion → **Restaurer le panier automatiquement**
7. ✅ Continuer vers paiement

---

### 🗂️ Solution : Session Côté Backend + LocalStorage Frontend

#### Stratégie Hybride

```
┌─────────────────────────────────────────────────────────────┐
│  FRONTEND (Angular)                                         │
│                                                              │
│  LocalStorage:                                              │
│  {                                                          │
│    "guestSessionId": "uuid-generated",                     │
│    "cartItems": [                                           │
│      { projectionId: 1, placeId: 10, tarifId: 1 },        │
│      { projectionId: 1, placeId: 11, tarifId: 2 }         │
│    ],                                                       │
│    "expiresAt": "2024-12-15T14:30:00Z"                     │
│  }                                                          │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       │ POST /api/guest-cart (avec sessionId)
                       ▼
┌─────────────────────────────────────────────────────────────┐
│  BACKEND (ms-cinema)                                        │
│                                                              │
│  Table: guest_cart                                          │
│  - session_id (UUID unique)                                │
│  - created_at                                               │
│  - expires_at (15 min)                                      │
│  - converted_to_user_id (après login)                       │
│                                                              │
│  Table: guest_cart_item                                     │
│  - guest_cart_id                                            │
│  - film_projection_id                                       │
│  - place_id                                                 │
│  - tarif_type_id                                            │
│  - prix_snapshot                                            │
└─────────────────────────────────────────────────────────────┘
```

---

### 📋 Nouvelles Tables pour Guest Cart

#### Table `guest_cart`
```sql
CREATE TABLE public.guest_cart (
    id BIGSERIAL PRIMARY KEY,
    session_id VARCHAR(255) UNIQUE NOT NULL,  -- UUID généré côté frontend
    
    -- Conversion après login
    converted_to_user_id BIGINT REFERENCES public.user(id),
    converted_to_reservation_id BIGINT REFERENCES public.reservation(id),
    is_converted BOOLEAN DEFAULT false,
    
    -- Expiration
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,  -- created_at + 15 min
    
    -- Métadonnées
    ip_address VARCHAR(50),
    user_agent TEXT
);

CREATE INDEX idx_guest_cart_session_id ON public.guest_cart(session_id);
CREATE INDEX idx_guest_cart_expires_at ON public.guest_cart(expires_at);
CREATE INDEX idx_guest_cart_converted ON public.guest_cart(is_converted);

-- Job automatique de nettoyage des paniers expirés
-- DELETE FROM guest_cart WHERE expires_at < NOW() AND is_converted = false;
```

#### Table `guest_cart_item`
```sql
CREATE TABLE public.guest_cart_item (
    id BIGSERIAL PRIMARY KEY,
    guest_cart_id BIGINT NOT NULL REFERENCES public.guest_cart(id) ON DELETE CASCADE,
    
    -- Place sélectionnée
    film_projection_id BIGINT NOT NULL REFERENCES public.film_projection(id),
    place_id BIGINT NOT NULL REFERENCES public.place(id),
    tarif_type_id BIGINT NOT NULL REFERENCES public.tarif_type(id),
    
    -- Prix au moment de l'ajout (snapshot)
    prix_base DECIMAL(10,2) NOT NULL,
    prix_final DECIMAL(10,2) NOT NULL,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Contrainte : une place ne peut être dans plusieurs paniers actifs
    CONSTRAINT unique_place_in_cart UNIQUE (place_id, film_projection_id)
);

CREATE INDEX idx_guest_cart_item_cart ON public.guest_cart_item(guest_cart_id);
CREATE INDEX idx_guest_cart_item_projection ON public.guest_cart_item(film_projection_id);
```

---

### 🔄 Flux Utilisateur Non Connecté

#### **Scénario 1 : Sélection de Places (Non Connecté)**

```javascript
// FRONTEND (Angular)
// 1. Utilisateur arrive sur la page de sélection de places
const sessionId = localStorage.getItem('guestSessionId') || generateUUID();
localStorage.setItem('guestSessionId', sessionId);

// 2. Utilisateur clique sur une place
async function addToGuestCart(projectionId, placeId, tarifId) {
  const response = await http.post('/api/guest-cart/items', {
    sessionId: sessionId,
    projectionId: projectionId,
    placeId: placeId,
    tarifId: tarifId
  });
  
  // Sauvegarder aussi en localStorage (backup)
  const cart = JSON.parse(localStorage.getItem('guestCart') || '{"items": []}');
  cart.items.push({ projectionId, placeId, tarifId });
  localStorage.setItem('guestCart', JSON.stringify(cart));
}

// 3. Utilisateur clique sur "Réserver"
function proceedToCheckout() {
  if (!isAuthenticated()) {
    // Sauvegarder l'intention de redirection
    sessionStorage.setItem('redirectAfterLogin', '/checkout');
    router.navigate(['/login']); // Redirection vers login
  } else {
    router.navigate(['/checkout']);
  }
}
```

```java
// BACKEND (ms-cinema)
@RestController
@RequestMapping("/api/guest-cart")
public class GuestCartController {

    @Autowired
    private GuestCartService guestCartService;

    @PostMapping("/items")
    public ResponseEntity<GuestCartItemDto> addItem(@RequestBody GuestCartItemRequest request) {
        // 1. Vérifier si la place est disponible
        if (!placeService.isAvailable(request.getPlaceId(), request.getProjectionId())) {
            throw new PlaceNotAvailableException("Place déjà réservée");
        }

        // 2. Récupérer ou créer le panier guest
        GuestCart cart = guestCartService.getOrCreate(request.getSessionId());

        // 3. Vérifier expiration
        if (cart.getExpiresAt().isBefore(LocalDateTime.now())) {
            guestCartService.clearExpiredCart(cart.getId());
            throw new CartExpiredException("Panier expiré");
        }

        // 4. Ajouter l'item
        GuestCartItem item = guestCartService.addItem(cart, request);

        return ResponseEntity.ok(mapper.toDto(item));
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<GuestCartDto> getCart(@PathVariable String sessionId) {
        GuestCart cart = guestCartService.findBySessionId(sessionId);
        return ResponseEntity.ok(mapper.toDto(cart));
    }
}
```

---

#### **Scénario 2 : Connexion & Restauration du Panier**

```javascript
// FRONTEND (Angular)
// Après succès du login Keycloak
async function onLoginSuccess(keycloakUser) {
  const sessionId = localStorage.getItem('guestSessionId');
  
  if (sessionId) {
    // Convertir le panier guest en réservation utilisateur
    await http.post('/api/guest-cart/convert', {
      sessionId: sessionId,
      userId: keycloakUser.sub // Keycloak ID
    });
    
    // Nettoyer le localStorage
    localStorage.removeItem('guestSessionId');
    localStorage.removeItem('guestCart');
  }
  
  // Rediriger vers checkout
  const redirectUrl = sessionStorage.getItem('redirectAfterLogin') || '/';
  router.navigate([redirectUrl]);
}
```

```java
// BACKEND (ms-cinema)
@PostMapping("/convert")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<ReservationDto> convertGuestCartToReservation(
    @RequestBody ConvertCartRequest request,
    @AuthenticationPrincipal Jwt jwt
) {
    String keycloakId = jwt.getSubject();
    
    // 1. Récupérer le panier guest
    GuestCart guestCart = guestCartService.findBySessionId(request.getSessionId());
    
    if (guestCart.isConverted()) {
        throw new CartAlreadyConvertedException();
    }
    
    // 2. Récupérer ou créer l'utilisateur
    User user = userService.getOrCreateFromKeycloak(keycloakId, jwt);
    
    // 3. Créer la réservation
    Reservation reservation = reservationService.createFromGuestCart(guestCart, user);
    
    // 4. Marquer le panier comme converti
    guestCart.setConvertedToUserId(user.getId());
    guestCart.setConvertedToReservationId(reservation.getId());
    guestCart.setIsConverted(true);
    guestCartRepository.save(guestCart);
    
    return ResponseEntity.ok(mapper.toDto(reservation));
}
```

---

### ⏰ Gestion de l'Expiration

#### Job Automatique (Spring @Scheduled)

```java
@Service
public class GuestCartCleanupService {

    @Autowired
    private GuestCartRepository guestCartRepository;

    // Toutes les 5 minutes
    @Scheduled(cron = "0 */5 * * * *")
    public void cleanupExpiredCarts() {
        LocalDateTime now = LocalDateTime.now();
        
        // Récupérer les paniers expirés non convertis
        List<GuestCart> expiredCarts = guestCartRepository
            .findByExpiresAtBeforeAndIsConvertedFalse(now);
        
        for (GuestCart cart : expiredCarts) {
            // Libérer les places réservées
            cart.getItems().forEach(item -> {
                placeService.releasePlace(item.getPlaceId(), item.getFilmProjectionId());
            });
            
            // Supprimer le panier (cascade delete items)
            guestCartRepository.delete(cart);
        }
        
        log.info("Cleaned up {} expired guest carts", expiredCarts.size());
    }
}
```

---

## 🔗 Communication entre Microservices

### FeignClient (Synchrone)

#### Configuration dans `ms-cinema`

```java
// pom.xml - Ajouter dépendance
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>

// Application.java
@SpringBootApplication
@EnableFeignClients
public class CinemaApplication {
    public static void main(String[] args) {
        SpringApplication.run(CinemaApplication.class, args);
    }
}

// PaymentClient.java
@FeignClient(name = "ms-payment", url = "${services.payment.url}")
public interface PaymentClient {
    
    @PostMapping("/api/payments/intent")
    PaymentIntentDto createPaymentIntent(@RequestBody CreatePaymentRequest request);
    
    @GetMapping("/api/payments/{id}")
    PaymentDto getPayment(@PathVariable("id") Long paymentId);
    
    @PostMapping("/api/payments/{id}/confirm")
    PaymentDto confirmPayment(@PathVariable("id") Long paymentId);
}

// NotificationClient.java
@FeignClient(name = "ms-notification", url = "${services.notification.url}")
public interface NotificationClient {
    
    @PostMapping("/api/notifications/email")
    EmailNotificationDto sendEmail(@RequestBody SendEmailRequest request);
    
    @PostMapping("/api/notifications/generate-ticket-pdf")
    byte[] generateTicketPdf(@RequestBody GenerateTicketRequest request);
}

// application.yml
services:
  payment:
    url: http://localhost:8083  # ou via service discovery
  notification:
    url: http://localhost:8084
```

#### Utilisation dans un Service

```java
@Service
@RequiredArgsConstructor
public class ReservationService {
    
    private final PaymentClient paymentClient;
    private final NotificationClient notificationClient;
    private final ReservationRepository reservationRepository;
    
    @Transactional
    public ReservationDto confirmReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new NotFoundException("Réservation non trouvée"));
        
        try {
            // 1. Créer l'intention de paiement via ms-payment
            PaymentIntentDto paymentIntent = paymentClient.createPaymentIntent(
                CreatePaymentRequest.builder()
                    .reservationId(reservationId)
                    .amount(reservation.getMontantTotal())
                    .currency("EUR")
                    .build()
            );
            
            reservation.setPaymentId(paymentIntent.getId());
            reservation.setStatut("PAYMENT_PENDING");
            reservationRepository.save(reservation);
            
            return mapper.toDto(reservation);
            
        } catch (FeignException e) {
            log.error("Erreur lors de la création du paiement", e);
            throw new PaymentServiceException("Service de paiement indisponible");
        }
    }
    
    @Transactional
    public void onPaymentSuccess(Long reservationId, Long paymentId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> new NotFoundException("Réservation non trouvée"));
        
        reservation.setStatut("PAID");
        reservation.setConfirmationDate(LocalDateTime.now());
        reservationRepository.save(reservation);
        
        // Envoyer email de confirmation via ms-notification
        try {
            notificationClient.sendEmail(
                SendEmailRequest.builder()
                    .userId(reservation.getUserId())
                    .type("RESERVATION_CONFIRMATION")
                    .reservationId(reservationId)
                    .build()
            );
        } catch (FeignException e) {
            // Log mais ne pas bloquer (retry plus tard)
            log.warn("Erreur envoi email, sera retryé plus tard", e);
        }
    }
}
```

---

### Message Queue (Asynchrone) - Recommandé pour Notifications

Pour éviter que `ms-cinema` soit bloqué si `ms-notification` est lent/down, utiliser une **queue asynchrone**.

#### Architecture avec RabbitMQ

```
ms-cinema                     RabbitMQ                   ms-notification
    │                            │                             │
    │  Publish Event             │                             │
    ├───"reservation.confirmed"──►│                             │
    │                            │                             │
    │                            │  Consume Event              │
    │                            ├────────────────────────────►│
    │                            │                             │
    │                            │                   Send Email│
    │                            │                      ◄──────┤
```

#### Configuration

```java
// pom.xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>

// ms-cinema : Publisher
@Service
@RequiredArgsConstructor
public class EventPublisher {
    
    private final RabbitTemplate rabbitTemplate;
    
    public void publishReservationConfirmed(Reservation reservation) {
        ReservationConfirmedEvent event = ReservationConfirmedEvent.builder()
            .reservationId(reservation.getId())
            .userId(reservation.getUserId())
            .userEmail(reservation.getUser().getEmail())
            .filmTitle(reservation.getFilmProjection().getFilm().getTitre())
            .projectionDate(reservation.getFilmProjection().getDateProjection())
            .places(reservation.getTickets().stream()
                .map(t -> t.getPlace().getNumero())
                .toList())
            .build();
        
        rabbitTemplate.convertAndSend(
            "cinema.exchange", 
            "reservation.confirmed", 
            event
        );
    }
}

// ms-notification : Consumer
@Component
@RabbitListener(queues = "notification.reservation.confirmed")
public class ReservationConfirmedListener {
    
    @Autowired
    private EmailService emailService;
    
    @RabbitHandler
    public void handleReservationConfirmed(ReservationConfirmedEvent event) {
        log.info("Réception event reservation.confirmed: {}", event.getReservationId());
        
        emailService.sendReservationConfirmation(event);
    }
}
```

---

## 🗄️ Bases de Données Séparées

### Principe de Séparation

Chaque microservice a **sa propre base de données**, aucun service ne doit accéder directement à la BDD d'un autre.

```
docker-compose.yml
──────────────────────────────────────────
services:
  db-cinema:
    image: postgres:15
    environment:
      POSTGRES_DB: db_cinema
      POSTGRES_USER: cinema_user
      POSTGRES_PASSWORD: cinema_pass
    ports:
      - "5432:5432"

  db-payment:
    image: postgres:15
    environment:
      POSTGRES_DB: db_payment
      POSTGRES_USER: payment_user
      POSTGRES_PASSWORD: payment_pass
    ports:
      - "5433:5432"

  db-notification:
    image: postgres:15
    environment:
      POSTGRES_DB: db_notification
      POSTGRES_USER: notif_user
      POSTGRES_PASSWORD: notif_pass
    ports:
      - "5434:5432"

  rabbitmq:
    image: rabbitmq:3-management
    ports:
      - "5672:5672"   # AMQP
      - "15672:15672" # Management UI
```

---

### ⚠️ Gestion des Données Partagées

**Problème :** Si `ms-payment` a besoin d'infos sur l'utilisateur, comment faire ?

**Solutions :**

#### Option 1 : Duplication Contrôlée (Recommandé)
Dupliquer les données essentielles dans chaque service.

```sql
-- db_payment
CREATE TABLE user_snapshot (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,  -- ID du user dans ms-cinema
    email VARCHAR(255),
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    synced_at TIMESTAMP
);

-- Synchronisé via events
```

#### Option 2 : API Call (FeignClient)
`ms-payment` appelle `ms-cinema` pour récupérer les infos user.

```java
@FeignClient(name = "ms-cinema", url = "${services.cinema.url}")
public interface CinemaClient {
    @GetMapping("/api/users/{id}")
    UserDto getUser(@PathVariable Long id);
}
```

**Avantages :** Pas de duplication  
**Inconvénients :** Couplage, latence, point de défaillance

#### Option 3 : Event Sourcing (Avancé)
Chaque changement est un event dans une queue centrale.

---

## 📐 Structure des Projets

```
cinema-microservices/
│
├── ms-cinema/                    ← Service principal
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/fr/uga/cinema/
│   │   │   │   ├── entities/
│   │   │   │   ├── repositories/
│   │   │   │   ├── services/
│   │   │   │   ├── web/
│   │   │   │   ├── clients/      ← FeignClients
│   │   │   │   ├── events/       ← Event publishers
│   │   │   │   └── config/
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       └── db/migration/
│   │   └── test/
│   ├── pom.xml
│   └── Dockerfile
│
├── ms-payment/                   ← Service paiement
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/fr/uga/payment/
│   │   │   │   ├── entities/
│   │   │   │   ├── services/
│   │   │   │   │   ├── StripeService.java
│   │   │   │   │   └── PayPalService.java
│   │   │   │   ├── web/
│   │   │   │   │   ├── PaymentController.java
│   │   │   │   │   └── WebhookController.java
│   │   │   │   ├── clients/      ← FeignClient vers ms-cinema
│   │   │   │   └── config/
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       └── db/migration/
│   │   └── test/
│   ├── pom.xml
│   └── Dockerfile
│
├── ms-notification/              ← Service notifications
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/fr/uga/notification/
│   │   │   │   ├── entities/
│   │   │   │   ├── services/
│   │   │   │   │   ├── EmailService.java
│   │   │   │   │   ├── PdfGenerator.java
│   │   │   │   │   └── QrCodeGenerator.java
│   │   │   │   ├── web/
│   │   │   │   ├── listeners/    ← RabbitMQ listeners
│   │   │   │   ├── templates/    ← Email templates
│   │   │   │   └── config/
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       └── db/migration/
│   │   └── test/
│   ├── pom.xml
│   └── Dockerfile
│
├── docker-compose.yml            ← Orchestration complète
├── kong-config/
│   └── kong.yml                  ← Config API Gateway
└── keycloak/
    └── realm-export.json
```

---

## 🚀 Plan de Migration vers Microservices

### Phase 1 : Préparation (Semaine 1)
✅ Créer les 3 projets Spring Boot  
✅ Configurer les 3 bases de données séparées  
✅ Configurer RabbitMQ  
✅ Configurer FeignClient  

### Phase 2 : Extraction ms-payment (Semaine 2)
✅ Créer les entités Payment, Refund  
✅ Créer PaymentService avec Stripe (sandbox)  
✅ Créer les endpoints API  
✅ Tester en isolation  
✅ Intégrer FeignClient dans ms-cinema  

### Phase 3 : Extraction ms-notification (Semaine 3)
✅ Créer les entités EmailNotification  
✅ Implémenter EmailService (SendGrid/SMTP)  
✅ Implémenter génération PDF + QR code  
✅ Configurer RabbitMQ listeners  
✅ Tester envoi emails  

### Phase 4 : Refactoring ms-cinema (Semaine 4)
✅ Ajouter tables guest_cart, guest_cart_item  
✅ Implémenter GuestCartService  
✅ Modifier ReservationService pour appeler ms-payment  
✅ Remplacer envoi emails direct par events RabbitMQ  

### Phase 5 : Mise à jour Frontend Angular (Semaine 5)
✅ Gestion panier localStorage  
✅ Redirection login après sélection places  
✅ Restauration panier après login  
✅ Intégration Stripe Elements  
✅ Affichage tickets avec QR codes  

### Phase 6 : Tests & Déploiement (Semaine 6)
✅ Tests d'intégration inter-services  
✅ Tests de charge  
✅ Configuration Kong pour routing  
✅ Déploiement Docker Compose  

---

## 📊 Comparaison Monolithe vs Microservices

| Critère | Monolithe (Actuel) | Microservices (Futur) |
|---------|-------------------|----------------------|
| **Déploiement** | 1 seul service | 3 services indépendants |
| **Scalabilité** | Tout ou rien | Scalabilité fine (ex: scale ms-payment x3) |
| **Isolation des pannes** | ❌ Si payment plante, tout plante | ✅ Si ms-payment down, cinema fonctionne |
| **Technologies** | Java/Spring Boot partout | Possibilité Node.js pour ms-notification |
| **Base de données** | 1 seule BDD | 3 BDD isolées |
| **Complexité** | ✅ Simple | ⚠️ Plus complexe (réseau, transactions distribuées) |
| **Développement** | ✅ Rapide au début | ⚠️ Overhead coordination |
| **Monitoring** | 1 seul service à surveiller | 3 services + queue + gateway |

---

## ⚖️ Mon Avis sur l'Architecture Proposée

### ✅ Points Positifs

1. **Séparation logique claire**
   - Cinema (métier principal)
   - Payment (responsabilité critique isolée)
   - Notification (async, non bloquant)

2. **Scalabilité ciblée**
   - Black Friday → Scale ms-payment x5
   - Campagne email → Scale ms-notification x3
   - Cinema reste stable

3. **Isolement des pannes**
   - Si Stripe tombe, le reste du site fonctionne
   - Si SendGrid lent, ça n'impacte pas les réservations

4. **Sécurité**
   - Credentials Stripe isolés dans ms-payment uniquement
   - PCI DSS compliance plus facile

5. **Évolutivité**
   - Facile d'ajouter ms-loyalty (programme fidélité)
   - Ou ms-recommendation (suggestions films)

### ⚠️ Points d'Attention

1. **Transactions distribuées**
   - Si payment OK mais email échoue → Utiliser Saga pattern
   - Besoin d'un état "PAYMENT_SUCCESS_PENDING_NOTIFICATION"

2. **Latence réseau**
   - Appels inter-services = latence
   - Mitigation : cache, async quand possible

3. **Debugging plus complexe**
   - Distributed tracing nécessaire (Zipkin/Jaeger)
   - Correlation ID à propager

4. **Coût opérationnel**
   - 3 services = 3x plus de monitoring
   - 3 BDD = 3x plus de backups

---

## 🎯 Recommandation Finale

### Pour un Projet en Production

✅ **OUI aux microservices** SI :
- Équipe > 5 développeurs
- Traffic élevé (>10K users/jour)
- Besoin de scale indépendamment
- Budget DevOps suffisant

### Pour un Projet de Formation/Démo

⚠️ **Compromis Hybride** :
1. **Garder monolithe pour l'instant**
2. **Préparer l'architecture microservices** (packages séparés)
3. **Implémenter FeignClient + Events** (même en interne)
4. **Migration facile plus tard**

```
ms-cinema/ (monolithe modulaire)
├── payment/     ← Package isolé, prêt à extraire
├── notification/ ← Package isolé, prêt à extraire
└── cinema/      ← Core business
```

---

## ✅ Ce que je vous propose

### Option A : Microservices Complets (4-6 semaines)
- 3 services séparés
- 3 BDD séparées
- RabbitMQ + FeignClient
- Docker Compose complet

### Option B : Monolithe Modulaire (2-3 semaines) ⭐ RECOMMANDÉ
- 1 seul service mais **packages séparés**
- 1 seule BDD mais **schémas séparés**
- Events internes (Spring ApplicationEvent)
- **Facile à migrer vers microservices plus tard**

---

**Quelle option préférez-vous ?** 🤔

A. 🚀 Microservices complets (ambitieux, plus long)  
B. 🎯 Monolithe modulaire (pragmatique, recommandé)  
C. 🤷 Vous me conseillez ? (je recommande B pour commencer)

**Et concernant le panier non connecté ?**  
✅ Cette fonctionnalité, je l'implémente dans les 2 cas ! C'est essentiel.

**Dites-moi votre choix et on commence ! 💪**













