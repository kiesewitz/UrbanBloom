import 'package:flutter/material.dart';
import '../../../../ui/organisms/admin_sidebar.dart';
import '../../../../ui/organisms/admin_header.dart';

class ChallengeManagementScreen extends StatelessWidget {
  const ChallengeManagementScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Row(
        children: [
          const AdminSidebar(currentPath: '/challenges'),
          Expanded(
            child: Column(
              children: [
                const AdminHeader(title: 'Challenge Management'),
                Expanded(
                  child: SingleChildScrollView(
                    padding: const EdgeInsets.all(24),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            const Text(
                              'Active & Upcoming Challenges',
                              style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
                            ),
                            ElevatedButton.icon(
                              onPressed: () {},
                              icon: const Icon(Icons.add),
                              label: const Text('Create Challenge'),
                              style: ElevatedButton.styleFrom(
                                backgroundColor: Colors.indigo,
                                foregroundColor: Colors.white,
                              ),
                            ),
                          ],
                        ),
                        const SizedBox(height: 24),
                        GridView.count(
                          crossAxisCount: 3,
                          shrinkWrap: true,
                          crossAxisSpacing: 16,
                          mainAxisSpacing: 16,
                          childAspectRatio: 1.5,
                          children: [
                            _buildChallengeCard(
                              'Spring Planting 2026',
                              'Active',
                              'Plant 5 trees in your district',
                              '450 participants',
                              Colors.green,
                            ),
                            _buildChallengeCard(
                              'Watering Warrior',
                              'Upcoming',
                              'Water 10 public plants during heatwaves',
                              'Starts in 5 days',
                              Colors.blue,
                            ),
                            _buildChallengeCard(
                              'District Duel: North vs South',
                              'Active',
                              'Which district logs more actions this week?',
                              '1,200 participants',
                              Colors.orange,
                            ),
                          ],
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

  Widget _buildChallengeCard(String title, String status, String desc, String stats, Color color) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                  decoration: BoxDecoration(
                    color: color.withValues(alpha: 0.1),
                    borderRadius: BorderRadius.circular(4),
                  ),
                  child: Text(
                    status,
                    style: TextStyle(color: color, fontWeight: FontWeight.bold, fontSize: 12),
                  ),
                ),
                const Icon(Icons.more_vert),
              ],
            ),
            const SizedBox(height: 16),
            Text(
              title,
              style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 8),
            Text(
              desc,
              style: const TextStyle(color: Colors.grey, fontSize: 14),
            ),
            const Spacer(),
            const Divider(),
            Row(
              children: [
                const Icon(Icons.group, size: 16, color: Colors.grey),
                const SizedBox(width: 8),
                Text(stats, style: const TextStyle(fontSize: 12, color: Colors.grey)),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
