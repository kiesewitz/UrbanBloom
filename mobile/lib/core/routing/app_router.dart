import 'package:go_router/go_router.dart';
import '../../features/action/presentation/pages/action_list_page.dart';
import '../../features/user/presentation/pages/login_screen.dart';
import '../../features/user/presentation/pages/registration_screen.dart';
import '../../features/user/presentation/pages/profile_screen.dart';
import '../../features/user/presentation/pages/password_reset_screen.dart';

final appRouter = GoRouter(
  initialLocation: '/login',
  routes: [
    GoRoute(
      path: '/login',
      builder: (context, state) => const LoginScreen(),
    ),
    GoRoute(
      path: '/register',
      builder: (context, state) => const RegistrationScreen(),
    ),
    GoRoute(
      path: '/password-reset',
      builder: (context, state) => const PasswordResetScreen(),
    ),
    GoRoute(
      path: '/actions',
      builder: (context, state) => const ActionListPage(),
    ),
    GoRoute(
      path: '/profile',
      builder: (context, state) => const ProfileScreen(),
    ),
  ],
);
