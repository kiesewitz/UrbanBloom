import 'package:freezed_annotation/freezed_annotation.dart';

part 'health_dto.freezed.dart';
part 'health_dto.g.dart';

/// HealthDTO - Data Transfer Object for Health Check
@freezed
class HealthDTO with _$HealthDTO {
  const factory HealthDTO({
    required String status,
    String? message,
    DateTime? timestamp,
  }) = _HealthDTO;

  factory HealthDTO.fromJson(Map<String, dynamic> json) =>
      _$HealthDTOFromJson(json);
}
