import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../../../../design_system/design_tokens.dart';
import '../../../../design_system/components/atoms/primary_button.dart';
import '../../../../design_system/components/atoms/status_badge.dart';
import '../../../../core/di/providers.dart';
import '../../data/models/health_dto.dart';

/// Health Check Page - Template Level
/// Smart Component (StatefulWidget) handling data and state
class HealthCheckPage extends ConsumerStatefulWidget {
  const HealthCheckPage({super.key});

  @override
  ConsumerState<HealthCheckPage> createState() => _HealthCheckPageState();
}

class _HealthCheckPageState extends ConsumerState<HealthCheckPage> {
  HealthDTO? _healthStatus;
  bool _isLoading = false;
  String? _errorMessage;

  Future<void> _checkHealth() async {
    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      final repository = ref.read(healthRepositoryProvider);
      final health = await repository.checkHealth();
      
      setState(() {
        _healthStatus = health;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _errorMessage = 'Failed to check health: $e';
        _isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.background,
      appBar: AppBar(
        title: const Text('School Library Mobile'),
        backgroundColor: AppColors.primary,
        foregroundColor: AppColors.textOnPrimary,
        elevation: AppElevation.sm,
      ),
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(AppSpacing.lg),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              // Header
              Text(
                'Hello World 👋',
                style: AppTypography.displayLarge,
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: AppSpacing.md),
              Text(
                'Digital School Library - Mobile App',
                style: AppTypography.bodyLarge.copyWith(
                  color: AppColors.textSecondary,
                ),
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: AppSpacing.xxl),

              // Health Status Card
              if (_healthStatus != null)
                _HealthStatusCard(health: _healthStatus!),

              if (_errorMessage != null)
                _ErrorCard(message: _errorMessage!),

              const SizedBox(height: AppSpacing.xl),

              // Check Health Button
              PrimaryButton(
                label: 'Check Backend Health',
                onPressed: _checkHealth,
                isLoading: _isLoading,
                icon: Icons.health_and_safety,
              ),

              const SizedBox(height: AppSpacing.lg),

              // Info Text
              Text(
                'Press the button to check if the backend service is running.',
                style: AppTypography.bodySmall,
                textAlign: TextAlign.center,
              ),
            ],
          ),
        ),
      ),
    );
  }
}

/// Health Status Card - Organism Level
class _HealthStatusCard extends StatelessWidget {
  final HealthDTO health;

  const _HealthStatusCard({required this.health});

  @override
  Widget build(BuildContext context) {
    final isHealthy = health.status == 'UP';

    return Container(
      padding: const EdgeInsets.all(AppSpacing.lg),
      decoration: BoxDecoration(
        color: AppColors.surface,
        borderRadius: BorderRadius.circular(AppRadius.md),
        border: Border.all(color: AppColors.border),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withValues(alpha: 0.05),
            blurRadius: AppElevation.md,
            offset: const Offset(0, 2),
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Icon(
                isHealthy ? Icons.check_circle : Icons.error,
                color: isHealthy ? AppColors.success : AppColors.error,
                size: 32.0,
              ),
              const SizedBox(width: AppSpacing.md),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      'Backend Status',
                      style: AppTypography.titleLarge,
                    ),
                    const SizedBox(height: AppSpacing.xs),
                    StatusBadge(
                      label: health.status,
                      type: isHealthy ? StatusType.success : StatusType.error,
                    ),
                  ],
                ),
              ),
            ],
          ),
          if (health.message != null) ...[
            const SizedBox(height: AppSpacing.md),
            const Divider(color: AppColors.divider),
            const SizedBox(height: AppSpacing.md),
            Text(
              health.message!,
              style: AppTypography.bodyMedium,
            ),
          ],
          if (health.timestamp != null) ...[
            const SizedBox(height: AppSpacing.sm),
            Text(
              'Checked: ${_formatTimestamp(health.timestamp!)}',
              style: AppTypography.bodySmall,
            ),
          ],
        ],
      ),
    );
  }

  String _formatTimestamp(DateTime timestamp) {
    return '${timestamp.hour.toString().padLeft(2, '0')}:'
        '${timestamp.minute.toString().padLeft(2, '0')}:'
        '${timestamp.second.toString().padLeft(2, '0')}';
  }
}

/// Error Card - Molecule Level
class _ErrorCard extends StatelessWidget {
  final String message;

  const _ErrorCard({required this.message});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(AppSpacing.md),
      decoration: BoxDecoration(
        color: AppColors.error.withValues(alpha: 0.1),
        borderRadius: BorderRadius.circular(AppRadius.sm),
        border: Border.all(color: AppColors.error),
      ),
      child: Row(
        children: [
          const Icon(
            Icons.error_outline,
            color: AppColors.error,
          ),
          const SizedBox(width: AppSpacing.md),
          Expanded(
            child: Text(
              message,
              style: AppTypography.bodySmall.copyWith(
                color: AppColors.error,
              ),
            ),
          ),
        ],
      ),
    );
  }
}
