import 'package:dio/dio.dart';
import '../models/health_dto.dart';

/// Health Repository - Handles Health Check API calls
class HealthRepository {
  final Dio _dio;

  HealthRepository(this._dio);

  Future<HealthDTO> checkHealth() async {
    try {
      final response = await _dio.get('/health');
      
      if (response.statusCode == 200) {
        return HealthDTO(
          status: 'UP',
          message: 'Backend is healthy',
          timestamp: DateTime.now(),
        );
      } else {
        return HealthDTO(
          status: 'DOWN',
          message: 'Unexpected status code: ${response.statusCode}',
          timestamp: DateTime.now(),
        );
      }
    } on DioException catch (e) {
      return HealthDTO(
        status: 'DOWN',
        message: 'Connection error: ${e.message}',
        timestamp: DateTime.now(),
      );
    } catch (e) {
      return HealthDTO(
        status: 'DOWN',
        message: 'Unknown error: $e',
        timestamp: DateTime.now(),
      );
    }
  }
}
