import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

class AdminSidebar extends StatelessWidget {
  final String currentPath;

  const AdminSidebar({
    super.key,
    required this.currentPath,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      width: 260,
      decoration: BoxDecoration(
        color: Colors.white,
        border: Border(
          right: BorderSide(color: Colors.grey[200]!),
        ),
      ),
      child: Column(
        children: [
          Container(
            padding: const EdgeInsets.all(24),
            child: Row(
              children: [
                const Icon(Icons.eco, color: Colors.green, size: 32),
                const SizedBox(width: 12),
                const Text(
                  'UrbanBloom',
                  style: TextStyle(
                    fontSize: 20,
                    fontWeight: FontWeight.bold,
                    color: Colors.indigo,
                  ),
                ),
              ],
            ),
          ),
          const Divider(height: 1),
          Expanded(
            child: ListView(
              padding: const EdgeInsets.symmetric(vertical: 16),
              children: [
                _buildNavItem(context, 'Overview', Icons.dashboard, '/'),
                _buildNavItem(context, 'Users', Icons.people, '/users'),
                _buildNavItem(context, 'Districts', Icons.location_city, '/districts'),
                _buildNavItem(context, 'Challenges', Icons.emoji_events, '/challenges'),
                _buildNavItem(context, 'Plant Catalog', Icons.local_florist, '/plants'),
                const Divider(),
                _buildNavItem(context, 'Reports', Icons.bar_chart, '/reports'),
                _buildNavItem(context, 'Settings', Icons.settings, '/settings'),
              ],
            ),
          ),
          const Divider(height: 1),
          _buildNavItem(context, 'Logout', Icons.logout, '/login', color: Colors.red),
          const SizedBox(height: 16),
        ],
      ),
    );
  }

  Widget _buildNavItem(
    BuildContext context,
    String title,
    IconData icon,
    String path, {
    Color? color,
  }) {
    final isSelected = currentPath == path;
    final activeColor = color ?? Colors.indigo;

    return ListTile(
      leading: Icon(
        icon,
        color: isSelected ? activeColor : Colors.grey[600],
      ),
      title: Text(
        title,
        style: TextStyle(
          color: isSelected ? activeColor : Colors.grey[800],
          fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
        ),
      ),
      selected: isSelected,
      selectedTileColor: activeColor.withValues(alpha: 0.05),
      onTap: () => context.go(path),
    );
  }
}
