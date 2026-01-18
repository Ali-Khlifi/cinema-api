# 🛒 Flux Détaillé : Panier pour Utilisateur Non Connecté

## 🎯 Objectif
Permettre à un utilisateur de sélectionner des places **sans être connecté**, puis l'obliger à se connecter/créer un compte avant le paiement, tout en **restaurant son panier automatiquement**.

---

## 📱 Scénario Complet Pas-à-Pas

### 👤 Persona : "Marc, visiteur non connecté"

```
┌────────────────────────────────────────────────────────────────────┐
│  ÉTAPE 1 : NAVIGATION (NON CONNECTÉ)                              │
└────────────────────────────────────────────────────────────────────┘

Marc arrive sur le site
    │
    ├─ Page d'accueil : Liste des films
    │  └─ Clic sur "Avatar 3"
    │
    ├─ Page du film : Détails + Projections disponibles
    │  └─ Clic sur "Sam 16 Déc - 20h00 - UGC Lyon"
    │
    └─ Page de sélection des places
       └─ Plan de salle interactif s'affiche


┌────────────────────────────────────────────────────────────────────┐
│  ÉTAPE 2 : SÉLECTION DE PLACES (GÉNÉRATION SESSION)               │
└────────────────────────────────────────────────────────────────────┘

Frontend Angular détecte : pas de sessionId
    │
    ├─ Génération UUID : "a8f3e2c1-9d4b-4e5f-a2b3-1c2d3e4f5a6b"
    │  └─ Stockage : localStorage.setItem('guestSessionId', uuid)
    │
    └─ Envoi au backend : POST /api/guest-cart
       └─ Création d'un GuestCart en BDD
          - session_id = uuid
          - expires_at = NOW() + 15 min


Marc sélectionne les places :
    │
    ├─ Clic sur "Rangée C, Siège 12" (Tarif Normal)
    │  └─ POST /api/guest-cart/items
    │     { sessionId: "a8f3...", placeId: 45, tarifId: 1 }
    │
    ├─ Place ajoutée au panier
    │  └─ Backend vérifie disponibilité
    │     └─ Si OK : Création GuestCartItem + "soft lock" 15 min
    │
    ├─ Clic sur "Rangée C, Siège 13" (Tarif Réduit)
    │  └─ POST /api/guest-cart/items
    │     { sessionId: "a8f3...", placeId: 46, tarifId: 2 }
    │
    └─ Panier affiché :
       - Place C12 - Tarif Normal - 12,00 €
       - Place C13 - Tarif Réduit - 9,00 €
       ─────────────────────────────────────
       TOTAL : 21,00 €


┌────────────────────────────────────────────────────────────────────┐
│  ÉTAPE 3 : TENTATIVE DE RÉSERVATION → BLOCAGE                     │
└────────────────────────────────────────────────────────────────────┘

Marc clique sur "RÉSERVER & PAYER"
    │
    ├─ Frontend vérifie : isAuthenticated() ?
    │  └─ NON → Affichage Modal
    │
    └─ Modal : "Connexion requise"
       ┌──────────────────────────────────────────────┐
       │  Pour finaliser votre réservation,          │
       │  vous devez être connecté.                   │
       │                                              │
       │  [SE CONNECTER]  [CRÉER UN COMPTE]          │
       └──────────────────────────────────────────────┘


Marc clique sur "SE CONNECTER"
    │
    └─ Redirection vers Keycloak login
       └─ URL de retour enregistrée :
          sessionStorage.setItem('redirectAfterLogin', '/checkout')


┌────────────────────────────────────────────────────────────────────┐
│  ÉTAPE 4 : CONNEXION KEYCLOAK                                      │
└────────────────────────────────────────────────────────────────────┘

Page Keycloak
    │
    ├─ Marc entre : email + password
    │  └─ OU : Inscription si nouveau
    │
    └─ Authentification réussie
       └─ Redirection vers frontend avec JWT


┌────────────────────────────────────────────────────────────────────┐
│  ÉTAPE 5 : RESTAURATION AUTOMATIQUE DU PANIER                     │
└────────────────────────────────────────────────────────────────────┘

Frontend : Callback après login
    │
    ├─ Récupération JWT (Keycloak token)
    │  └─ Stockage : localStorage.setItem('access_token', jwt)
    │
    ├─ Récupération sessionId du panier guest
    │  └─ const sessionId = localStorage.getItem('guestSessionId')
    │
    ├─ Appel API de conversion :
    │  POST /api/guest-cart/convert
    │  Headers: { Authorization: "Bearer <jwt>" }
    │  Body: { sessionId: "a8f3e2c1-..." }
    │
    └─ Backend traite la conversion


Backend : ConvertGuestCartToReservation()
    │
    ├─ Extraction du Keycloak ID depuis JWT
    │  └─ String keycloakId = jwt.getSubject()
    │
    ├─ Récupération GuestCart par sessionId
    │  └─ GuestCart cart = guestCartRepo.findBySessionId(sessionId)
    │
    ├─ Vérification : panier non expiré ? non déjà converti ?
    │  └─ Si OK → Continue
    │
    ├─ Récupération ou création User
    │  └─ User user = userService.getOrCreateFromKeycloak(keycloakId)
    │     - Vérifie si user avec ce keycloakId existe
    │     - Si NON : Crée nouveau User
    │        - id (auto)
    │        - keycloak_id = keycloakId
    │        - email = jwt.getClaim("email")
    │        - first_name = jwt.getClaim("given_name")
    │        - last_name = jwt.getClaim("family_name")
    │
    ├─ Création Reservation
    │  └─ Reservation reservation = new Reservation()
    │     - numero = "RES-2024-" + generateSequence()
    │     - user_id = user.id
    │     - film_projection_id = cart.items[0].projectionId
    │     - statut = "PENDING"
    │     - sous_total = 21.00
    │     - montant_total = 21.00
    │     - reservation_date = NOW()
    │     - expiration_date = NOW() + 15 min
    │
    ├─ Création Tickets (un par place)
    │  └─ Pour chaque GuestCartItem :
    │     Ticket ticket = new Ticket()
    │     - reservation_id = reservation.id
    │     - place_id = item.placeId
    │     - tarif_type_id = item.tarifId
    │     - prix_base = 12.00 (ou 9.00)
    │     - prix_final = 12.00 (ou 9.00)
    │
    ├─ Marquage du panier comme converti
    │  └─ cart.is_converted = true
    │     cart.converted_to_user_id = user.id
    │     cart.converted_to_reservation_id = reservation.id
    │
    └─ Retour : ReservationDto


Frontend : Réception de la Reservation
    │
    ├─ Nettoyage du localStorage
    │  └─ localStorage.removeItem('guestSessionId')
    │     localStorage.removeItem('guestCart')
    │
    ├─ Redirection automatique
    │  └─ const redirect = sessionStorage.getItem('redirectAfterLogin')
    │     router.navigate([redirect]) // → '/checkout'
    │
    └─ Page Checkout affichée avec la réservation


┌────────────────────────────────────────────────────────────────────┐
│  ÉTAPE 6 : PAIEMENT                                                │
└────────────────────────────────────────────────────────────────────┘

Page Checkout
    │
    ├─ Affichage récapitulatif :
    │  - Film : Avatar 3
    │  - Date : Sam 16 Déc - 20h00
    │  - Places : C12 (Normal), C13 (Réduit)
    │  - Total : 21,00 €
    │
    ├─ Marc clique sur "PAYER PAR CARTE"
    │  └─ POST /api/reservations/{id}/pay
    │
    ├─ Backend appelle ms-payment (FeignClient)
    │  └─ POST http://ms-payment:8083/api/payments/intent
    │     { reservationId: 123, amount: 21.00, currency: "EUR" }
    │
    ├─ ms-payment appelle Stripe
    │  └─ Stripe crée PaymentIntent
    │     └─ client_secret retourné
    │
    ├─ Frontend affiche Stripe Elements
    │  └─ Marc entre : numéro de carte, expiration, CVC
    │
    ├─ Frontend confirme le paiement
    │  └─ stripe.confirmCardPayment(client_secret)
    │
    └─ Stripe traite le paiement


Webhook Stripe → ms-payment
    │
    ├─ POST http://ms-payment:8083/api/webhooks/stripe
    │  └─ Event : payment_intent.succeeded
    │
    ├─ ms-payment met à jour Payment
    │  └─ statut = "SUCCESS"
    │
    └─ ms-payment appelle ms-cinema (callback)
       └─ POST http://ms-cinema:8082/api/reservations/{id}/on-payment-success


Backend ms-cinema : onPaymentSuccess()
    │
    ├─ Met à jour Reservation
    │  └─ statut = "PAID"
    │     confirmation_date = NOW()
    │
    ├─ Génère QR codes pour les tickets
    │  └─ Pour chaque Ticket :
    │     qr_code = generateQRCode("TICKET-123-C12")
    │
    ├─ Publie event RabbitMQ
    │  └─ rabbitTemplate.send("reservation.confirmed", event)
    │
    └─ Retour succès au frontend


┌────────────────────────────────────────────────────────────────────┐
│  ÉTAPE 7 : CONFIRMATION & EMAIL                                    │
└────────────────────────────────────────────────────────────────────┘

ms-notification : Listener RabbitMQ
    │
    ├─ Réception event "reservation.confirmed"
    │  └─ { reservationId: 123, userEmail: "marc@email.com", ... }
    │
    ├─ Génération PDF des tickets
    │  └─ pdfService.generateTickets(reservationId)
    │     - Inclut QR codes
    │     - Sauvegarde : /tmp/tickets-123.pdf
    │
    ├─ Récupération template email
    │  └─ Template : "reservation_confirmation.html"
    │     - Remplacement variables : {{userName}}, {{filmTitle}}, etc.
    │
    ├─ Envoi email via SendGrid
    │  └─ sendgrid.send({
    │       to: "marc@email.com",
    │       subject: "Confirmation réservation - Avatar 3",
    │       html: template,
    │       attachments: [{ filename: "tickets.pdf", content: pdfBase64 }]
    │     })
    │
    ├─ Enregistrement dans BDD
    │  └─ EmailNotification
    │     - user_id = user.id
    │     - reservation_id = 123
    │     - notification_type = "RESERVATION_CONFIRMATION"
    │     - statut = "SENT"
    │     - sent_at = NOW()
    │
    └─ Log succès


Frontend : Page Confirmation
    │
    └─ Affichage :
       ┌──────────────────────────────────────────────┐
       │  ✅ Réservation confirmée !                  │
       │                                              │
       │  Numéro : RES-2024-001234                   │
       │                                              │
       │  Vos tickets ont été envoyés par email à :  │
       │  marc@email.com                             │
       │                                              │
       │  [TÉLÉCHARGER LES TICKETS PDF]              │
       │  [VOIR MES RÉSERVATIONS]                    │
       └──────────────────────────────────────────────┘
```

---

## 📊 Schéma Technique : Tables & Relations

```sql
-- Avant Login (Guest)
┌─────────────────────┐      ┌─────────────────────┐
│   guest_cart        │      │  guest_cart_item    │
├─────────────────────┤      ├─────────────────────┤
│ id                  │◄─────┤ id                  │
│ session_id (UUID)   │ 1  N │ guest_cart_id       │
│ is_converted        │      │ film_projection_id  │
│ expires_at          │      │ place_id            │
│ converted_to_user   │      │ tarif_type_id       │
│ converted_to_res    │      │ prix_final          │
└─────────────────────┘      └─────────────────────┘
           │
           │ Conversion après login
           ▼
┌─────────────────────┐      ┌─────────────────────┐
│      user           │      │    reservation      │
├─────────────────────┤      ├─────────────────────┤
│ id                  │◄─────┤ id                  │
│ keycloak_id         │ 1  N │ user_id             │
│ email               │      │ numero              │
│ first_name          │      │ statut              │
│ last_name           │      │ montant_total       │
└─────────────────────┘      └──────┬──────────────┘
                                    │ 1
                                    │
                                    │ N
                             ┌──────▼──────────────┐
                             │      ticket         │
                             ├─────────────────────┤
                             │ id                  │
                             │ reservation_id      │
                             │ place_id            │
                             │ tarif_type_id       │
                             │ qr_code             │
                             │ is_used             │
                             └─────────────────────┘
```

---

## 🔐 Sécurité & Edge Cases

### 1. Panier expiré pendant la navigation
```
Si expires_at < NOW() :
    - Libérer les places
    - Supprimer le GuestCart
    - Afficher message : "Votre panier a expiré, veuillez resélectionner vos places"
```

### 2. Place déjà réservée entre-temps
```
Si place n'est plus disponible lors de la conversion :
    - Ne pas créer le Ticket pour cette place
    - Informer l'utilisateur
    - Lui permettre de sélectionner une autre place
```

### 3. Double conversion (clic multiple)
```
Vérification : cart.is_converted == true ?
    - Si OUI : Retourner la Reservation existante
    - Éviter la duplication
```

### 4. Session hijacking
```
- sessionId est un UUID aléatoire (non devinable)
- Stocké en localStorage (accessible uniquement par le domaine)
- Expire après 15 min
- Pas de données sensibles dans le panier guest
```

### 5. Utilisateur ferme le navigateur
```
- localStorage persiste (contrairement aux cookies session)
- Panier restauré si l'utilisateur revient dans les 15 min
- Sinon, nettoyage automatique backend
```

---

## 🎨 UX : Messages Utilisateur

### Pendant la sélection (non connecté)
```
┌──────────────────────────────────────────────────────────┐
│  ℹ️ Vous pouvez sélectionner vos places sans compte.    │
│     Vous devrez vous connecter avant le paiement.       │
└──────────────────────────────────────────────────────────┘
```

### Lors du clic "Réserver"
```
┌──────────────────────────────────────────────────────────┐
│  🔒 Connexion requise                                    │
│                                                          │
│  Pour finaliser votre réservation et sécuriser vos      │
│  places, vous devez vous connecter ou créer un compte.  │
│                                                          │
│  Votre panier sera automatiquement restauré.            │
│                                                          │
│  [SE CONNECTER]           [CRÉER UN COMPTE]             │
└──────────────────────────────────────────────────────────┘
```

### Après login (restauration OK)
```
┌──────────────────────────────────────────────────────────┐
│  ✅ Panier restauré !                                    │
│                                                          │
│  Vos 2 places pour "Avatar 3" sont toujours réservées.  │
│  Vous avez 14 min pour finaliser votre paiement.        │
└──────────────────────────────────────────────────────────┘
```

### Si panier expiré
```
┌──────────────────────────────────────────────────────────┐
│  ⏱️ Panier expiré                                        │
│                                                          │
│  Votre réservation a expiré. Les places ont été         │
│  libérées. Veuillez sélectionner à nouveau vos places.  │
│                                                          │
│  [RETOUR À LA SÉLECTION]                                │
└──────────────────────────────────────────────────────────┘
```

---

## 🧪 Tests à Effectuer

### Test 1 : Parcours nominal
✅ Sélection places → Login → Panier restauré → Paiement → Confirmation

### Test 2 : Expiration avant login
✅ Sélection places → Attendre 16 min → Login → Message "Panier expiré"

### Test 3 : Place prise entre-temps
✅ User A sélectionne C12 → User B finalise C12 → User A login → Erreur sur C12

### Test 4 : Double clic conversion
✅ Login → Clic rapide 2x sur "Réserver" → Doit créer 1 seule Reservation

### Test 5 : Fermeture navigateur
✅ Sélection places → Fermer navigateur → Rouvrir dans 5 min → Panier présent

### Test 6 : Multi-appareils
✅ Sélection places sur mobile → Login sur desktop → Panier synchronisé ? NON (sessionId différent)

---

## 📱 Code Frontend Angular (Extraits)

### Service : GuestCartService

```typescript
@Injectable({ providedIn: 'root' })
export class GuestCartService {
  private sessionId: string;
  private cart$ = new BehaviorSubject<GuestCart | null>(null);

  constructor(private http: HttpClient) {
    this.sessionId = this.getOrCreateSessionId();
  }

  private getOrCreateSessionId(): string {
    let sessionId = localStorage.getItem('guestSessionId');
    if (!sessionId) {
      sessionId = this.generateUUID();
      localStorage.setItem('guestSessionId', sessionId);
    }
    return sessionId;
  }

  addPlace(projectionId: number, placeId: number, tarifId: number): Observable<GuestCartItem> {
    return this.http.post<GuestCartItem>('/api/guest-cart/items', {
      sessionId: this.sessionId,
      projectionId,
      placeId,
      tarifId
    }).pipe(
      tap(() => this.refreshCart())
    );
  }

  getCart(): Observable<GuestCart> {
    return this.http.get<GuestCart>(`/api/guest-cart/${this.sessionId}`);
  }

  convertToReservation(): Observable<Reservation> {
    return this.http.post<Reservation>('/api/guest-cart/convert', {
      sessionId: this.sessionId
    }).pipe(
      tap(() => {
        localStorage.removeItem('guestSessionId');
        localStorage.removeItem('guestCart');
      })
    );
  }

  private generateUUID(): string {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
      const r = Math.random() * 16 | 0;
      const v = c === 'x' ? r : (r & 0x3 | 0x8);
      return v.toString(16);
    });
  }
}
```

### Component : CheckoutComponent

```typescript
@Component({ selector: 'app-checkout', templateUrl: './checkout.component.html' })
export class CheckoutComponent implements OnInit {
  
  constructor(
    private authService: AuthService,
    private guestCartService: GuestCartService,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (this.authService.isAuthenticated()) {
      // Déjà connecté, convertir le panier si existant
      this.convertGuestCartIfExists();
    }
  }

  proceedToPayment(): void {
    if (!this.authService.isAuthenticated()) {
      // Sauvegarder l'URL de redirection
      sessionStorage.setItem('redirectAfterLogin', '/checkout');
      
      // Rediriger vers Keycloak
      this.authService.login();
    } else {
      // Continuer vers paiement
      this.router.navigate(['/payment']);
    }
  }

  private convertGuestCartIfExists(): void {
    const sessionId = localStorage.getItem('guestSessionId');
    if (sessionId) {
      this.guestCartService.convertToReservation().subscribe({
        next: (reservation) => {
          console.log('Panier converti en réservation:', reservation);
          // Afficher toast "Panier restauré !"
        },
        error: (err) => {
          if (err.error?.code === 'CART_EXPIRED') {
            // Afficher message "Panier expiré"
          }
        }
      });
    }
  }
}
```

---

## ✅ Résumé

Cette solution permet de :
✅ **Expérience fluide** : User peut explorer sans contrainte  
✅ **Conversion optimale** : Incite à créer un compte au bon moment  
✅ **Pas de perte** : Panier restauré automatiquement  
✅ **Sécurité** : Authentification obligatoire avant paiement  
✅ **Performance** : localStorage + backend (double sécurité)  
✅ **Scalable** : Job de nettoyage automatique des paniers abandonnés  

**Cette fonctionnalité est un vrai plus pour l'UX ! 🎯**













