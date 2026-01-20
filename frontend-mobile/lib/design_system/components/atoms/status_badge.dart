import 'package:flutter/material.dart';
import '../../design_tokens.dart';

/// Status Badge Atom - Presentation Component
/// Displays status with semantic colors
enum StatusType { success, error, warning, info }

class StatusBadge extends StatelessWidget {
  final String label;
  final StatusType type;

  const StatusBadge({
    required this.label,
    required this.type,
    super.key,
  });

  Color get _backgroundColor {
    switch (type) {
      case StatusType.success:
        return AppColors.success;
      case StatusType.error:
        return AppColors.error;
      case StatusType.warning:
        return AppColors.warning;
      case StatusType.info:
        return AppColors.info;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(
        horizontal: AppSpacing.md,
        vertical: AppSpacing.sm,
      ),
      decoration: BoxDecoration(
        color: _backgroundColor,
        borderRadius: BorderRadius.circular(AppRadius.round),
      ),
      child: Text(
        label,
        style: AppTypography.bodySmall.copyWith(
          color: AppColors.textOnPrimary,
          fontWeight: AppTypography.fontWeightMedium,
        ),
      ),
    );
  }
}
