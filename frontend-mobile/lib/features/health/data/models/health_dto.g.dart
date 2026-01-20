// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'health_dto.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

_$HealthDTOImpl _$$HealthDTOImplFromJson(Map<String, dynamic> json) =>
    _$HealthDTOImpl(
      status: json['status'] as String,
      message: json['message'] as String?,
      timestamp: json['timestamp'] == null
          ? null
          : DateTime.parse(json['timestamp'] as String),
    );

Map<String, dynamic> _$$HealthDTOImplToJson(_$HealthDTOImpl instance) =>
    <String, dynamic>{
      'status': instance.status,
      'message': instance.message,
      'timestamp': instance.timestamp?.toIso8601String(),
    };
