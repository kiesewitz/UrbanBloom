import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:url_strategy/url_strategy.dart';
import 'core/routing/admin_router.dart';

void main() {
  setPathUrlStrategy();
  runApp(
    const ProviderScope(
      child: UrbanBloomAdminApp(),
    ),
  );
}

class UrbanBloomAdminApp extends StatelessWidget {
  const UrbanBloomAdminApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp.router(
      title: 'UrbanBloom Admin',
      debugShowCheckedModeBanner: false,
      routerConfig: adminRouter,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(
          seedColor: Colors.indigo,
          primary: Colors.indigo,
        ),
        useMaterial3: true,
      ),
    );
  }
}
