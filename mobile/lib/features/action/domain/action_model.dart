import 'package:json_annotation/json_annotation.dart';

part 'action_model.g.dart';

@JsonSerializable()
class ActionModel {
  final String actionId;
  final String userId;
  final String plantName;
  final double latitude;
  final double longitude;
  final String status;
  final DateTime createdAt;

  ActionModel({
    required this.actionId,
    required this.userId,
    required this.plantName,
    required this.latitude,
    required this.longitude,
    required this.status,
    required this.createdAt,
  });

  factory ActionModel.fromJson(Map<String, dynamic> json) => _$ActionModelFromJson(json);
  Map<String, dynamic> toJson() => _$ActionModelToJson(this);
}
