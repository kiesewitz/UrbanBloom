import 'package:flutter/material.dart';
import '../../../../ui/organisms/admin_sidebar.dart';
import '../../../../ui/organisms/admin_header.dart';
import '../../../../ui/molecules/admin_stat_card.dart';

class AdminDashboardPage extends StatelessWidget {
  const AdminDashboardPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Row(
        children: [
          const AdminSidebar(currentPath: '/'),
          Expanded(
            child: Column(
              children: [
                const AdminHeader(title: 'Overview'),
                Expanded(
                  child: SingleChildScrollView(
                    padding: const EdgeInsets.all(24.0),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        const Row(
                          children: [
                            AdminStatCard(
                              title: 'Total Actions',
                              value: '1,234',
                              color: Colors.green,
                              icon: Icons.eco,
                            ),
                            SizedBox(width: 16),
                            AdminStatCard(
                              title: 'Active Users',
                              value: '567',
                              color: Colors.blue,
                              icon: Icons.people,
                            ),
                            SizedBox(width: 16),
                            AdminStatCard(
                              title: 'Validated Points',
                              value: '12,340',
                              color: Colors.orange,
                              icon: Icons.emoji_events,
                            ),
                          ],
                        ),
                        const SizedBox(height: 24),
                        Card(
                          child: Padding(
                            padding: const EdgeInsets.all(24.0),
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text(
                                  'Recent Activity',
                                  style: Theme.of(context).textTheme.titleLarge,
                                ),
                                const Divider(height: 32),
                                ListView.separated(
                                  shrinkWrap: true,
                                  physics: const NeverScrollableScrollPhysics(),
                                  itemCount: 5,
                                  separatorBuilder: (context, index) => const Divider(),
                                  itemBuilder: (context, index) {
                                    return ListTile(
                                      contentPadding: EdgeInsets.zero,
                                      leading: const CircleAvatar(
                                        child: Icon(Icons.person, size: 20),
                                      ),
                                      title: Text('Nikita Rath logged a new action: "Planted Sunflower"'),
                                      subtitle: Text('${index + 1} hours ago • District Mitte'),
                                      trailing: Container(
                                        padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                                        decoration: BoxDecoration(
                                          color: Colors.green.withValues(alpha: 0.1),
                                          borderRadius: BorderRadius.circular(4),
                                        ),
                                        child: const Text(
                                          '+10 pts',
                                          style: TextStyle(color: Colors.green, fontWeight: FontWeight.bold),
                                        ),
                                      ),
                                    );
                                  },
                                ),
                              ],
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
