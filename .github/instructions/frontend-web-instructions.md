# Frontend Web Admin Development Instructions - UrbanBloom

**Technology Stack**: Flutter Web 3.16+, Dart 3.2+, Riverpod  
**Architectural Approach**: Component-Driven Design (CDD) with Atomic Design  
**Target Platform**: Web (Desktop-first)  
**Target Audience**: Web admin developers (1-2 people)

---

## Web-Specific Considerations

### Desktop-First Design
- **Minimum width**: 1024px (desktop screens)
- **Layout**: Multi-column layouts, sidebars, data tables
- **Navigation**: Persistent sidebar navigation (not bottom nav like mobile)
- **Forms**: Complex multi-section forms with inline validation

### Performance
- **Code Splitting**: Use deferred imports for large admin features
- **Lazy Loading**: Load data on demand (tables, charts)
- **Virtual Scrolling**: For large data tables

### Browser Compatibility
- **Target**: Chrome, Firefox, Safari, Edge (latest 2 versions)
- **Testing**: Test in all target browsers before PR

---

## Directory Structure (Web-Specific)

```
lib/
├── ui/
│   ├── atoms/              # Buttons, inputs, icons (shared with mobile)
│   ├── molecules/          # Search bars, filters, table cells
│   ├── organisms/          # Data tables, charts, admin forms
│   │   ├── action_admin_table.dart    # Action management table
│   │   ├── user_admin_table.dart      # User management table
│   │   ├── analytics_chart.dart       # Analytics charts
│   │   └── admin_form.dart            # CRUD form generator
│   ├── templates/
│   │   ├── admin_layout.dart          # Admin panel layout with sidebar
│   │   └── dashboard_layout.dart      # Dashboard grid layout
│   └── pages/
│       ├── dashboard_page.dart        # Main dashboard with KPIs
│       ├── actions_management_page.dart
│       ├── users_management_page.dart
│       ├── challenges_management_page.dart
│       └── analytics_page.dart
├── domain/                 # Same as mobile (shared models)
├── data/                   # API client, repositories
├── providers/              # Riverpod providers
└── core/
    ├── theme/              # Web-specific theme (desktop spacing)
    ├── utils/
    │   └── csv_export.dart # CSV export utility
    └── constants/
```

---

## Admin Layout Template

**Persistent Sidebar Navigation**:
```dart
class AdminLayout extends StatelessWidget {
  const AdminLayout({
    required this.body,
    required this.selectedRoute,
    super.key,
  });

  final Widget body;
  final String selectedRoute;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Row(
        children: [
          // Sidebar navigation
          NavigationRail(
            extended: true,
            destinations: const [
              NavigationRailDestination(
                icon: Icon(Icons.dashboard),
                label: Text('Dashboard'),
              ),
              NavigationRailDestination(
                icon: Icon(Icons.eco),
                label: Text('Actions'),
              ),
              NavigationRailDestination(
                icon: Icon(Icons.people),
                label: Text('Users'),
              ),
              NavigationRailDestination(
                icon: Icon(Icons.emoji_events),
                label: Text('Challenges'),
              ),
              NavigationRailDestination(
                icon: Icon(Icons.analytics),
                label: Text('Analytics'),
              ),
            ],
            selectedIndex: _getSelectedIndex(selectedRoute),
            onDestinationSelected: (index) => _navigateTo(context, index),
          ),
          const VerticalDivider(thickness: 1, width: 1),
          // Main content
          Expanded(
            child: body,
          ),
        ],
      ),
    );
  }

  int _getSelectedIndex(String route) {
    switch (route) {
      case '/dashboard': return 0;
      case '/actions': return 1;
      case '/users': return 2;
      case '/challenges': return 3;
      case '/analytics': return 4;
      default: return 0;
    }
  }

  void _navigateTo(BuildContext context, int index) {
    switch (index) {
      case 0: context.go('/dashboard');
      case 1: context.go('/actions');
      case 2: context.go('/users');
      case 3: context.go('/challenges');
      case 4: context.go('/analytics');
    }
  }
}
```

---

## Data Tables with Filtering & Export

**ActionAdminTable Organism**:
```dart
class ActionAdminTable extends ConsumerStatefulWidget {
  const ActionAdminTable({super.key});

  @override
  ConsumerState<ActionAdminTable> createState() => _ActionAdminTableState();
}

class _ActionAdminTableState extends ConsumerState<ActionAdminTable> {
  String _searchQuery = '';
  ActionStatus? _filterStatus;
  int _currentPage = 0;
  static const int _rowsPerPage = 25;

  @override
  Widget build(BuildContext context) {
    final asyncActions = ref.watch(
      adminActionsProvider(
        page: _currentPage,
        query: _searchQuery,
        status: _filterStatus,
      ),
    );

    return Column(
      children: [
        _buildToolbar(),
        const SizedBox(height: 16),
        Expanded(
          child: asyncActions.when(
            data: (paginated) => _buildTable(paginated),
            loading: () => const Center(child: CircularProgressIndicator()),
            error: (error, stack) => ErrorWidget.withRetry(
              message: 'Failed to load actions',
              onRetry: () => ref.refresh(adminActionsProvider(
                page: _currentPage,
                query: _searchQuery,
                status: _filterStatus,
              )),
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildToolbar() {
    return Row(
      children: [
        // Search
        Expanded(
          flex: 2,
          child: TextField(
            decoration: const InputDecoration(
              hintText: 'Search actions...',
              prefixIcon: Icon(Icons.search),
            ),
            onChanged: (value) {
              setState(() => _searchQuery = value);
            },
          ),
        ),
        const SizedBox(width: 16),
        // Status filter
        Expanded(
          child: DropdownButton<ActionStatus?>(
            value: _filterStatus,
            hint: const Text('Filter by status'),
            isExpanded: true,
            items: [
              const DropdownMenuItem(value: null, child: Text('All')),
              ...ActionStatus.values.map((status) => DropdownMenuItem(
                value: status,
                child: Text(status.displayName),
              )),
            ],
            onChanged: (status) {
              setState(() => _filterStatus = status);
            },
          ),
        ),
        const SizedBox(width: 16),
        // Export button
        ElevatedButton.icon(
          onPressed: _exportToCSV,
          icon: const Icon(Icons.download),
          label: const Text('Export CSV'),
        ),
      ],
    );
  }

  Widget _buildTable(PaginatedActions paginated) {
    return SingleChildScrollView(
      child: PaginatedDataTable(
        header: Text('Actions (${paginated.totalCount})'),
        rowsPerPage: _rowsPerPage,
        availableRowsPerPage: const [25, 50, 100],
        onPageChanged: (page) {
          setState(() => _currentPage = page ~/ _rowsPerPage);
        },
        columns: const [
          DataColumn(label: Text('ID')),
          DataColumn(label: Text('Title')),
          DataColumn(label: Text('User')),
          DataColumn(label: Text('Status')),
          DataColumn(label: Text('Points')),
          DataColumn(label: Text('Created')),
          DataColumn(label: Text('Actions')),
        ],
        source: _ActionDataSource(
          actions: paginated.items,
          onView: (action) => context.go('/actions/${action.id}'),
          onVerify: (action) => _verifyAction(action),
          onDelete: (action) => _deleteAction(action),
        ),
      ),
    );
  }

  Future<void> _exportToCSV() async {
    final allActions = await ref.read(
      allActionsForExportProvider.future,
    );

    final csv = CsvExporter.exportActions(allActions);
    final bytes = utf8.encode(csv);
    final blob = html.Blob([bytes]);
    final url = html.Url.createObjectUrlFromBlob(blob);

    final anchor = html.AnchorElement(href: url)
      ..setAttribute('download', 'actions_${DateTime.now().toIso8601String()}.csv')
      ..click();

    html.Url.revokeObjectUrl(url);
  }

  Future<void> _verifyAction(Action action) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Verify Action'),
        content: Text('Verify "${action.title}"?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('Cancel'),
          ),
          ElevatedButton(
            onPressed: () => Navigator.pop(context, true),
            child: const Text('Verify'),
          ),
        ],
      ),
    );

    if (confirmed == true) {
      await ref.read(adminActionsProvider.notifier).verifyAction(action.id);
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Action verified')),
      );
    }
  }

  Future<void> _deleteAction(Action action) async {
    // Similar to verify, with delete confirmation
  }
}

class _ActionDataSource extends DataTableSource {
  _ActionDataSource({
    required this.actions,
    required this.onView,
    required this.onVerify,
    required this.onDelete,
  });

  final List<Action> actions;
  final Function(Action) onView;
  final Function(Action) onVerify;
  final Function(Action) onDelete;

  @override
  DataRow getRow(int index) {
    final action = actions[index];
    return DataRow(cells: [
      DataCell(Text(action.id.substring(0, 8))),
      DataCell(Text(action.title)),
      DataCell(Text(action.creatorName)),
      DataCell(_buildStatusChip(action.status)),
      DataCell(Text(action.pointsEarned.toString())),
      DataCell(Text(DateFormat.yMd().format(action.createdAt))),
      DataCell(_buildActions(action)),
    ]);
  }

  Widget _buildStatusChip(ActionStatus status) {
    return Chip(
      label: Text(status.displayName),
      backgroundColor: status.color,
    );
  }

  Widget _buildActions(Action action) {
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        IconButton(
          icon: const Icon(Icons.visibility),
          onPressed: () => onView(action),
          tooltip: 'View',
        ),
        if (action.status == ActionStatus.pending)
          IconButton(
            icon: const Icon(Icons.check),
            onPressed: () => onVerify(action),
            tooltip: 'Verify',
          ),
        IconButton(
          icon: const Icon(Icons.delete),
          onPressed: () => onDelete(action),
          tooltip: 'Delete',
        ),
      ],
    );
  }

  @override
  bool get isRowCountApproximate => false;

  @override
  int get rowCount => actions.length;

  @override
  int get selectedRowCount => 0;
}
```

---

## Dashboard with KPI Cards

**DashboardPage**:
```dart
@RoutePage()
class DashboardPage extends ConsumerWidget {
  const DashboardPage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final asyncStats = ref.watch(dashboardStatsProvider);

    return AdminLayout(
      selectedRoute: '/dashboard',
      body: Padding(
        padding: const EdgeInsets.all(24),
        child: asyncStats.when(
          data: (stats) => _buildDashboard(stats),
          loading: () => const Center(child: CircularProgressIndicator()),
          error: (error, stack) => ErrorWidget.withRetry(
            message: 'Failed to load dashboard',
            onRetry: () => ref.refresh(dashboardStatsProvider),
          ),
        ),
      ),
    );
  }

  Widget _buildDashboard(DashboardStats stats) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          'Dashboard',
          style: Theme.of(context).textTheme.headlineLarge,
        ),
        const SizedBox(height: 24),
        // KPI Cards
        Row(
          children: [
            Expanded(
              child: KpiCard(
                title: 'Total Actions',
                value: stats.totalActions.toString(),
                trend: stats.actionsTrend,
                icon: Icons.eco,
                color: Colors.green,
              ),
            ),
            const SizedBox(width: 16),
            Expanded(
              child: KpiCard(
                title: 'Active Users',
                value: stats.activeUsers.toString(),
                trend: stats.usersTrend,
                icon: Icons.people,
                color: Colors.blue,
              ),
            ),
            const SizedBox(width: 16),
            Expanded(
              child: KpiCard(
                title: 'Pending Verifications',
                value: stats.pendingVerifications.toString(),
                icon: Icons.pending_actions,
                color: Colors.orange,
              ),
            ),
            const SizedBox(width: 16),
            Expanded(
              child: KpiCard(
                title: 'Active Challenges',
                value: stats.activeChallenges.toString(),
                icon: Icons.emoji_events,
                color: Colors.purple,
              ),
            ),
          ],
        ),
        const SizedBox(height: 32),
        // Charts
        Expanded(
          child: Row(
            children: [
              Expanded(
                flex: 2,
                child: Card(
                  child: Padding(
                    padding: const EdgeInsets.all(16),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          'Actions Over Time',
                          style: Theme.of(context).textTheme.titleLarge,
                        ),
                        const SizedBox(height: 16),
                        Expanded(
                          child: ActionsTimeSeriesChart(data: stats.actionsTimeSeries),
                        ),
                      ],
                    ),
                  ),
                ),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: Card(
                  child: Padding(
                    padding: const EdgeInsets.all(16),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          'Top Districts',
                          style: Theme.of(context).textTheme.titleLarge,
                        ),
                        const SizedBox(height: 16),
                        Expanded(
                          child: TopDistrictsChart(data: stats.topDistricts),
                        ),
                      ],
                    ),
                  ),
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }
}

class KpiCard extends StatelessWidget {
  const KpiCard({
    required this.title,
    required this.value,
    required this.icon,
    required this.color,
    this.trend,
    super.key,
  });

  final String title;
  final String value;
  final IconData icon;
  final Color color;
  final double? trend;  // Percentage change

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Icon(icon, color: color, size: 32),
                const Spacer(),
                if (trend != null) _buildTrendIndicator(),
              ],
            ),
            const SizedBox(height: 16),
            Text(
              value,
              style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 4),
            Text(
              title,
              style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                color: Colors.grey[600],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildTrendIndicator() {
    final isPositive = trend! >= 0;
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
      decoration: BoxDecoration(
        color: isPositive ? Colors.green[100] : Colors.red[100],
        borderRadius: BorderRadius.circular(4),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(
            isPositive ? Icons.arrow_upward : Icons.arrow_downward,
            size: 16,
            color: isPositive ? Colors.green[700] : Colors.red[700],
          ),
          const SizedBox(width: 4),
          Text(
            '${trend!.abs().toStringAsFixed(1)}%',
            style: TextStyle(
              color: isPositive ? Colors.green[700] : Colors.red[700],
              fontWeight: FontWeight.bold,
            ),
          ),
        ],
      ),
    );
  }
}
```

---

## Charts (fl_chart)

**ActionsTimeSeriesChart**:
```dart
import 'package:fl_chart/fl_chart.dart';

class ActionsTimeSeriesChart extends StatelessWidget {
  const ActionsTimeSeriesChart({
    required this.data,
    super.key,
  });

  final List<TimeSeriesData> data;

  @override
  Widget build(BuildContext context) {
    return LineChart(
      LineChartData(
        gridData: FlGridData(show: true),
        titlesData: FlTitlesData(
          leftTitles: AxisTitles(
            sideTitles: SideTitles(showTitles: true, reservedSize: 40),
          ),
          bottomTitles: AxisTitles(
            sideTitles: SideTitles(
              showTitles: true,
              getTitlesWidget: (value, meta) {
                final date = data[value.toInt()].date;
                return Text(DateFormat('MMM d').format(date));
              },
            ),
          ),
        ),
        lineBarsData: [
          LineChartBarData(
            spots: data
                .asMap()
                .entries
                .map((e) => FlSpot(e.key.toDouble(), e.value.count.toDouble()))
                .toList(),
            isCurved: true,
            color: AppColors.primary,
            barWidth: 3,
            dotData: FlDotData(show: false),
            belowBarData: BarAreaData(
              show: true,
              color: AppColors.primary.withOpacity(0.2),
            ),
          ),
        ],
      ),
    );
  }
}
```

---

## CSV Export Utility

```dart
class CsvExporter {
  static String exportActions(List<Action> actions) {
    final buffer = StringBuffer();

    // Header
    buffer.writeln('ID,Title,Description,User,Status,Points,Created');

    // Rows
    for (final action in actions) {
      buffer.writeln([
        action.id,
        _escape(action.title),
        _escape(action.description),
        _escape(action.creatorName),
        action.status.displayName,
        action.pointsEarned,
        action.createdAt.toIso8601String(),
      ].join(','));
    }

    return buffer.toString();
  }

  static String _escape(String value) {
    if (value.contains(',') || value.contains('"') || value.contains('\n')) {
      return '"${value.replaceAll('"', '""')}"';
    }
    return value;
  }
}
```

---

## Web-Specific Routing

**Browser History Integration**:
```dart
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
    // Enable browser history
    routerNeglect: false,
  );
}
```

**URL Parameters**:
```dart
@TypedGoRoute<ActionsManagementRoute>(path: '/actions')
class ActionsManagementRoute extends GoRouteData {
  const ActionsManagementRoute({
    this.page = 0,
    this.search,
    this.status,
  });

  final int page;
  final String? search;
  final ActionStatus? status;

  @override
  Widget build(BuildContext context, GoRouterState state) {
    return ActionsManagementPage(
      initialPage: page,
      initialSearch: search,
      initialStatus: status,
    );
  }
}

// Navigate with query params
const ActionsManagementRoute(
  page: 2,
  search: 'tree',
  status: ActionStatus.verified,
).go(context);
// URL: /actions?page=2&search=tree&status=verified
```

---

## Web-Specific Theme

**Desktop Spacing**:
```dart
class WebAppSpacing {
  static const double xs = 4.0;
  static const double sm = 8.0;
  static const double md = 16.0;
  static const double lg = 24.0;
  static const double xl = 32.0;
  static const double xxl = 48.0;
  static const double xxxl = 64.0;  // Extra spacing for web
}

ThemeData get webAdminTheme => ThemeData(
  useMaterial3: true,
  colorScheme: ColorScheme.fromSeed(
    seedColor: AppColors.primary,
    brightness: Brightness.light,
  ),
  cardTheme: CardTheme(
    elevation: 2,
    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
  ),
  dataTableTheme: DataTableThemeData(
    headingRowColor: MaterialStateProperty.all(Colors.grey[100]),
  ),
);
```

---

## Authentication (JWT in Cookies)

**Secure Cookie Storage**:
```dart
import 'dart:html' as html;

class WebAuthStorage {
  static const _tokenKey = 'auth_token';

  static void saveToken(String token) {
    html.document.cookie = '$_tokenKey=$token; path=/; max-age=604800; secure; samesite=strict';
  }

  static String? getToken() {
    final cookies = html.document.cookie?.split('; ') ?? [];
    for (final cookie in cookies) {
      final parts = cookie.split('=');
      if (parts[0] == _tokenKey) {
        return parts.length > 1 ? parts[1] : null;
      }
    }
    return null;
  }

  static void clearToken() {
    html.document.cookie = '$_tokenKey=; path=/; max-age=0';
  }
}
```

---

## Performance: Code Splitting

**Deferred Loading**:
```dart
import 'pages/analytics_page.dart' deferred as analytics;

// Later, when user navigates
await analytics.loadLibrary();
return analytics.AnalyticsPage();
```

---

## Testing Web-Specific Features

**Test with Chrome Driver**:
```bash
flutter drive --target=test_driver/app.dart --driver=test_driver/app_test.dart -d chrome
```

**Widget Test with Web Mocks**:
```dart
testWidgets('CSV export downloads file', (tester) async {
  // Mock html.AnchorElement
  await tester.pumpWidget(const MyApp());

  await tester.tap(find.text('Export CSV'));
  await tester.pumpAndSettle();

  // Verify download was triggered
  // (requires mocking html library)
});
```

---

## Related Files
- **Global Standards**: `.github/copilot-instructions.md`
- **Web Admin CDD Agent**: `.github/agents/web-admin-cdd.agent.md`
- **OpenAPI Spec**: `openapi/urbanbloom-api-v1.yaml`
- **Mobile Instructions** (for shared concepts): `.github/instructions/frontend-mobile-instructions.md`
- **Frontend Prompts**: `docs/prompts/frontend-prompts.md`
