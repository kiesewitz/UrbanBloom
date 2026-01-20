import 'package:flutter_test/flutter_test.dart';
import 'package:school_library_mobile/core/routing/app_router.dart';
import 'package:go_router/go_router.dart';

void main() {
  group('AppRouter', () {
    test('should have routes for login, register and health', () {
      final routes = AppRouter.router.configuration.routes;
      
      final routeNames = routes.map((r) => (r as GoRoute).name).toList();
      
      expect(routeNames, containsAll(['login', 'register', 'health']));
    });

    test('should have correct paths for routes', () {
      final routes = AppRouter.router.configuration.routes;
      
      final routePaths = routes.map((r) => (r as GoRoute).path).toList();
      
      expect(routePaths, containsAll(['/login', '/register', '/health']));
    });
  });
}
