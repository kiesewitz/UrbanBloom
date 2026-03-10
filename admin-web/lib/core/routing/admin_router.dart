import 'package:go_router/go_router.dart';
import '../../features/analytics/presentation/pages/admin_dashboard_page.dart';
import '../../features/access_control/presentation/pages/admin_login_screen.dart';
import '../../features/districts/presentation/pages/user_management_screen.dart';
import '../../features/districts/presentation/pages/district_comparison_screen.dart';
import '../../features/challenges/presentation/pages/challenge_management_screen.dart';

final adminRouter = GoRouter(
  initialLocation: '/login',
  routes: [
    GoRoute(
      path: '/login',
      builder: (context, state) => const AdminLoginScreen(),
    ),
    GoRoute(
      path: '/',
      builder: (context, state) => const AdminDashboardPage(),
    ),
    GoRoute(
      path: '/users',
      builder: (context, state) => const UserManagementScreen(),
    ),
    GoRoute(
      path: '/districts',
      builder: (context, state) => const DistrictComparisonScreen(),
    ),
    GoRoute(
      path: '/challenges',
      builder: (context, state) => const ChallengeManagementScreen(),
    ),
  ],
);
