# Restful Booker Java API Test Automation Framework

An enterprise-ready, interview-defensible REST API test automation framework built in Java targeting the [Restful Booker Demo API](https://restful-booker.herokuapp.com). Designed specifically for QA Automation Engineers and SDETs to showcase robust software design patterns, POJO serialization/deserialization, centralized request specifications, schema validation, data-driven testing, and rich Allure reporting.

---

## 🎯 Portfolio & Architectural Highlights

- **Clean Architecture & Separation of Concerns:** Config management, request building, endpoint routes, POJOs, services, and tests are strictly separated into distinct layers.
- **Defensive Against Live API Quirks:** Rather than making naive assumptions from outdated API documentation, tests are calibrated to match real observed behaviors of the live Heroku environment, thoroughly documented with inline technical commentary.
- **Zero Secrets Hardcoded:** Base URLs and credentials are read dynamically from classpath properties via a thread-safe `ConfigReader`.
- **POJO Serialization & Deserialization:** Leverages Jackson Databind with the Builder pattern and `@JsonProperty` mappings for type-safe requests and responses.
- **Comprehensive API Chaining:** Demonstrates stateful multi-endpoint workflow chaining (Create ➡️ Retrieve ➡️ Full Update ➡️ Partial Update ➡️ Delete ➡️ 404 Verification).
- **JSON Schema Validation:** Enforces strict structural contract compliance using REST Assured's `json-schema-validator` (Draft-07).
- **Allure Reporting:** Integrates `allure-testng` and `allure-rest-assured` filter to capture exact request/response headers, bodies, timestamps, and test steps.

---

## 🛠 Technology Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| **Java** | 17+ / 21+ | Primary programming language |
| **REST Assured** | 5.5.2 | HTTP client and fluent API assertion library |
| **TestNG** | 7.11.0 | Test execution engine, data providers, and assertions |
| **Jackson Databind** | 2.19.0 | POJO-to-JSON and JSON-to-POJO mapping |
| **JSON Schema Validator** | 5.5.2 | Contract & schema enforcement |
| **Allure Framework** | 2.29.1 | Interactive visual test reporting and attachments |
| **Apache Maven** | 3.9+ | Build management, dependency resolution, and test runner |
| **Git** | 2.x | Source version control |

---

## 📂 Project Structure

```text
api-automation-framework/
├── pom.xml                                      # Maven dependencies, plugins, and execution profiles
├── testng.xml                                   # Suite configuration with logical test groupings
├── .gitignore                                   # Standard VCS ignore rules
├── README.md                                    # Project documentation
├── src/test/java/
│   ├── auth/
│   │   └── AuthManager.java                     # Token generation, extraction, and caching
│   ├── base/
│   │   └── BaseTest.java                        # Base fixtures, SLA verification, and test hooks
│   ├── config/
│   │   └── ConfigReader.java                    # Classpath properties loader
│   ├── endpoints/
│   │   └── Endpoints.java                       # Centralized endpoint path constants
│   ├── models/
│   │   ├── AuthRequest.java                     # /auth payload POJO
│   │   ├── Booking.java                         # Booking entity POJO (with Builder)
│   │   ├── BookingDates.java                    # Booking dates child POJO
│   │   └── BookingResponse.java                 # POST /booking response wrapper POJO
│   ├── requests/
│   │   └── RequestSpec.java                     # RequestSpecification factory with Allure filter
│   └── tests/
│       ├── AuthenticationTest.java              # Auth positive & negative tests (TC01 - TC04)
│       ├── BookingCreateTest.java               # POJO, file-based, and DataProvider tests (TC05 - TC07)
│       ├── BookingGetTest.java                  # List, ID, filter, schema, and SLA tests (TC08 - TC12)
│       ├── BookingUpdateTest.java               # Full PUT update & persistence tests (TC13 - TC14)
│       ├── BookingPatchTest.java                # Partial PATCH update tests (TC15 - TC16)
│       ├── BookingDeleteTest.java               # Deletion & post-delete verification (TC17 - TC18)
│       ├── EndToEndBookingFlowTest.java         # Stateful API chaining lifecycle test (TC19)
│       └── NegativeScenariosTest.java           # 400, 403, 404, 405, 500 error tests (TC20 - TC28)
└── src/test/resources/
    ├── config.properties                        # Environment configuration and SLA thresholds
    ├── schemas/
    │   └── booking-schema.json                  # JSON Schema definition for booking validation
    └── testdata/
        └── booking.json                         # External JSON test fixture
```

---

## 🧪 Test Case Coverage Matrix (TC01 - TC30)

| ID | Test Class | Test Method | Description | Expected Status |
|---|---|---|---|---|
| **TC01** | `AuthenticationTest` | `testSuccessfulAuthentication_TC01` | Valid credentials generate auth token | `200 OK` |
| **TC02** | `AuthenticationTest` | `testAuthenticationInvalidPassword_TC02` | Invalid password returns error reason | `200 OK` (Live Quirk) |
| **TC03** | `AuthenticationTest` | `testAuthenticationInvalidUsername_TC03` | Non-existent username returns error reason | `200 OK` (Live Quirk) |
| **TC04** | `AuthenticationTest` | `testAuthenticationEmptyBody_TC04` | Empty payload returns error reason | `200 OK` (Live Quirk) |
| **TC05** | `BookingCreateTest` | `testCreateBookingWithPojo_TC05` | Create booking using typed Java POJO | `200 OK` (Live Quirk) |
| **TC06** | `BookingCreateTest` | `testCreateBookingFromJsonFile_TC06` | Create booking from `booking.json` resource | `200 OK` |
| **TC07a**| `BookingCreateTest` | `testCreateBookingDataDriven_TC07 [1]` | Data-driven dataset 1 (Sherlock Holmes) | `200 OK` |
| **TC07b**| `BookingCreateTest` | `testCreateBookingDataDriven_TC07 [2]` | Data-driven dataset 2 (John Watson) | `200 OK` |
| **TC07c**| `BookingCreateTest` | `testCreateBookingDataDriven_TC07 [3]` | Data-driven dataset 3 (Irene Adler) | `200 OK` |
| **TC08** | `BookingGetTest` | `testGetAllBookingIds_TC08` | Retrieve non-empty list of booking IDs | `200 OK` |
| **TC09** | `BookingGetTest` | `testGetBookingById_TC09` | Retrieve specific booking by ID & assert fields | `200 OK` |
| **TC10** | `BookingGetTest` | `testGetBookingFilterByName_TC10` | Filter bookings using query params | `200 OK` |
| **TC11** | `BookingGetTest` | `testGetBookingJsonSchemaValidation_TC11`| Validate schema against `booking-schema.json` | `200 OK` |
| **TC12** | `BookingGetTest` | `testGetBookingResponseTimeUpperBound_TC12`| Demonstration response-time SLA validation | `200 OK` |
| **TC13** | `BookingUpdateTest` | `testFullUpdateBookingPut_TC13` | Full update with `Cookie: token=...` | `200 OK` |
| **TC14** | `BookingUpdateTest` | `testFullUpdatePersisted_TC14` | Verify full update persisted via GET | `200 OK` |
| **TC15** | `BookingPatchTest` | `testPartialUpdateFirstnameAndNeeds_TC15` | Partial PATCH update of name & needs | `200 OK` |
| **TC16** | `BookingPatchTest` | `testPartialUpdatePriceAndDeposit_TC16` | Partial PATCH update of price & deposit | `200 OK` |
| **TC17** | `BookingDeleteTest` | `testDeleteBookingWithAuthToken_TC17` | DELETE booking with auth cookie | `201 Created` (Live Quirk) |
| **TC18** | `BookingDeleteTest` | `testVerifyBookingDeletedReturns404_TC18`| Verify deleted booking returns 404 | `404 Not Found` |
| **TC19** | `EndToEndBookingFlowTest` | `testCompleteBookingLifecycleE2E_TC19` | Chained E2E: POST ➡️ GET ➡️ PUT ➡️ GET ➡️ PATCH ➡️ GET ➡️ DELETE ➡️ GET 404 | Multiple chained steps |
| **TC20** | `NegativeScenariosTest` | `testGetNonExistentBookingIdReturns404_TC20` | GET nonexistent ID (999999999) | `404 Not Found` |
| **TC21** | `NegativeScenariosTest` | `testGetAlphanumericBookingIdReturns404_TC21`| GET non-numeric ID | `404 Not Found` |
| **TC22** | `NegativeScenariosTest` | `testPostMissingRequiredFieldsReturns500_TC22`| POST missing required fields | `500 Server Error` (Live Quirk) |
| **TC23** | `NegativeScenariosTest` | `testPostMalformedJsonReturns400_TC23` | POST malformed JSON syntax | `400 Bad Request` |
| **TC24** | `NegativeScenariosTest` | `testPutWithoutTokenReturns403_TC24` | PUT without auth cookie | `403 Forbidden` |
| **TC25** | `NegativeScenariosTest` | `testPutWithInvalidTokenReturns403_TC25` | PUT with garbage token | `403 Forbidden` |
| **TC26** | `NegativeScenariosTest` | `testDeleteWithoutTokenReturns403_TC26` | DELETE without auth cookie | `403 Forbidden` |
| **TC27** | `NegativeScenariosTest` | `testDeleteWithInvalidTokenReturns403_TC27` | DELETE with garbage token | `403 Forbidden` |
| **TC28** | `NegativeScenariosTest` | `testDeleteNonExistentBookingWithTokenReturns405_TC28` | DELETE non-existent booking with valid token | `405 Not Allowed` (Live Quirk) |

*Total: 30 test runs (including 3 DataProvider iterations).*

---

## 🔍 Observed Live API Quirks vs. Standard REST Assumptions

During live testing against the Heroku-hosted Restful Booker service, several discrepancies were observed. Building resilient automation requires validating real system behavior rather than blindly following specifications:

1. **`POST /auth` Bad Credentials Returns 200 OK:**
   Instead of returning `401 Unauthorized`, the endpoint returns `200 OK` with JSON `{"reason":"Bad credentials"}` and no token.
2. **`POST /booking` Returns 200 OK:**
   Standard REST conventions prescribe `201 Created` with a `Location` header, but Restful Booker returns `200 OK` with the response body `{"bookingid": ..., "booking": {...}}`.
3. **`DELETE /booking/{id}` Returns 201 Created:**
   Unlike standard `200 OK` or `204 No Content`, Restful Booker responds with `201 Created` and plaintext `Created`.
4. **`DELETE /booking/{nonexistent_id}` Returns 405 Method Not Allowed:**
   Attempting to delete a non-existent ID with a valid token yields `405 Method Not Allowed` rather than `404 Not Found`.
5. **`POST /booking` Missing Fields Returns 500 Internal Server Error:**
   Omitting required fields in the JSON body crashes the server-side validator, returning `500 Internal Server Error` instead of `400 Bad Request`.

---

## 🚀 Setup & Execution Guide (Windows / PowerShell)

### Prerequisites
- **JDK 17+ or 21+** installed and available on PATH (`java -version`).
- **Apache Maven 3.8+** installed and on PATH (`mvn -version`).
- **Git** installed (`git --version`).

### 1. Clone & Navigate to Project
```powershell
git clone <repository-url>
cd api-automation-framework
```

### 2. Execute Tests via Maven
Run the full TestNG suite configured in `testng.xml`:
```powershell
mvn clean test
```

### 3. Generate & Open Allure Report
After the tests execute, generate and view the interactive Allure HTML report:
```powershell
mvn allure:serve
```
*This command starts a lightweight local web server and automatically opens the report in your default browser.*

To generate a standalone static report in `target/site/allure-maven-plugin`:
```powershell
mvn allure:report
```
*(Note: `pom.xml` configures `<reportVersion>2.29.0</reportVersion>` because the standalone `allure-commandline` binary distribution was published as `2.29.0`, avoiding the Maven Central 404 artifact resolution error on `2.29.1.zip`).*

---

## 💼 Pairing with a UI Automation Framework (SDET Portfolio Context)

For a comprehensive QA Automation / SDET portfolio, this API automation framework pairs seamlessly with a Selenium or Playwright UI automation project:

1. **Test Pyramid Alignment:** 70% of regression and business logic testing runs quickly and reliably at the API layer (taking seconds with zero flakiness), while the UI suite focuses on end-to-end user journeys and visual rendering.
2. **Hybrid State Pre-Condition Setup:** In real-world enterprise automation, UI tests should not walk through 10 tedious form screens just to seed test data. Instead, this API framework's `BookingCreateTest` or `AuthManager` can be invoked via REST calls in UI `@BeforeMethod` hooks to instantly seed bookings and inject authentication cookies directly into the browser session, slashing UI test execution times by 80%.
3. **Dual-Layer Validation:** E2E tests can perform an action in the UI (e.g., booking a room in a web portal) and immediately verify backend state via REST Assured assertions against the database/API layer.
