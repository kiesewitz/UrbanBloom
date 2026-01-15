# Frontend Development Prompts Catalog - UrbanBloom

**Purpose**: 
- Reusable prompts for Flutter mobile web development with CDD patterns
- Reusable prompts for React and Next.js web development
**Target Users**: Frontend developers (mobile and web)  
**Usage**: Copy prompt, fill placeholders, use with Mobile CDD Agent or Web Admin CDD Agent

---

## Component Design Prompts (Atomic Design)

### 1. Create Atom Component
```
Create an Atom component for [UI ELEMENT]:

Name: [COMPONENT_NAME]
Purpose: [WHAT IT DOES]
Variants: [LIST VARIANTS, e.g., primary/secondary/danger]
Props: [LIST PROPERTIES WITH TYPES]

Generate:
- StatelessWidget class (const constructor)
- Props as final fields
- No business logic (presentational only)
- No dependencies on other UI components
- Use design tokens from AppColors, AppSpacing, AppTypography
- Include documentation comments

Examples: Button, Text, Icon, Input Field, Badge
```

### 2. Create Molecule Component
```
Create a Molecule component for [COMBINATION]:

Name: [COMPONENT_NAME]
Atoms Used: [LIST 2-3 ATOMS]
Purpose: [WHAT IT DISPLAYS]
Props: [DATA IT RECEIVES]

Generate:
- StatelessWidget combining atoms
- Minimal local state (presentational only)
- Semantic structure (Row, Column, etc.)
- Spacing using AppSpacing constants
- Documentation with usage example

Examples: SearchBar, ListItem, CardHeader, FormField
```

### 3. Create Organism Component
```
Create an Organism component for [COMPLEX UI]:

Name: [COMPONENT_NAME]
Description: [WHAT IT DOES]
Data: [DOMAIN MODEL IT DISPLAYS]
Interactions: [USER ACTIONS, e.g., onTap, onSubmit]
State: [LOCAL STATE IF NEEDED, e.g., expanded/collapsed]

Generate:
- ConsumerWidget (for Riverpod integration) or StatefulWidget
- Combine molecules and atoms
- Handle user interactions
- Local state management (if needed)
- Accessibility (Semantics widget)
- Documentation with screenshots/description

Examples: ActionCard, UserProfile, ActionForm, LeaderboardList
```

### 4. Create Page Component
```
Create a Page for [SCREEN]:

Name: [PAGE_NAME]
Route: [URL PATH]
Purpose: [WHAT USER DOES HERE]
Data Sources: [RIVERPOD PROVIDERS]
Organisms: [ORGANISMS TO DISPLAY]
Navigation: [WHERE IT NAVIGATES TO]

Generate:
- @RoutePage annotation (go_router)
- ConsumerWidget
- Fetch data with ref.watch()
- AsyncValue handling (data/loading/error)
- Scaffold with AppBar
- Navigation logic
- Error handling with retry

Follow page structure from instructions.
```

---

## State Management Prompts (Riverpod)

### 5. Create Provider
```
Create a Riverpod provider for [DATA]:

Type: [Provider/FutureProvider/StreamProvider/NotifierProvider]
Data: [WHAT IT PROVIDES]
Dependencies: [OTHER PROVIDERS IT USES]
Caching: [SHOULD IT CACHE? AUTODISPOSE?]

Generate:
- @riverpod annotation
- Provider function/class
- Error handling
- Dependencies with ref.watch()
- Documentation

Examples:
- ApiClient provider (Provider)
- User actions list (FutureProvider)
- Connectivity status (StreamProvider)
- Cart state (NotifierProvider)
```

### 6. Create Notifier for Mutable State
```
Create a Notifier for [STATE]:

State Type: [DATA MODEL]
Initial State: [HOW TO INITIALIZE]
Mutations: [LIST METHODS TO CHANGE STATE]
Side Effects: [API CALLS, NAVIGATION, ETC.]

Generate:
- @riverpod class extending Notifier or AsyncNotifier
- build() method for initial state
- Mutation methods
- Optimistic updates (if applicable)
- Error handling with AsyncValue
- State persistence (if needed)

Example: Shopping cart, form state, filters
```

### 7. Optimize Provider with Select
```
Optimize this widget to listen only to [SPECIFIC FIELD]:

Current: ref.watch(userProvider)
Field Needed: [FIELD NAME, e.g., displayName]

Generate:
- ref.watch with .select()
- Prevents unnecessary rebuilds
- Type-safe field access

Example:
final name = ref.watch(userProvider.select((u) => u.displayName));
```

---

## API Integration Prompts

### 8. Generate Repository from OpenAPI
```
Create repository implementation for [ENTITY]:

OpenAPI Spec: openapi/urbanbloom-api-v1.yaml
Entity: [e.g., Action, User, Challenge]
Operations: [LIST CRUD OPERATIONS]

Generate:
- Repository interface (in domain/repositories/)
- Repository implementation (in data/repositories/)
- Use generated API client
- Offline-first pattern: Read from cache, fetch from API, update cache
- Error handling with Result type
- Retry logic for transient failures

Follow repository pattern from instructions.
```

### 9. Implement Offline Sync
```
Implement offline sync for [ENTITY]:

Entity: [e.g., Action]
Operations to Queue: [CREATE/UPDATE/DELETE]
Sync Trigger: [ON CONNECTIVITY CHANGE]

Generate:
- Sync queue table (Drift)
- Queue insertion on offline operation
- Sync manager to process queue
- Retry logic with exponential backoff
- Mark as synced after success
- Handle conflicts (last-write-wins or custom)

Follow offline-first architecture from instructions.
```

### 10. Create API Interceptor
```
Create HTTP interceptor for [PURPOSE]:

Purpose: [e.g., JWT authentication, logging, error handling]
Trigger: [ALL REQUESTS / SPECIFIC ENDPOINTS]

Generate:
- Interceptor class implementing Interceptor (Dio)
- onRequest: Modify outgoing requests
- onResponse: Process responses
- onError: Handle errors, retry if needed
- Configuration in API client provider

Examples: AuthInterceptor, LoggingInterceptor, RetryInterceptor
```

---

## Mock Integration & Testing Prompts

### 11. Generate OpenAPI Mock Server
```
Generate mock API server from OpenAPI spec:

OpenAPI Spec: openapi/urbanbloom-api-v1.yaml
Endpoints: [LIST ENDPOINTS TO MOCK, or 'ALL']
Delay: [SIMULATED LATENCY IN MS, e.g., 200ms]
Error Rate: [% OF REQUESTS TO FAIL, e.g., 5]

Generate using @openapitools/openapi-generator-cli:
- npx @openapitools/openapi-generator-cli generate -g nodejs -i openapi/urbanbloom-api-v1.yaml
- Mock server at http://localhost:3000
- Realistic response data from examples in OpenAPI spec
- Dynamic mock responses based on request parameters
- Error simulation for testing error handling

Documentation: Point to mock server README and Swagger UI at /api-docs
```

### 12. Create Mock API Client for Testing
```
Create mock API client for unit testing [ENTITY]:

Entity: [e.g., Action, User]
Repository: [REPOSITORY_NAME]
Mock Scenarios: [LIST SCENARIOS, e.g., success, 404 error, timeout]

Generate:
- MockApiClient extending ApiClient interface
- Stub methods returning predefined responses
- Configurable delays for async testing
- Error simulation (404, 500, timeout)
- Request tracking for verification
- Scenario management (switch between mock responses)

Usage in tests:
- Inject MockApiClient into repository
- Verify API calls were made correctly
- Test error handling paths
- No network dependencies

Examples: MockActionApiClient, MockUserApiClient
```

### 13. Test Component Against Mock API
```
Write widget test for [COMPONENT] with mock API:

Component: [COMPONENT_NAME]
Mock Scenarios: [LIST SCENARIOS TO TEST]
Expected UI States: [LOADING, SUCCESS, ERROR]

Generate:
- testWidgets() test function
- Mock API client setup with scenarios
- Pump widget with mocked repository
- Verify UI renders correctly for each scenario
  - Loading state: spinner/skeleton visible
  - Success state: data displayed correctly
  - Error state: error message shown, retry available
- User interactions: tap, scroll, input
- Verify no real API calls made (pump doesn't make network requests)

Example scenarios:
- Load actions list: Mock returns 3 actions
- 404 error: Mock returns NOT_FOUND, verify error message
- Network timeout: Mock simulates 5000ms delay, verify timeout handling
```

### 14. Mock OpenAPI Endpoints for Feature Development
```
Mock specific OpenAPI endpoints for development:

Endpoints to Mock: [LIST ENDPOINTS]
Example Data: [SAMPLE REQUEST/RESPONSE JSON]
Behavior: [REAL, DELAYED, ERROR, etc.]

Setup:
1. Generate mock server from spec: npx @openapitools/openapi-generator-cli generate -g nodejs ...
2. Configure endpoint behaviors in mock server
3. Point app to mock server (configurable base URL)
4. Commit mock configuration for team consistency

Benefits:
- Frontend development independent of backend
- Test error scenarios easily
- Consistent mock data across team
- Realistic latency simulation

Command:
flutter run --dart-define=API_BASE_URL=http://localhost:3000
```

### 15. Integration Test Against Mock Server
```
Write integration test [TEST_NAME] against mock API:

Test Scenario: [USER FLOW DESCRIPTION]
Endpoints Mocked: [LIST ENDPOINTS INVOLVED]
Mock Duration: [TEST TIMEOUT, e.g., 30 seconds]

Generate using patrol:
- Mock server started in setUp()
- Integration test simulates user actions
- Verify UI updates correctly with mock responses
- Test offline fallback to cached data
- Verify error recovery (retry, fallback)
- Clean mock server in tearDown()

Example: "User creates action, sees confirmation, action syncs in background"

Follow integration test pattern from instructions.
```

---

## Local Database Prompts (Drift)

### 16. Create Drift Table
```
Create Drift table for [ENTITY]:

Entity: [NAME]
Properties: [LIST WITH TYPES]
Primary Key: [FIELD]
Indexes: [FIELDS TO INDEX]
Foreign Keys: [RELATIONSHIPS]

Generate:
- Table class extending Table
- Column definitions with types and constraints
- @DataClassName annotation
- primaryKey override
- indexes (if needed)

Add to @DriftDatabase annotation.
```

### 17. Create Data Access Object (DAO)
```
Create DAO for [ENTITY]:

Table: [TABLE NAME]
Queries Needed:
- [e.g., getAll, getById, insert, update, delete]
- [Custom queries with filtering/sorting]

Generate:
- DAO class extending DatabaseAccessor
- @DriftAccessor annotation
- Query methods using drift DSL
- Batch operations for bulk inserts
- Stream queries for reactive UI

Follow DAO pattern from instructions.
```

---

## Navigation Prompts

### 18. Define Type-Safe Route
```
Create route for [PAGE]:

Path: [URL PATH, e.g., /actions/:id]
Parameters: [PATH PARAMS, QUERY PARAMS]
Page: [PAGE WIDGET]

Generate:
- @TypedGoRoute annotation
- Route data class extending GoRouteData
- Path parameters as fields
- build() method returning page widget
- Type-safe navigation methods

Add to router configuration.
```

### 19. Implement Navigation Guard
```
Add navigation guard for [CONDITION]:

Condition: [e.g., User must be authenticated]
Redirect: [WHERE TO REDIRECT IF CONDITION FAILS]

Generate:
- Redirect logic in GoRouter configuration
- Check condition (read from provider)
- Redirect to login/error page if needed
- Preserve original destination for redirect-after-login

Example: Auth guard, role-based access
```

---

## Form & Validation Prompts

### 20. Create Form Widget
```
Create form for [ENTITY]:

Entity: [e.g., Action, User]
Fields: [LIST FIELDS WITH TYPES]
Validation Rules: [FIELD-SPECIFIC RULES]
Submit Action: [WHAT HAPPENS ON SUBMIT]

Generate:
- StatefulWidget with Form and GlobalKey<FormState>
- TextFormField for each field
- Validators matching backend constraints
- Submit button with loading state
- Error display
- Keyboard handling (TextInputAction)

Follow form best practices from instructions.
```

### 16. Implement Field Validation
```
Create validator for [FIELD]:

Field: [e.g., Email, Password, Phone]
Rules: [VALIDATION RULES]
Error Messages: [USER-FRIENDLY MESSAGES]

Generate:
- Validator function (String? Function(String?))
- Check all rules (required, format, length, etc.)
- Return error message or null
- Match backend validation rules

Use in TextFormField validator parameter.
```

---

## Testing Prompts

### 17. Create Widget Test
```
Generate widget test for [COMPONENT]:

Component: [COMPONENT NAME]
Test Scenario: [WHAT TO TEST]
Props: [TEST DATA]
Interactions: [USER INTERACTIONS TO TEST]

Generate:
- testWidgets() function
- Pump widget with MaterialApp wrapper
- find.* locators
- expect() assertions
- User interaction simulation (tap, drag, etc.)
- Verify UI updates correctly

Use flutter_test package.
```

### 18. Create Golden Test
```
Generate golden test for [COMPONENT]:

Component: [COMPONENT NAME]
Variants: [STATES TO TEST, e.g., default, loading, error]

Generate:
- testWidgets() for each variant
- Pump widget with test data
- matchesGoldenFile() assertion
- Golden file path: goldens/[component]_[variant].png

Run: flutter test --update-goldens to generate initial images.
```

### 19. Create Integration Test
```
Generate integration test (patrol) for [USER FLOW]:

Flow: [DESCRIPTION, e.g., "User creates action"]
Steps: [LIST STEPS]
Expected Outcome: [SUCCESS CRITERIA]

Generate:
- patrolTest() function
- Pump app with test configuration
- Navigate through flow with $() syntax
- Enter text, tap buttons, etc.
- Verify final state

Use patrol package for reliable testing.
```

---

## Performance Optimization Prompts

### 20. Optimize Image Loading
```
Optimize image loading for [COMPONENT]:

Images: [NETWORK/LOCAL/ASSET]
Size: [DISPLAY SIZE]
Caching: [CACHE STRATEGY]

Generate:
- Image.network with cacheWidth/cacheHeight
- Placeholder while loading
- Error widget for failures
- Cached network image (if using package)
- Lazy loading for lists

Reduce memory usage by resizing images.
```

### 21. Implement Lazy Loading List
```
Implement infinite scroll for [LIST]:

Data Source: [PROVIDER]
Page Size: [e.g., 20 items]
Load Trigger: [SCROLL POSITION]

Generate:
- ListView.builder for virtual scrolling
- ScrollController to detect end of list
- Pagination logic in provider
- Loading indicator at bottom
- Error handling with retry

Avoid loading all data at once.
```

### 22. Add Code Splitting
```
Implement deferred loading for [FEATURE]:

Feature: [HEAVY FEATURE, e.g., Analytics, Admin Panel]
Trigger: [WHEN TO LOAD]

Generate:
- Import with 'deferred as' keyword
- loadLibrary() call before use
- Loading indicator during load
- Error handling if load fails

Use for large features to reduce initial bundle size.
```

---

## Accessibility Prompts

### 23. Add Semantic Labels
```
Add accessibility to [COMPONENT]:

Component: [NAME]
Interactive Elements: [BUTTONS, INPUTS, ETC.]
Purpose: [WHAT IT DOES]

Generate:
- Semantics widget wrappers
- label: Description of element
- button: true for buttons
- textField: true for inputs
- hint: Guidance for user
- excludeSemantics: true to hide decorative elements

Test with screen reader (TalkBack/VoiceOver).
```

### 24. Ensure Color Contrast
```
Verify color contrast for [COMPONENT]:

Component: [NAME]
Text Colors: [LIST]
Background Colors: [LIST]

Check:
- WCAG AA compliance (4.5:1 for normal text)
- Use online contrast checker
- Provide alternative if fails

Suggest color adjustments if needed.
```

---

## Styling & Theming Prompts

### 25. Create Theme-Aware Widget
```
Make [COMPONENT] respect theme:

Component: [NAME]
Theme Properties: [COLORS, FONTS, SPACING]

Generate:
- Use Theme.of(context) for theme data
- Use MediaQuery for responsive sizing
- Support light/dark mode
- Use design tokens (AppColors, etc.)

Component should work in any theme.
```

### 26. Implement Responsive Layout
```
Make [COMPONENT] responsive:

Component: [NAME]
Breakpoints: [MOBILE: <600, TABLET: 600-900, DESKTOP: >900]
Layout Changes: [DESCRIBE ADAPTATIONS]

Generate:
- LayoutBuilder for width detection
- Conditional layouts based on width
- Responsive spacing and font sizes
- Test on different screen sizes

Use MediaQuery.of(context).size.width.
```

---

## Animation Prompts

### 27. Add Transition Animation
```
Add animation to [COMPONENT]:

Component: [NAME]
Animation Type: [FADE/SLIDE/SCALE/CUSTOM]
Duration: [MILLISECONDS]
Curve: [CURVE TYPE]

Generate:
- AnimationController in StatefulWidget
- Tween for animation values
- AnimatedBuilder or implicit animation widget
- initState() and dispose() for controller
- Trigger animation on [EVENT]

Keep animations subtle and performant.
```

---

## Web-Specific Prompts (Flutter Web)

### 28. Create Data Table with Sorting
```
Create sortable data table for [ENTITY]:

Entity: [NAME]
Columns: [LIST WITH TYPES]
Sorting: [WHICH COLUMNS ARE SORTABLE]
Actions: [ROW ACTIONS, e.g., view, edit, delete]

Generate:
- PaginatedDataTable or DataTable
- DataTableSource implementation
- Sorting logic in provider
- Action buttons in DataCell
- Responsive column widths

Follow data table pattern from web instructions.
```

### 29. Implement CSV Export
```
Add CSV export for [DATA]:

Data: [ENTITY]
Columns: [COLUMNS TO EXPORT]
Trigger: [BUTTON CLICK]

Generate:
- Export function using csv package
- Escape special characters
- Create Blob and download (dart:html)
- Filename with timestamp
- Loading indicator during export

Web-only feature (check kIsWeb).
```

---

## Error Handling Prompts

### 30. Create Error Widget
```
Create reusable error widget:

Props:
- error: Exception/String
- stackTrace: StackTrace (optional)
- onRetry: VoidCallback

Generate:
- Widget displaying error message
- Retry button
- Friendly message for common errors (network, timeout, etc.)
- Technical details in debug mode only
- Icon indicating error type

Use across the app for consistent error UI.
```

### 31. Implement Retry Logic
```
Add retry logic for [OPERATION]:

Operation: [e.g., API call, image load]
Max Retries: [e.g., 3]
Backoff Strategy: [EXPONENTIAL/LINEAR]

Generate:
- Retry function with attempt counter
- Delay between retries (exponential backoff)
- Give up after max retries
- Log retry attempts
- Return error if all retries fail

Use for transient failures (network issues).
```

---

## UI/UX Enhancement Prompts

### 32. Add Pull-to-Refresh
```
Add pull-to-refresh to [LIST PAGE]:

Page: [NAME]
Data Provider: [PROVIDER TO REFRESH]

Generate:
- RefreshIndicator wrapper
- onRefresh callback
- ref.refresh() to reload data
- Loading indicator
- Success/error feedback

Standard mobile pattern for data refresh.
```

### 33. Implement Search with Debouncing
```
Add search functionality to [PAGE]:

Page: [NAME]
Search Field: [WHAT TO SEARCH]
Debounce: [MILLISECONDS, e.g., 300ms]

Generate:
- TextField for search input
- Debouncing with Timer
- Update provider with search query
- Clear button
- Search icon
- Loading indicator during search

Avoid excessive API calls with debouncing.
```

### 34. Add Empty State
```
Create empty state for [LIST]:

Context: [WHEN LIST IS EMPTY]
Message: [USER-FRIENDLY MESSAGE]
Action: [CTA BUTTON, e.g., "Create First Action"]

Generate:
- Widget with icon, message, and CTA
- Display when list.isEmpty
- Encouraging tone
- Navigation to creation screen

Better UX than blank screen.
```

---

## Platform-Specific Prompts

### 35. Implement Platform-Specific UI
```
Create platform-specific UI for [FEATURE]:

Feature: [NAME]
iOS Behavior: [DESCRIBE]
Android Behavior: [DESCRIBE]

Generate:
- Platform detection (Theme.of(context).platform)
- Cupertino widgets for iOS
- Material widgets for Android
- Consistent behavior despite different UI

Use Platform.isIOS, Platform.isAndroid.
```

---

## Related Files
- **Frontend Mobile Instructions**: `.github/instructions/frontend-mobile-instructions.md`
- **Frontend Web Instructions**: `.github/instructions/frontend-web-instructions.md`
- **Mobile CDD Agent**: `.github/agents/mobile-cdd.agent.md`
- **Web Admin CDD Agent**: `.github/agents/web-admin-cdd.agent.md`
- **Design Tokens**: `shared-resources/design-tokens/tokens.json`
- **OpenAPI Spec**: `openapi/urbanbloom-api-v1.yaml`
