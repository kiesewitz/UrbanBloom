# Frontend Mobile Development Instructions - UrbanBloom

**Technology Stack**: Flutter 3.16+, Dart 3.2+, Riverpod, Drift  
**Architectural Approach**: Component-Driven Design (CDD) with Atomic Design  
**Target Platforms**: iOS & Android  
**Target Audience**: Mobile developers (1-2 people)

---

## Component-Driven Design (CDD) - Atomic Design

### Directory Structure (Mandatory)

```
lib/
├── ui/                              # UI Components (Atomic Design)
│   ├── atoms/                       # Basic UI elements
│   │   ├── action_button.dart       # Primary/Secondary buttons
│   │   ├── heading_text.dart        # Typography components
│   │   ├── icon_badge.dart          # Badge icons
│   │   └── plant_image.dart         # Plant image widget
│   ├── molecules/                   # Simple combinations (2-3 atoms)
│   │   ├── action_card_header.dart  # Card header with icon+title
│   │   ├── user_avatar_with_name.dart
│   │   └── points_display.dart      # Points counter with icon
│   ├── organisms/                   # Complex components
│   │   ├── action_card.dart         # Full action card
│   │   ├── action_form.dart         # Action creation form
│   │   ├── leaderboard_list.dart    # Leaderboard with infinite scroll
│   │   └── challenge_card.dart      # Challenge display card
│   ├── templates/                   # Page layouts
│   │   ├── main_layout.dart         # App scaffold with nav
│   │   └── form_layout.dart         # Standard form layout
│   └── pages/                       # Full screens
│       ├── home_page.dart           # Home screen
│       ├── profile_page.dart        # User profile
│       ├── action_details_page.dart # Action details
│       └── create_action_page.dart  # Action creation
├── domain/                          # Domain models & business logic
│   ├── models/                      # Domain models
│   │   ├── action.dart              # Action model
│   │   ├── user.dart                # User model
│   │   ├── plant.dart               # Plant model
│   │   └── challenge.dart           # Challenge model
│   └── repositories/                # Repository interfaces
│       ├── action_repository.dart
│       ├── user_repository.dart
│       └── plant_repository.dart
├── data/                            # Data layer (infrastructure)
│   ├── api/                         # API client (generated from OpenAPI)
│   │   ├── api_client.dart          # Generated API client
│   │   └── interceptors/            # HTTP interceptors
│   │       └── auth_interceptor.dart
│   ├── local/                       # Local database (Drift)
│   │   ├── database.dart            # Drift database
│   │   ├── dao/                     # Data Access Objects
│   │   │   ├── action_dao.dart
│   │   │   └── user_dao.dart
│   │   └── tables/                  # Database tables
│   │       ├── actions_table.dart
│   │       └── users_table.dart
│   ├── sync/                        # Offline sync logic
│   │   ├── sync_manager.dart        # Sync coordinator
│   │   └── sync_queue.dart          # Queue for offline actions
│   └── repositories/                # Repository implementations
│       └── action_repository_impl.dart
├── providers/                       # Riverpod providers (state management)
│   ├── auth_provider.dart           # Authentication state
│   ├── action_provider.dart         # Action data
│   ├── user_provider.dart           # User data
│   └── connectivity_provider.dart   # Network connectivity
├── core/                            # Core utilities
│   ├── theme/                       # Theme & design tokens
│   │   ├── app_theme.dart
│   │   ├── colors.dart              # Design tokens (from shared-resources)
│   │   └── typography.dart
│   ├── utils/                       # Utility functions
│   │   ├── date_formatter.dart
│   │   └── validators.dart
│   └── constants/                   # App constants
│       └── api_constants.dart
└── main.dart                        # App entry point
```

---

## Atomic Design Principles

### Atoms (Basic Building Blocks)
- **NO dependencies** on other UI components
- **Highly reusable** across the app
- **Props only** (no business logic)
- **Examples**: Buttons, text, icons, input fields

**Example: ActionButton Atom**
```dart
import 'package:flutter/material.dart';

enum ActionButtonVariant { primary, secondary, danger }

class ActionButton extends StatelessWidget {
  const ActionButton({
    required this.onPressed,
    required this.child,
    this.variant = ActionButtonVariant.primary,
    this.isLoading = false,
    super.key,
  });

  final VoidCallback? onPressed;
  final Widget child;
  final ActionButtonVariant variant;
  final bool isLoading;

  @override
  Widget build(BuildContext context) {
    return ElevatedButton(
      onPressed: isLoading ? null : onPressed,
      style: _getButtonStyle(variant),
      child: isLoading
          ? const SizedBox(
              height: 20,
              width: 20,
              child: CircularProgressIndicator(strokeWidth: 2),
            )
          : child,
    );
  }

  ButtonStyle _getButtonStyle(ActionButtonVariant variant) {
    switch (variant) {
      case ActionButtonVariant.primary:
        return ElevatedButton.styleFrom(
          backgroundColor: AppColors.primary,
          foregroundColor: Colors.white,
        );
      case ActionButtonVariant.secondary:
        return ElevatedButton.styleFrom(
          backgroundColor: AppColors.secondary,
          foregroundColor: Colors.black87,
        );
      case ActionButtonVariant.danger:
        return ElevatedButton.styleFrom(
          backgroundColor: AppColors.error,
          foregroundColor: Colors.white,
        );
    }
  }
}
```

### Molecules (Simple Combinations)
- **Combine 2-3 atoms**
- **Minimal state** (presentational only)
- **Examples**: Card headers, list items, search bars

**Example: UserAvatarWithName Molecule**
```dart
class UserAvatarWithName extends StatelessWidget {
  const UserAvatarWithName({
    required this.user,
    this.size = 40,
    super.key,
  });

  final User user;
  final double size;

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        CircleAvatar(
          radius: size / 2,
          backgroundImage: user.avatarUrl != null
              ? NetworkImage(user.avatarUrl!)
              : null,
          child: user.avatarUrl == null
              ? Text(user.displayName[0].toUpperCase())
              : null,
        ),
        const SizedBox(width: 12),
        Text(
          user.displayName,
          style: Theme.of(context).textTheme.bodyLarge,
        ),
      ],
    );
  }
}
```

### Organisms (Complex Components)
- **Can have local state** (e.g., form state, expansion state)
- **Combine molecules and atoms**
- **Business logic** can be present (but prefer delegating to providers)
- **Examples**: Forms, data tables, complex cards

**Example: ActionCard Organism**
```dart
class ActionCard extends ConsumerWidget {
  const ActionCard({
    required this.action,
    this.onTap,
    super.key,
  });

  final Action action;
  final VoidCallback? onTap;

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    return Card(
      clipBehavior: Clip.antiAlias,
      child: InkWell(
        onTap: onTap,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Image (Atom)
            if (action.photoUrl != null)
              AspectRatio(
                aspectRatio: 16 / 9,
                child: Image.network(
                  action.photoUrl!,
                  fit: BoxFit.cover,
                ),
              ),
            Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  // Header (Molecule)
                  ActionCardHeader(
                    title: action.title,
                    icon: _getActionIcon(action.type),
                  ),
                  const SizedBox(height: 8),
                  // Description (Atom)
                  Text(
                    action.description,
                    style: Theme.of(context).textTheme.bodyMedium,
                    maxLines: 2,
                    overflow: TextOverflow.ellipsis,
                  ),
                  const SizedBox(height: 12),
                  // Footer with points and date
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      PointsDisplay(points: action.pointsEarned),
                      Text(
                        DateFormatter.formatRelative(action.createdAt),
                        style: Theme.of(context).textTheme.caption,
                      ),
                    ],
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  IconData _getActionIcon(ActionType type) {
    switch (type) {
      case ActionType.plantTree:
        return Icons.eco;
      case ActionType.cleanArea:
        return Icons.cleaning_services;
      case ActionType.recycling:
        return Icons.recycling;
    }
  }
}
```

### Templates (Page Layouts)
- **Layout only** (no business logic)
- **Reusable scaffolds** for pages
- **Examples**: Main layout with nav, form layout

**Example: MainLayout Template**
```dart
class MainLayout extends StatelessWidget {
  const MainLayout({
    required this.body,
    this.title,
    this.floatingActionButton,
    this.showBackButton = false,
    super.key,
  });

  final Widget body;
  final String? title;
  final Widget? floatingActionButton;
  final bool showBackButton;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: title != null
          ? AppBar(
              title: Text(title!),
              leading: showBackButton ? const BackButton() : null,
            )
          : null,
      body: SafeArea(child: body),
      floatingActionButton: floatingActionButton,
      bottomNavigationBar: const BottomNavBar(),
    );
  }
}
```

### Pages (Full Screens)
- **Connect to providers** (Riverpod)
- **Orchestrate organisms**
- **Handle navigation**
- **Examples**: Home page, profile page, detail pages

**Example: HomePage**
```dart
@RoutePage()
class HomePage extends ConsumerWidget {
  const HomePage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final asyncActions = ref.watch(recentActionsProvider);

    return MainLayout(
      title: 'UrbanBloom',
      floatingActionButton: FloatingActionButton(
        onPressed: () => context.router.push(const CreateActionRoute()),
        child: const Icon(Icons.add),
      ),
      body: asyncActions.when(
        data: (actions) => _buildActionsList(actions),
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (error, stack) => ErrorWidget.withRetry(
          message: 'Failed to load actions',
          onRetry: () => ref.refresh(recentActionsProvider),
        ),
      ),
    );
  }

  Widget _buildActionsList(List<Action> actions) {
    return ListView.builder(
      itemCount: actions.length,
      itemBuilder: (context, index) {
        final action = actions[index];
        return ActionCard(
          action: action,
          onTap: () => context.router.push(ActionDetailsRoute(id: action.id)),
        );
      },
    );
  }
}
```

---

## State Management with Riverpod

### Provider Types

**1. Provider** (Immutable data):
```dart
@riverpod
ApiClient apiClient(ApiClientRef ref) {
  final authToken = ref.watch(authTokenProvider);
  return ApiClient(baseUrl: ApiConstants.baseUrl, token: authToken);
}
```

**2. FutureProvider** (Async data):
```dart
@riverpod
Future<List<Action>> recentActions(RecentActionsRef ref) async {
  final repository = ref.watch(actionRepositoryProvider);
  return repository.getRecentActions(limit: 10);
}
```

**3. StreamProvider** (Real-time data):
```dart
@riverpod
Stream<ConnectivityStatus> connectivity(ConnectivityRef ref) {
  return ConnectivityService().statusStream;
}
```

**4. NotifierProvider** (Mutable state):
```dart
@riverpod
class UserActions extends _$UserActions {
  @override
  Future<List<Action>> build(String userId) async {
    return _fetchActions(userId);
  }

  Future<void> refresh() async {
    state = const AsyncValue.loading();
    state = await AsyncValue.guard(() => _fetchActions(userId));
  }

  Future<List<Action>> _fetchActions(String userId) async {
    final repository = ref.read(actionRepositoryProvider);
    return repository.getUserActions(userId);
  }
}
```

### Provider Usage Patterns

**Reading Providers**:
```dart
// In ConsumerWidget
final asyncValue = ref.watch(recentActionsProvider);

// Listen to specific field only (optimization)
final count = ref.watch(userProvider.select((user) => user.actionCount));

// One-time read (no rebuild on change)
final repository = ref.read(actionRepositoryProvider);
```

**Handling AsyncValue**:
```dart
asyncValue.when(
  data: (data) => DataWidget(data),
  loading: () => const CircularProgressIndicator(),
  error: (error, stack) => ErrorWidget(error),
);

// Or with pattern matching
switch (asyncValue) {
  case AsyncData(:final value):
    return DataWidget(value);
  case AsyncError(:final error):
    return ErrorWidget(error);
  default:
    return const CircularProgressIndicator();
}
```

---

## API Integration (OpenAPI-Generated Client)

### Step 1: Generate Dart Client
```bash
openapi-generator generate \
  -i openapi/urbanbloom-api-v1.yaml \
  -g dart-dio \
  -o mobile/lib/data/api/generated \
  --additional-properties=pubName=urbanbloom_api
```

### Step 2: Wrap in Repository Pattern
```dart
abstract class ActionRepository {
  Future<List<Action>> getRecentActions({int limit = 10});
  Future<Action> getActionById(String id);
  Future<Action> createAction(CreateActionRequest request);
  Future<void> deleteAction(String id);
}

class ActionRepositoryImpl implements ActionRepository {
  ActionRepositoryImpl({
    required this.apiClient,
    required this.localDatabase,
    required this.syncManager,
  });

  final ActionApi apiClient;  // Generated from OpenAPI
  final ActionDao localDatabase;
  final SyncManager syncManager;

  @override
  Future<List<Action>> getRecentActions({int limit = 10}) async {
    // 1. Return cached data immediately (offline-first)
    final cached = await localDatabase.getRecentActions(limit);

    // 2. Fetch from API in background
    try {
      final remote = await apiClient.getActions(limit: limit);
      final actions = remote.map((dto) => Action.fromDto(dto)).toList();

      // 3. Update cache
      await localDatabase.insertActions(actions);

      return actions;
    } catch (e) {
      // 4. Return cached data if API fails (offline support)
      if (cached.isNotEmpty) {
        return cached;
      }
      rethrow;
    }
  }

  @override
  Future<Action> createAction(CreateActionRequest request) async {
    // Try to send to API
    try {
      final dto = await apiClient.createAction(request.toDto());
      final action = Action.fromDto(dto);

      // Save to local DB
      await localDatabase.insertAction(action);

      return action;
    } catch (e) {
      // Queue for later sync if offline
      await syncManager.queueAction(request);
      rethrow;
    }
  }
}
```

### Step 3: Provide Repository
```dart
@riverpod
ActionRepository actionRepository(ActionRepositoryRef ref) {
  return ActionRepositoryImpl(
    apiClient: ref.watch(apiClientProvider).getActionsApi(),
    localDatabase: ref.watch(databaseProvider).actionDao,
    syncManager: ref.watch(syncManagerProvider),
  );
}
```

---

## Offline-First Architecture

### Local Database (Drift)

**Define Tables**:
```dart
@DataClassName('ActionData')
class Actions extends Table {
  TextColumn get id => text()();
  TextColumn get title => text().withLength(min: 1, max: 200)();
  TextColumn get description => text()();
  TextColumn get photoUrl => text().nullable()();
  IntColumn get pointsEarned => integer()();
  TextColumn get creatorId => text()();
  DateTimeColumn get createdAt => dateTime()();

  @override
  Set<Column> get primaryKey => {id};
}

@DriftDatabase(tables: [Actions, Users, Plants])
class AppDatabase extends _$AppDatabase {
  AppDatabase() : super(_openConnection());

  @override
  int get schemaVersion => 1;

  static QueryExecutor _openConnection() {
    return NativeDatabase.createInBackground(File('urbanbloom.db'));
  }
}
```

**Data Access Object (DAO)**:
```dart
@DriftAccessor(tables: [Actions])
class ActionDao extends DatabaseAccessor<AppDatabase> with _$ActionDaoMixin {
  ActionDao(AppDatabase db) : super(db);

  Future<List<ActionData>> getRecentActions(int limit) {
    return (select(actions)
          ..orderBy([(t) => OrderingTerm.desc(t.createdAt)])
          ..limit(limit))
        .get();
  }

  Future<void> insertAction(ActionData action) {
    return into(actions).insertOnConflictUpdate(action);
  }

  Future<void> insertActions(List<ActionData> actionList) {
    return batch((batch) {
      batch.insertAllOnConflictUpdate(actions, actionList);
    });
  }
}
```

### Sync Strategy

**Sync Manager**:
```dart
class SyncManager {
  SyncManager({
    required this.connectivity,
    required this.syncQueue,
    required this.apiClient,
  });

  final ConnectivityService connectivity;
  final SyncQueue syncQueue;
  final ApiClient apiClient;

  Future<void> syncPendingActions() async {
    if (!await connectivity.isConnected) return;

    final pending = await syncQueue.getPendingActions();

    for (final action in pending) {
      try {
        await apiClient.createAction(action.toDto());
        await syncQueue.markAsSynced(action.id);
      } catch (e) {
        // Retry later
        await syncQueue.incrementRetryCount(action.id);
      }
    }
  }

  void startAutoSync() {
    connectivity.statusStream.listen((status) {
      if (status == ConnectivityStatus.connected) {
        syncPendingActions();
      }
    });
  }
}
```

**Provider**:
```dart
@riverpod
SyncManager syncManager(SyncManagerRef ref) {
  final manager = SyncManager(
    connectivity: ref.watch(connectivityServiceProvider),
    syncQueue: ref.watch(syncQueueProvider),
    apiClient: ref.watch(apiClientProvider),
  );
  manager.startAutoSync();
  return manager;
}
```

---

## Design Tokens (from shared-resources)

**Import Design Tokens**:
```dart
// lib/core/theme/colors.dart
class AppColors {
  // Primary colors
  static const primary = Color(0xFF4CAF50);      // Green
  static const primaryDark = Color(0xFF388E3C);
  static const primaryLight = Color(0xFF81C784);

  // Secondary colors
  static const secondary = Color(0xFF8BC34A);    // Light Green
  static const accent = Color(0xFFFF9800);       // Orange

  // Neutral colors
  static const background = Color(0xFFF5F5F5);
  static const surface = Color(0xFFFFFFFF);
  static const error = Color(0xFFD32F2F);

  // Text colors
  static const textPrimary = Color(0xFF212121);
  static const textSecondary = Color(0xFF757575);
  static const textHint = Color(0xFF9E9E9E);
}

// lib/core/theme/spacing.dart
class AppSpacing {
  static const double xs = 4.0;
  static const double sm = 8.0;
  static const double md = 16.0;
  static const double lg = 24.0;
  static const double xl = 32.0;
  static const double xxl = 48.0;
}

// lib/core/theme/typography.dart
class AppTypography {
  static const TextStyle h1 = TextStyle(
    fontSize: 32,
    fontWeight: FontWeight.bold,
    height: 1.2,
  );

  static const TextStyle h2 = TextStyle(
    fontSize: 24,
    fontWeight: FontWeight.bold,
    height: 1.3,
  );

  static const TextStyle body = TextStyle(
    fontSize: 16,
    fontWeight: FontWeight.normal,
    height: 1.5,
  );

  static const TextStyle caption = TextStyle(
    fontSize: 12,
    fontWeight: FontWeight.normal,
    height: 1.4,
  );
}
```

**Apply Theme**:
```dart
ThemeData get lightTheme => ThemeData(
  useMaterial3: true,
  colorScheme: ColorScheme.fromSeed(
    seedColor: AppColors.primary,
    brightness: Brightness.light,
  ),
  textTheme: const TextTheme(
    displayLarge: AppTypography.h1,
    displayMedium: AppTypography.h2,
    bodyLarge: AppTypography.body,
    bodySmall: AppTypography.caption,
  ),
);
```

---

## Navigation (go_router)

**Define Routes**:
```dart
@TypedGoRoute<HomeRoute>(path: '/')
class HomeRoute extends GoRouteData {
  const HomeRoute();

  @override
  Widget build(BuildContext context, GoRouterState state) => const HomePage();
}

@TypedGoRoute<ActionDetailsRoute>(path: '/actions/:id')
class ActionDetailsRoute extends GoRouteData {
  const ActionDetailsRoute({required this.id});

  final String id;

  @override
  Widget build(BuildContext context, GoRouterState state) {
    return ActionDetailsPage(actionId: id);
  }
}

// Router configuration
@riverpod
GoRouter router(RouterRef ref) {
  return GoRouter(
    routes: $appRoutes,
    redirect: (context, state) {
      final isAuthenticated = ref.read(authProvider).isAuthenticated;
      if (!isAuthenticated && state.location != '/login') {
        return '/login';
      }
      return null;
    },
  );
}
```

**Navigate**:
```dart
// Type-safe navigation
context.push(const ActionDetailsRoute(id: '123'));

// Or with extension
const ActionDetailsRoute(id: '123').push(context);
```

---

## Testing

### Widget Tests
```dart
void main() {
  testWidgets('ActionCard displays action details', (tester) async {
    final action = Action(
      id: '1',
      title: 'Plant a Tree',
      description: 'Planted oak tree in park',
      pointsEarned: 50,
      createdAt: DateTime.now(),
    );

    await tester.pumpWidget(
      MaterialApp(
        home: Scaffold(
          body: ActionCard(action: action),
        ),
      ),
    );

    expect(find.text('Plant a Tree'), findsOneWidget);
    expect(find.text('Planted oak tree in park'), findsOneWidget);
    expect(find.text('50'), findsOneWidget);
  });
}
```

### Golden Tests (UI Regression)
```dart
void main() {
  testWidgets('ActionCard golden test', (tester) async {
    await tester.pumpWidget(
      MaterialApp(
        home: Scaffold(
          body: ActionCard(action: testAction),
        ),
      ),
    );

    await expectLater(
      find.byType(ActionCard),
      matchesGoldenFile('goldens/action_card.png'),
    );
  });
}
```

### Integration Tests (patrol)
```dart
void main() {
  patrolTest('User can create action', ($) async {
    await $.pumpWidgetAndSettle(const MyApp());

    // Navigate to create action
    await $(FloatingActionButton).tap();

    // Fill form
    await $(TextField).which<TextField>((w) => w.decoration?.labelText == 'Title')
        .enterText('My Action');

    // Submit
    await $(ActionButton).which<ActionButton>((w) => w.child is Text && (w.child as Text).data == 'Create')
        .tap();

    // Verify success
    await $.waitUntilVisible(find.text('Action created successfully'));
  });
}
```

---

## Accessibility

### Semantic Labels
```dart
Semantics(
  label: 'Create new action button',
  button: true,
  child: FloatingActionButton(
    onPressed: createAction,
    child: const Icon(Icons.add),
  ),
)
```

### Color Contrast
Ensure WCAG AA compliance (4.5:1 for normal text, 3:1 for large text).

### Text Scaling
Support system font size settings:
```dart
Text(
  'Hello',
  style: Theme.of(context).textTheme.bodyLarge,  // Respects system scaling
)
```

---

## Performance Optimization

### 1. Const Constructors
```dart
const ActionButton(onPressed: null, child: Text('Click'));  // Reuses instance
```

### 2. ListView.builder (Virtual Scrolling)
```dart
ListView.builder(
  itemCount: items.length,
  itemBuilder: (context, index) => ItemWidget(items[index]),
);
```

### 3. Image Caching
```dart
Image.network(
  url,
  cacheWidth: 400,  // Resize for display size
  cacheHeight: 300,
);
```

### 4. Code Splitting
```dart
import 'heavy_feature.dart' deferred as heavy;

// Later
await heavy.loadLibrary();
heavy.showFeature();
```

---

## Related Files
- **Global Standards**: `.github/copilot-instructions.md`
- **Mobile CDD Agent**: `.github/agents/mobile-cdd.agent.md`
- **OpenAPI Spec**: `openapi/urbanbloom-api-v1.yaml`
- **Design Tokens**: `shared-resources/design-tokens/tokens.json`
- **Frontend Prompts**: `docs/prompts/frontend-prompts.md`
