# CieloPass — Copilot Development Instructions

## 1. Project Overview & Business Rules

**CieloPass** is a high-reliability Android application designed for ticket sales for local events,
running natively on **Cielo Smart / Cielo LIO** POS terminals.

### Core Business Flows

1. **Event Discovery:** List available events with date, venue, price tiers, and available ticket
   counts.
2. **Ticket Selection:** Select ticket quantities and calculate total amounts.
3. **Checkout & Payment:** Initiate transaction via Cielo Smart DeepLink integration (`order://`).
4. **Payment Result Processing:** Handle callback response (Approved, Denied, User Cancelled) via
   `ResponseActivity`.
5. **Receipt & Ticket QR Code:** Generate offline-verifiable QR code linked strictly to approved,
   completed transactions.

### Non-Negotiable Business Rules

- **Pre-Transaction Registration:** Before triggering any Cielo payment Intent, pre-register the
  transaction in Room DB with status `PENDING` and a unique `UUID`.
- **Anti-Duplication / Idempotency:** Check for active `PENDING` transactions before firing a new
  payment Intent to prevent double charges on network or terminal UI retries.
- **Foreground Service Execution:** Payment Intent dispatches must be paired with
  `CieloPaymentForegroundService` to keep the process alive on POS hardware (Target SDK 29).

---

## 2. Discovered Tech Stack & Dependencies

Extracted directly from project version catalog (`gradle/libs.versions.toml`) and build
configuration (`app/build.gradle.kts`):

- **Kotlin & Tooling:** Kotlin `2.4.10`, Android Gradle Plugin `9.3.1`, KSP `2.3.10`
- **SDK Targets:** `compileSdk = 37`, `targetSdk = 29` (Cielo Smart LIO requirement), `minSdk = 24`
- **UI & Layouts:** Jetpack Compose BOM `2026.06.01`, Material3 (`androidx.compose.material3`),
  Activity Compose `1.13.0`
- **Navigation:** Jetpack Navigation 3 (`androidx.navigation3:navigation3-runtime`,
  `navigation3-ui`, `material3-adaptive-navigation3`)
- **Architecture Pattern:** MVI (Model-View-Intent / Model-View-Event) with UDF (`StateFlow<State>`,
  `SharedFlow<Effect>`)
- **Dependency Injection:** Koin `4.2.2` (`koin-core`, `koin-android`, `koin-androidx-compose`)
- **Database & Local Storage:** Room `2.8.4` (Runtime, KTX, KSP Compiler), DataStore `1.2.1`
- **Serialization:** Kotlinx Serialization `1.11.0`, Protobuf JavaLite `4.35.1`
- **Networking:** Retrofit `3.0.0` with Gson Converter
- **Security & Cryptography:** Google Tink Android `1.23.0`, Password4j `1.8.4`, JJWT `0.13.0`
- **Code Quality:** ktlint `14.2.0` (`org.jlleitschuh.gradle.ktlint`)
- **Desugaring:** Core Library Desugaring `2.1.5`

---

## 3. Architecture & Package Conventions

### Directory Layout

The codebase is structured into `core` and `features` directories. Each feature strictly follows the
`data`-`domain`-`presentation` pattern.

```
com.cielo.cielopass/
├── core/
│   ├── cielo/                 # DeepLink Intent builder, Base64 parser, Foreground Service
│   ├── database/              # Room DB, DAOs, Entities
│   ├── datastore/             # Encrypted DataStore via Google Tink
│   ├── network/               # Retrofit instance & API services
│   ├── navigation/            # Navigation 3 NavKeys & host setup
│   └── ui/                    # Design System, Theme, Typography, Reusable Composables
└── features/
    ├── events/
    │   ├── data/              # Repositories, DataSources, Mappers
    │   ├── domain/            # Domain Models, Use Cases, Repository Interfaces
    │   └── presentation/      # MVI Contract (State, Event, Effect), ViewModel, Composables
    ├── checkout/
    │   ├── data/
    │   ├── domain/
    │   └── presentation/
    └── tickets/
        ├── data/
        ├── domain/
        └── presentation/
```

### MVI Pattern Implementation

Every feature screen in `presentation/` must define an MVI Contract interface or file containing:

```kotlin
// Example MVI Contract Structure
data class CheckoutState(
    val isLoading: Boolean = false,
    val selectedTickets: List<TicketItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val error: String? = null
)

sealed interface CheckoutEvent {
    data class SelectQuantity(val ticketId: String, val count: Int) : CheckoutEvent
    data object ProcessPayment : CheckoutEvent
}

sealed interface CheckoutEffect {
    data class LaunchCieloPayment(val paymentUri: String) : CheckoutEffect
    data class ShowToast(val message: String) : CheckoutEffect
}
```

- **ViewModel:** Exposes `val state: StateFlow<CheckoutState>` and
  `val effect: Flow<CheckoutEffect>`.
- **User Actions:** Received through `fun onEvent(event: CheckoutEvent)`.
- **Navigation 3:** Screen routing handled cleanly via NavKeys reacting to `CheckoutEffect`
  emissions.

---

## 4. Cielo DeepLink Contract & Idempotency Strategy

### AndroidManifest Requirements

1. **Package Visibility Query:**

```xml

<queries>
    <package android:name="com.ads.lio.uriappclient" />
</queries>
```

2. **DeepLink Meta-data:**

```xml

<meta-data android:name="cs_integration_type" android:value="uri" />
```

3. **Response Callback Activity:**

```xml

<activity android:name=".core.cielo.ResponseActivity" android:exported="true" android:launchMode="singleTask">
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <data android:host="response" android:scheme="order" />
    </intent-filter>
</activity>
```

### Payment Lifecycle & Anti-Duplication

1. **Transaction Pre-Registration:** Before launching the deeplink, insert a record into Room DB
   with status `PENDING` and a generated `transactionId` (UUID).
2. **Foreground Service:** Start `CieloPaymentForegroundService` (`dataSync` type) prior to
   `startActivity(intent)`.
3. **Callback Processing:** `ResponseActivity` receives
   `order://response?response=<BASE64_STRING>&responsecode=0`.
4. **Base64 Decoding:** Decodes the Base64 string into JSON containing `code` and `reason` (or
   payment details).
    - Code `0`: Payment Approved -> Update status to `APPROVED` in Room DB.
    - Code `1`: Cancelled by User -> Update status to `CANCELLED`.
    - Code `2`/`3`/`4`: Errors -> Update status to `FAILED`.
5. **State Synchronization:** Emit database updates to refresh UI immediately.

---

## 5. Design System & Branding Guidelines (Cielo Brandbook)

### Color Palette

- **Primary (Cielo Blue):** `#00AEEF` (RGB `0, 174, 239`)
- **Secondary (Nightfall):** `#204986` (RGB `32, 73, 134`)
- **Neutral Dark (Rain):** `#5A646E` (RGB `90, 100, 110`)
- **Neutral Light (Cloud):** `#F1F2F2` (RGB `241, 242, 242`)
- **Accent Highlighting (Pistachio):** `#E0E566`
- **Alert / Warning (Sunset):** `#E0004D`

### Typography & Branding Rules

- **Montserrat Font Family:** Always use Montserrat for product headings, titles, and body text.
- **Brand Text Rule:** Never use the Cielo logo image inline as text. Always write "Cielo" in
  Montserrat typography.

---

## 6. Quality Gates & Verification Rules for AI Agents

When explicitly requested by the user to verify quality or before finishing tasks that ask for verification, AI agents can run:

1. **Static Analysis & Formatting:**
   ```powershell
   ./gradlew ktlintCheck
   ```
2. **Unit Tests:**
   ```powershell
   ./gradlew test
   ```
3. **Build Assembly Verification:**
   ```powershell
   ./gradlew assembleDebug
   ```

### AI Coding Agent Directives

- **Explicit User Request Requirement for Commits and PRs:** NEVER create git commits or pull requests automatically unless the user explicitly requests you to do so.
- **Explicit User Request Requirement for Gradle Quality Gates:** NEVER run `./gradlew ktlintCheck`, `./gradlew test`, or `./gradlew assembleDebug` quality gate commands automatically unless the user explicitly asks to run them or verify the build/tests.
- **Zero Unused Imports & Clean Syntax:** Always format code adhering to `ktlint` rules.
- **Strict Architecture Boundaries:** Never import data layer classes directly into presentation layer Composables. Always pass through Domain Use Cases/Repositories.
- **Idempotency Enforcement:** Ensure any code modifying transaction states uses Room transactions (`@Transaction`) to guarantee thread safety and data integrity.
