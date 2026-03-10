import 'package:flutter/material.dart';
import '../../../../ui/organisms/admin_sidebar.dart';
import '../../../../ui/organisms/admin_header.dart';

class UserManagementScreen extends StatefulWidget {
  const UserManagementScreen({super.key});

  @override
  State<UserManagementScreen> createState() => _UserManagementScreenState();
}

class _UserManagementScreenState extends State<UserManagementScreen> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Row(
        children: [
          const AdminSidebar(currentPath: '/users'),
          Expanded(
            child: Column(
              children: [
                const AdminHeader(title: 'User Management'),
                Expanded(
                  child: SingleChildScrollView(
                    padding: const EdgeInsets.all(24.0),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Text(
                              'Manage Citizens & Administrators',
                              style: Theme.of(context).textTheme.headlineSmall,
                            ),
                            ElevatedButton.icon(
                              onPressed: () {},
                              icon: const Icon(Icons.add),
                              label: const Text('Add User'),
                              style: ElevatedButton.styleFrom(
                                backgroundColor: Colors.indigo,
                                foregroundColor: Colors.white,
                              ),
                            ),
                          ],
                        ),
                        const SizedBox(height: 24),
                        Card(
                          child: Column(
                            children: [
                              Padding(
                                padding: const EdgeInsets.all(16.0),
                                child: TextField(
                                  decoration: InputDecoration(
                                    hintText: 'Search users by name or email...',
                                    prefixIcon: const Icon(Icons.search),
                                    border: OutlineInputBorder(
                                      borderRadius: BorderRadius.circular(8),
                                    ),
                                  ),
                                ),
                              ),
                              SizedBox(
                                width: double.infinity,
                                child: DataTable(
                                  columns: const [
                                    DataColumn(label: Text('User')),
                                    DataColumn(label: Text('Email')),
                                    DataColumn(label: Text('Role')),
                                    DataColumn(label: Text('Status')),
                                    DataColumn(label: Text('Actions')),
                                  ],
                                  rows: [
                                    _buildUserRow('Nikita Rath', 'nikita@urbanbloom.local', 'ADMIN', true),
                                    _buildUserRow('Max Mustermann', 'max@gmail.com', 'CITIZEN', true),
                                    _buildUserRow('Erika Mustermann', 'erika@schule.de', 'DISTRICT_MANAGER', true),
                                    _buildUserRow('John Doe', 'john@old.com', 'CITIZEN', false),
                                  ],
                                ),
                              ),
                            ],
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

  DataRow _buildUserRow(String name, String email, String role, bool active) {
    return DataRow(cells: [
      DataCell(Text(name, style: const TextStyle(fontWeight: FontWeight.bold))),
      DataCell(Text(email)),
      DataCell(Chip(
        label: Text(role, style: const TextStyle(fontSize: 12)),
        backgroundColor: _getRoleColor(role).withValues(alpha: 0.1),
        labelStyle: TextStyle(color: _getRoleColor(role)),
      )),
      DataCell(Icon(
        active ? Icons.check_circle : Icons.cancel,
        color: active ? Colors.green : Colors.red,
      )),
      DataCell(Row(
        children: [
          IconButton(icon: const Icon(Icons.edit), onPressed: () {}),
          IconButton(icon: const Icon(Icons.delete, color: Colors.red), onPressed: () {}),
        ],
      )),
    ]);
  }

  Color _getRoleColor(String role) {
    switch (role) {
      case 'ADMIN': return Colors.purple;
      case 'DISTRICT_MANAGER': return Colors.blue;
      default: return Colors.green;
    }
  }
}
