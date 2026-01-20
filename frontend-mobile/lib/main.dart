import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'core/routing/app_router.dart';
import 'design_system/design_tokens.dart';

void main() {
  runApp(
    const ProviderScope(
      child: SchoolLibraryApp(),
    ),
  );
}

class SchoolLibraryApp extends StatelessWidget {
  const SchoolLibraryApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp.router(
      title: 'School Library Mobile',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(
          seedColor: AppColors.primary,
          primary: AppColors.primary,
          secondary: AppColors.secondary,
          surface: AppColors.surface,
          error: AppColors.error,
        ),
        useMaterial3: true,
        fontFamily: AppTypography.fontFamily,
      ),
      routerConfig: AppRouter.router,
    );
  }
}

