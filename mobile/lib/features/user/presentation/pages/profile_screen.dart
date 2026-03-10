import 'package:flutter/material.dart';
import '../../../../core/theme/design_tokens.dart';
import '../../../../ui/atoms/urban_button.dart';

class ProfileScreen extends StatelessWidget {
  const ProfileScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('My Profile'),
        backgroundColor: AppColors.primary,
        foregroundColor: Colors.white,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(AppSpacing.l),
        child: Column(
          children: [
            const CircleAvatar(
              radius: 50,
              backgroundColor: AppColors.secondary,
              child: Icon(Icons.person, size: 50, color: Colors.white),
            ),
            const SizedBox(height: AppSpacing.m),
            const Text(
              'Nikita Rath',
              style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
            ),
            const Text(
              'test@urbanbloom.local',
              style: TextStyle(color: AppColors.textSecondary),
            ),
            const SizedBox(height: AppSpacing.xl),
            _buildProfileItem(Icons.badge, 'Student ID', '211wita20'),
            _buildProfileItem(Icons.class_, 'Class', '5AHWIN'),
            _buildProfileItem(Icons.star, 'Level', 'Level 1 (Seedling)'),
            _buildProfileItem(Icons.emoji_events, 'Total Points', '120 pts'),
            const SizedBox(height: AppSpacing.xl),
            UrbanButton(
              label: 'Edit Profile',
              onPressed: () {},
            ),
            const SizedBox(height: AppSpacing.m),
            OutlinedButton(
              onPressed: () {
                // TODO: Logout logic
              },
              style: OutlinedButton.styleFrom(
                minimumSize: const Size(double.infinity, 50),
                side: const BorderSide(color: AppColors.error),
                foregroundColor: AppColors.error,
              ),
              child: const Text('Log Out'),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildProfileItem(IconData icon, String label, String value) {
    return Padding(
      padding: const EdgeInsets.only(bottom: AppSpacing.m),
      child: Row(
        children: [
          Icon(icon, color: AppColors.primary),
          const SizedBox(width: AppSpacing.m),
          Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(label, style: const TextStyle(fontSize: 12, color: AppColors.textSecondary)),
              Text(value, style: const TextStyle(fontSize: 16, fontWeight: FontWeight.w500)),
            ],
          ),
        ],
      ),
    );
  }
}
