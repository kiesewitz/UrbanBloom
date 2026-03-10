import 'package:dio/dio.dart';
import '../domain/action_model.dart';

class ActionRepository {
  final Dio _dio;

  ActionRepository(this._dio);

  Future<List<ActionModel>> getActions() async {
    try {
      final response = await _dio.get('/actions');
      if (response.statusCode == 200) {
        final List<dynamic> data = response.data['items'];
        return data.map((json) => ActionModel.fromJson(json)).toList();
      }
      throw Exception('Failed to load actions');
    } catch (e) {
      rethrow;
    }
  }

  Future<String> createAction({
    required String plantId,
    required String plantName,
    required double latitude,
    required double longitude,
    String? description,
  }) async {
    try {
      final response = await _dio.post('/actions', data: {
        'plantId': plantId,
        'plantName': plantName,
        'latitude': latitude,
        'longitude': longitude,
        'description': description,
      });
      
      if (response.statusCode == 201) {
        return response.data['actionId'];
      }
      throw Exception('Failed to create action');
    } catch (e) {
      rethrow;
    }
  }
}
