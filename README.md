# Speechify Refactoring & LRU Cache Practice Assessment

This practice repository reproduces the exact structure and constraints of the Speechify Refactoring Assessment.

---

## 🛠️ Step 1: IntelliJ IDEA & JDK Setup

1. Open **IntelliJ IDEA**.
2. Click **Open** (or `File` → `Open`) and select:
   `/Users/vinithapukazhbagyar/personal-project/kotlin-practice`
3. IntelliJ will detect `build.gradle.kts`. If prompted to download or select a JDK:
   - Go to `File` → `Project Structure` → `Project` → **SDK**.
   - Click **Add SDK** → **Download JDK...** (Select **Amazon Corretto 17** or **Eclipse Temurin 17**).
4. Disable any AI plugins (Copilot, Tabnine) in `Settings` → `Plugins`.

---

## 📋 The Assessment Tasks

### Task 1: Refactor the Codebase for Structure & Readability (~30 min)
- **Inspect `practice.legacy.LegacyDocumentService`**:
  - Notice the **God Object**: it handles HTTP fetching, text cleanup, word counting, reading speed calculation, voice logic, and audio synthesis.
  - Notice the **Tight Coupling**: `RemoteApiClient` and `AudioSynthesizerEngine` are instantiated directly inside the service.
  - Notice the **Lack of Models**: returns an untyped `Map<String, Any>`.
- **Your Refactoring Goals**:
  1. Create typed domain models (`Document`, `ProcessedAudioDocument`).
  2. Extract data layer abstraction (`DocumentRepository`) and synthesis abstraction (`AudioSynthesizer`).
  3. Use **Constructor Injection** so dependencies are passed into classes, making them testable.
  4. Ensure existing business logic / behavior passes all unit tests.

### Task 2: Integrate LRU Cache into Data Layer (~20 min)
- **Inspect `practice.cache.LruCache`**:
  - An in-memory cache implementing `Cache<K, V>`.
- **Your Integration Goals**:
  1. Apply the **Cache-Aside / Decorator Pattern**: create a `CachedDocumentRepository` that wraps `DocumentRepository` and uses `Cache<String, Document>`.
  2. On `findById(id)`:
     - Check cache: if found, return cached item immediately (avoids hitting network API!).
     - If miss: fetch from the delegate repository, store in cache, and return.
  3. Write tests to prove:
     - Calling the service multiple times for the same document ID does **not** increase the API client call count.
     - LRU eviction works when adding items beyond the cache capacity.

---

## 📂 Project Organization

```
kotlin-practice/
├── build.gradle.kts
├── settings.gradle.kts
└── src/
    ├── main/kotlin/practice/
    │   ├── cache/
    │   │   └── LruCache.kt           <-- Provided cache interface & implementation
    │   ├── legacy/
    │   │   └── DocumentService.kt    <-- Legacy God-class starter code
    │   └── refactored/               <-- Model Solution
    │       ├── Models.kt             <-- Typed domain classes
    │       ├── DataLayer.kt          <-- Repository & CachedRepository
    │       └── DocumentAudioService.kt <-- Clean service
    └── test/kotlin/practice/
        ├── legacy/
        │   └── LegacyDocumentServiceTest.kt
        └── refactored/
            └── RefactoredDocumentServiceTest.kt
```

---

## ⏱️ Recommended 50-Minute Test Routine

1. **Minutes 0 - 5**: Read prompt carefully. Run existing tests to ensure they are green.
2. **Minutes 5 - 20**: Extract domain models and data source interfaces. Replace tight instantiations with constructor parameters.
3. **Minutes 20 - 30**: Break the God-class into clean single-responsibility methods/classes. Run tests. Git commit: `"Refactor: extract clean architecture layers"`.
4. **Minutes 30 - 45**: Implement cached repository using the provided LRU Cache. Add cache tests. Git commit: `"Feat: integrate LRU cache into data layer"`.
5. **Minutes 45 - 50**: Clean up unused imports, format code (`Cmd + Option + L`), verify all tests pass (`./gradlew test`), and `git push`.
