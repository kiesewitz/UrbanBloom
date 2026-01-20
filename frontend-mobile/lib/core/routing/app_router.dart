import 'package:go_router/go_router.dart';
import '../../features/health/presentation/pages/health_check_page.dart';
import '../../features/user/presentation/pages/forgot_password_screen.dart';
import '../../features/user/presentation/pages/login_screen.dart';
import '../../features/user/presentation/pages/password_sent_screen.dart';
import '../../features/user/presentation/pages/profile_screen.dart';
import '../../features/user/presentation/pages/registration_screen.dart';

/// App Router Configuration with go_router
class AppRouter {
  static final GoRouter router = GoRouter(
    initialLocation: '/login',
    routes: [
      GoRoute(
        path: '/login',
        name: 'login',
        builder: (context, state) => const LoginScreen(),
      ),
      GoRoute(
        path: '/register',
        name: 'register',
        builder: (context, state) => const RegistrationScreen(),
      ),
      GoRoute(
        path: '/profile',
        name: 'profile',
        builder: (context, state) => const ProfileScreen(),
      ),
      GoRoute(
        path: '/forgot-password',
        name: 'forgot-password',
        builder: (context, state) => const ForgotPasswordScreen(),
      ),
      GoRoute(
        path: '/password-sent',
        name: 'password-sent',
        builder: (context, state) {
          final email = state.uri.queryParameters['email'];
          return PasswordSentScreen(email: email);
        },
      ),
      GoRoute(
        path: '/health',
        name: 'health',
        builder: (context, state) => const HealthCheckPage(),
      ),
    ],
  );
}
