// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'action_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

ActionModel _$ActionModelFromJson(Map<String, dynamic> json) => ActionModel(
      actionId: json['actionId'] as String,
      userId: json['userId'] as String,
      plantName: json['plantName'] as String,
      latitude: (json['latitude'] as num).toDouble(),
      longitude: (json['longitude'] as num).toDouble(),
      status: json['status'] as String,
      createdAt: DateTime.parse(json['createdAt'] as String),
    );

Map<String, dynamic> _$ActionModelToJson(ActionModel instance) =>
    <String, dynamic>{
      'actionId': instance.actionId,
      'userId': instance.userId,
      'plantName': instance.plantName,
      'latitude': instance.latitude,
      'longitude': instance.longitude,
      'status': instance.status,
      'createdAt': instance.createdAt.toIso8601String(),
    };
