# Restful Booker Java API Test Automation Framework

An enterprise-ready, interview-defensible REST API test automation framework built in Java targeting the [Restful Booker Demo API](https://restful-booker.herokuapp.com). Designed specifically for QA Automation Engineers and SDETs to showcase robust software design patterns, POJO serialization/deserialization, centralized request specifications, schema validation, data-driven testing, and rich Allure reporting.

---

## 🎯 Portfolio & Architectural Highlights

- **Clean Architecture & Separation of Concerns:** Config management, request building, endpoint routes, POJOs, services, and tests are strictly separated into distinct layers.
- **Defensive Against Live API Quirks:** Rather than making naive assumptions from outdated API documentation, tests are calibrated to match real observed behaviors of the live Heroku environment, thoroughly documented with inline technical commentary.
- **Zero Secrets Hardcoded:** Base URLs and credentials are read dynamically from classpath properties via a thread-safe `ConfigReader`, with support for environment variable and system property overrides.
- **POJO Serialization & Deserialization:** Leverages Jackson Databind with the Builder pattern and `@JsonProperty` mappings for type-safe requests and responses.
- **Comprehensive API Chaining:** Demonstrates stateful multi-endpoint workflow chaining (Create ➡️ Retrieve ➡️ Full Update ➡️ Partial Update ➡️ Delete ➡️ 404 Verification).
- **JSON Schema Validation:** Enforces strict structural contract compliance using REST Assured's `json-schema-validator` (Draft-07).
- **Allure Reporting:** Integrates `allure-testng` and `allure-rest-assured` filter to capture exact request/response headers, bodies, timestamps, and test steps.
- **CI/CD Integration:** Pre-configured GitHub Actions workflow (`.github/workflows/api-tests.yml`) executing tests and preserving report artifacts on every push/PR.

---

## 🛠 Technology Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| **Java** | 17+ | Primary programming language |
| **REST Assured** | 5.4.0 | HTTP client and fluent API assertion library |
| **TestNG** | 7.9.0 | Test execution engine, data providers, and assertions |
| **Jackson Databind** | 2.17.0 | POJO-to-JSON and JSON-to-POJO mapping |
| **JSON Schema Validator** | 5.4.0 | Contract & schema enforcement |
| **Allure Framework** | 2.25.0 | Interactive visual test reporting and attachments |
| **Apache Maven** | 3.9+ | Build management, dependency resolution, and test runner |
| **Git** | 2.x | Source version control |

---

## 📂 Project Structure

```text
restful-booker-api-automation/
├── .github/
│   └── workflows/
│       └── api-tests.yml                        # GitHub Actions CI pipeline configuration
├── pom.xml                                      # Maven dependencies, plugins, and execution profiles
├── testng.xml                                   # Suite configuration with logical test groupings
├── .gitignore                                   # Standard VCS ignore rules
├── README.md                                    # Project documentation
├── src/test/java/com/prince/api/
│   ├── auth/
│   │   └── AuthManager.java                     # Token generation, extraction, and caching
│   ├── base/
│   │   └── BaseTest.java                        # Base fixtures, SLA verification, and dynamic helper routines
│   ├── config/
│   │   └── ConfigReader.java                    # Classpath properties loader with ENV overrides
│   ├── endpoints/
│   │   └── Endpoints.java                       # Centralized endpoint path constants
│   ├── models/
│   │   ├── AuthRequest.java                     # /auth payload POJO
│   │   ├── Booking.java                         # Booking entity POJO (with Builder pattern)
│   │   ├── BookingDates.java                    # Booking dates child POJO
│   │   └── BookingResponse.java                 # POST /booking response wrapper POJO
│   ├── requests/
│   │   └── RequestSpec.java                     # RequestSpecification factory with Allure filter & WAF headers
│   ├── utils/
│   │   ├── JsonUtils.java                       # Jackson JSON utility reader & mapper
│   │   └── TestDataFactory.java                  # Reusable payload generation factory
│   └── tests/
│       ├── AuthenticationTest.java              # Auth positive & negative tests (TC01 - TC04)
│       ├── BookingCreateTest.java               # POJO, file-based, and DataProvider tests (TC05 - TC07)
│       ├── BookingGetTest.java                  # List, ID, filter, schema, and SLA tests (TC08 - TC12)
│       ├── BookingUpdateTest.java               # Full PUT update & persistence tests (TC13 - TC14)
│       ├── BookingPatchTest.java                # Partial PATCH update tests (TC15 - TC16)
│       ├── BookingDeleteTest.java               # Deletion & post-delete verification (TC17 - TC18)
│       ├── EndToEndBookingFlowTest.java         # Stateful API chaining lifecycle test (TC19)
│       └── NegativeScenariosTest.java           # 400, 403, 404, 405, 500 error tests (TC20 - TC30)
└── src/test/resources/
    ├── config.properties                        # Environment configuration and SLA thresholds
    ├── schemas/
    │   └── booking-schema.json                  # JSON Schema contract definition for booking validation
    └── testdata/
        └── booking.json                         # External JSON test fixture
```

---

## 🧪 Test Case Coverage Matrix (TC01 - TC30)

| ID | Test Class | Test Method | Description | Expected Status |
|---|---|---|---|---|
| **TC01** | `AuthenticationTest` | `testTC01_ValidAuthentication` | Valid credentials generate auth token | `200 OK` |
| **TC02** | `AuthenticationTest` | `testTC02_InvalidPasswordAuthentication` | Invalid password returns error reason | `200 OK` (Live Quirk) |
| **TC03** | `AuthenticationTest` | `testTC03_InvalidUsernameAuthentication` | Non-existent username returns error reason | `200 OK` (Live Quirk) |
| **TC04** | `AuthenticationTest` | `testTC04_EmptyPayloadAuthentication` | Empty payload returns error reason | `200 OK` (Live Quirk) |
| **TC05** | `BookingCreateTest` | `testTC05_CreateBookingUsingPojo` | Create booking using typed Java POJO | `200 OK` |
| **TC06** | `BookingCreateTest` | `testTC06_CreateBookingUsingExternalJson` | Create booking from `booking.json` resource | `200 OK` |
| **TC07a**| `BookingCreateTest` | `testTC07_CreateBookingDataDriven [Alice]` | Data-driven dataset 1 (Alice Smith) | `200 OK` |
| **TC07b**| `BookingCreateTest` | `testTC07_CreateBookingDataDriven [Bob]` | Data-driven dataset 2 (Bob Johnson) | `200 OK` |
| **TC07c**| `BookingCreateTest` | `testTC07_CreateBookingDataDriven [Charlie]` | Data-driven dataset 3 (Charlie Davis) | `200 OK` |
| **TC08** | `BookingGetTest` | `testTC08_GetAllBookingIds` | Retrieve non-empty list of booking IDs | `200 OK` |
| **TC09** | `BookingGetTest` | `testTC09_GetBookingByDynamicId` | Retrieve specific booking by ID & assert fields | `200 OK` |
| **TC10** | `BookingGetTest` | `testTC10_GetBookingsWithQueryParams` | Filter bookings using query params | `200 OK` |
| **TC11** | `BookingGetTest` | `testTC11_BookingJsonSchemaValidation` | Validate schema against `booking-schema.json` | `200 OK` |
| **TC12** | `BookingGetTest` | `testTC12_BookingResponseSlaValidation` | Response-time SLA performance validation | `200 OK` |
| **TC13** | `BookingUpdateTest` | `testTC13_FullBookingUpdatePut` | Full update with `Cookie: token=...` | `200 OK` |
| **TC14** | `BookingUpdateTest` | `testTC14_VerifyPutChangesPersisted` | Verify full update persisted via GET | `200 OK` |
| **TC15** | `BookingPatchTest` | `testTC15_PartialUpdateFirstnameAndNeeds` | Partial PATCH update of name & needs | `200 OK` |
| **TC16** | `BookingPatchTest` | `testTC16_PartialUpdatePriceAndDeposit` | Partial PATCH update of price & deposit | `200 OK` |
| **TC17** | `BookingDeleteTest` | `testTC17_DeleteBookingWithAuth` | DELETE booking with auth cookie | `201 Created` (Live Quirk) |
| **TC18** | `BookingDeleteTest` | `testTC18_VerifyDeletedBookingNotFound` | Verify deleted booking returns 404 | `404 Not Found` |
| **TC19** | `EndToEndBookingFlowTest` | `testTC19_CompleteBookingLifecycle` | Chained E2E: Auth ➡️ POST ➡️ GET ➡️ PUT ➡️ GET ➡️ PATCH ➡️ GET ➡️ DELETE ➡️ GET 404 | Multiple chained steps |
| **TC20** | `NegativeScenariosTest` | `testTC20_GetNonExistingBooking` | GET nonexistent ID (999999999) | `404 Not Found` |
| **TC21** | `NegativeScenariosTest` | `testTC21_PutWithoutAuthentication` | PUT without auth cookie | `403 Forbidden` |
| **TC22** | `NegativeScenariosTest` | `testTC22_PutWithInvalidAuthentication` | PUT with garbage token | `403 Forbidden` |
| **TC23** | `NegativeScenariosTest` | `testTC23_PatchWithoutAuthentication` | PATCH without auth cookie | `403 Forbidden` |
| **TC24** | `NegativeScenariosTest` | `testTC24_DeleteWithoutAuthentication` | DELETE without auth cookie | `403 Forbidden` |
| **TC25** | `NegativeScenariosTest` | `testTC25_UnsupportedHttpMethod` | Unsupported POST on `/booking/{id}` item route | `404 Not Found` |
| **TC26** | `NegativeScenariosTest` | `testTC26_MalformedJsonRequestBody` | POST malformed JSON syntax | `400 Bad Request` / `500` |
| **TC27** | `NegativeScenariosTest` | `testTC27_InvalidPathBookingId` | GET non-numeric string ID | `404 Not Found` |
| **TC28** | `NegativeScenariosTest` | `testTC28_MissingRequiredFieldsPayload` | POST missing required fields payload | `500 Server Error` (Live Quirk) |
| **TC29** | `NegativeScenariosTest` | `testTC29_DeleteNonExistingBooking` | DELETE non-existent booking with valid token | `405 Not Allowed` (Live Quirk) |
| **TC30** | `NegativeScenariosTest` | `testTC30_PingHealthCheck` | Health check GET `/ping` endpoint | `201 Created` (Live Quirk) |

*Total: 32 test runs (including 3 DataProvider iterations).*

---

## 🔍 Observed Live API Quirks vs. Standard REST Assumptions

During live testing against the Heroku-hosted Restful Booker service, several discrepancies were observed. Building resilient automation requires validating real system behavior rather than blindly following specifications:

1. **`POST /auth` Bad Credentials Returns 200 OK:**
   Instead of returning `401 Unauthorized`, the endpoint returns `200 OK` with JSON `{"reason":"Bad credentials"}` and no token.
2. **Strict Header & User-Agent Filtering:**
   Restful Booker Heroku host enforces strict header matching (`Accept: application/json`). Default REST Assured multi-mime accept headers yield `HTTP 418 I'm a Teapot`. Fixed via explicit header configuration in `RequestSpec`.
3. **`DELETE /booking/{id}` Returns 201 Created:**
   Unlike standard `200 OK` or `204 No Content`, Restful Booker responds with `201 Created` and plaintext `Created`.
4. **`DELETE /booking/{nonexistent_id}` Returns 405 Method Not Allowed:**
   Attempting to delete a non-existent ID with a valid token yields `405 Method Not Allowed` rather than `404 Not Found`.
5. **`POST /booking` Missing Fields Returns 500 Internal Server Error:**
   Omitting required fields in the JSON body crashes the server-side validator, returning `500 Internal Server Error` instead of `400 Bad Request`.

---

## 🚀 Setup & Execution Guide (Windows / PowerShell)

### Prerequisites
- **JDK 17+** installed and available on PATH (`java -version`).
- **Apache Maven 3.8+** installed and on PATH (`mvn -version`).
- **Git** installed (`git --version`).

### 1. Clone & Navigate to Project
```powershell
git clone https://github.com/princepadaliya31/Restful-Booker-API-Automation-Framework.git
cd restful-booker-api-automation
```

### 2. Execute Tests via Maven
Run the full TestNG suite configured in `testng.xml`:
```powershell
mvn clean test
```

Execute specific TestNG groups:
```powershell
mvn clean test -Dgroups=smoke
mvn clean test -Dgroups=regression
mvn clean test -Dgroups=e2e
```

### 3. Generate & Open Allure Report
After the tests execute, generate and view the interactive Allure HTML report:
```powershell
allure serve target/allure-results
```
*This command starts a lightweight local web server and automatically opens the report in your default browser.*

To generate a standalone static report in `target/site/allure-maven-plugin`:
```powershell
mvn allure:report
```

---

## 💼 Pairing with a UI Automation Framework (SDET Portfolio Context)

For a comprehensive QA Automation / SDET portfolio, this API automation framework pairs seamlessly with a Selenium or Playwright UI automation project:

1. **Test Pyramid Alignment:** 70% of regression and business logic testing runs quickly and reliably at the API layer (taking seconds with zero flakiness), while the UI suite focuses on end-to-end user journeys and visual rendering.
2. **Hybrid State Pre-Condition Setup:** In real-world enterprise automation, UI tests should not walk through 10 tedious form screens just to seed test data. Instead, this API framework's `BookingCreateTest` or `AuthManager` can be invoked via REST calls in UI `@BeforeMethod` hooks to instantly seed bookings and inject authentication cookies directly into the browser session, slashing UI test execution times by 80%.
3. **Dual-Layer Validation:** E2E tests can perform an action in the UI (e.g., booking a room in a web portal) and immediately verify backend state via REST Assured assertions against the database/API layer.
