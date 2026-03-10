import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:dio/dio.dart';
import '../../domain/action_model.dart';
import '../../data/action_repository.dart';
import '../../../../core/theme/design_tokens.dart';

final actionRepositoryProvider = Provider((ref) => ActionRepository(Dio()));

final actionsProvider = FutureProvider<List<ActionModel>>((ref) async {
  final repository = ref.watch(actionRepositoryProvider);
  return repository.getActions();
});

class ActionListPage extends ConsumerWidget {
  const ActionListPage({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final actionsAsync = ref.watch(actionsProvider);

    return Scaffold(
      appBar: AppBar(
        title: const Text('My Green Actions'),
        backgroundColor: AppColors.primary,
        foregroundColor: Colors.white,
      ),
      body: actionsAsync.when(
        data: (actions) => ListView.builder(
          itemCount: actions.length,
          itemBuilder: (context, index) {
            final action = actions[index];
            return Card(
              margin: const EdgeInsets.all(AppSpacing.s),
              child: ListTile(
                title: Text(action.plantName),
                subtitle: Text('Status: ${action.status}'),
                trailing: const Icon(Icons.chevron_right),
              ),
            );
          },
        ),
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (err, stack) => Center(child: Text('Error: $err')),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          // Navigate to create action
        },
        backgroundColor: AppColors.primary,
        child: const Icon(Icons.add, color: Colors.white),
      ),
    );
  }
}
