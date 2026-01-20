// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'health_dto.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

T _$identity<T>(T value) => value;

final _privateConstructorUsedError = UnsupportedError(
  'It seems like you constructed your class using `MyClass._()`. This constructor is only meant to be used by freezed and you are not supposed to need it nor use it.\nPlease check the documentation here for more information: https://github.com/rrousselGit/freezed#adding-getters-and-methods-to-our-models',
);

HealthDTO _$HealthDTOFromJson(Map<String, dynamic> json) {
  return _HealthDTO.fromJson(json);
}

/// @nodoc
mixin _$HealthDTO {
  String get status => throw _privateConstructorUsedError;
  String? get message => throw _privateConstructorUsedError;
  DateTime? get timestamp => throw _privateConstructorUsedError;

  /// Serializes this HealthDTO to a JSON map.
  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;

  /// Create a copy of HealthDTO
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  $HealthDTOCopyWith<HealthDTO> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $HealthDTOCopyWith<$Res> {
  factory $HealthDTOCopyWith(HealthDTO value, $Res Function(HealthDTO) then) =
      _$HealthDTOCopyWithImpl<$Res, HealthDTO>;
  @useResult
  $Res call({String status, String? message, DateTime? timestamp});
}

/// @nodoc
class _$HealthDTOCopyWithImpl<$Res, $Val extends HealthDTO>
    implements $HealthDTOCopyWith<$Res> {
  _$HealthDTOCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  /// Create a copy of HealthDTO
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? status = null,
    Object? message = freezed,
    Object? timestamp = freezed,
  }) {
    return _then(
      _value.copyWith(
            status: null == status
                ? _value.status
                : status // ignore: cast_nullable_to_non_nullable
                      as String,
            message: freezed == message
                ? _value.message
                : message // ignore: cast_nullable_to_non_nullable
                      as String?,
            timestamp: freezed == timestamp
                ? _value.timestamp
                : timestamp // ignore: cast_nullable_to_non_nullable
                      as DateTime?,
          )
          as $Val,
    );
  }
}

/// @nodoc
abstract class _$$HealthDTOImplCopyWith<$Res>
    implements $HealthDTOCopyWith<$Res> {
  factory _$$HealthDTOImplCopyWith(
    _$HealthDTOImpl value,
    $Res Function(_$HealthDTOImpl) then,
  ) = __$$HealthDTOImplCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call({String status, String? message, DateTime? timestamp});
}

/// @nodoc
class __$$HealthDTOImplCopyWithImpl<$Res>
    extends _$HealthDTOCopyWithImpl<$Res, _$HealthDTOImpl>
    implements _$$HealthDTOImplCopyWith<$Res> {
  __$$HealthDTOImplCopyWithImpl(
    _$HealthDTOImpl _value,
    $Res Function(_$HealthDTOImpl) _then,
  ) : super(_value, _then);

  /// Create a copy of HealthDTO
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? status = null,
    Object? message = freezed,
    Object? timestamp = freezed,
  }) {
    return _then(
      _$HealthDTOImpl(
        status: null == status
            ? _value.status
            : status // ignore: cast_nullable_to_non_nullable
                  as String,
        message: freezed == message
            ? _value.message
            : message // ignore: cast_nullable_to_non_nullable
                  as String?,
        timestamp: freezed == timestamp
            ? _value.timestamp
            : timestamp // ignore: cast_nullable_to_non_nullable
                  as DateTime?,
      ),
    );
  }
}

/// @nodoc
@JsonSerializable()
class _$HealthDTOImpl implements _HealthDTO {
  const _$HealthDTOImpl({required this.status, this.message, this.timestamp});

  factory _$HealthDTOImpl.fromJson(Map<String, dynamic> json) =>
      _$$HealthDTOImplFromJson(json);

  @override
  final String status;
  @override
  final String? message;
  @override
  final DateTime? timestamp;

  @override
  String toString() {
    return 'HealthDTO(status: $status, message: $message, timestamp: $timestamp)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$HealthDTOImpl &&
            (identical(other.status, status) || other.status == status) &&
            (identical(other.message, message) || other.message == message) &&
            (identical(other.timestamp, timestamp) ||
                other.timestamp == timestamp));
  }

  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  int get hashCode => Object.hash(runtimeType, status, message, timestamp);

  /// Create a copy of HealthDTO
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  @pragma('vm:prefer-inline')
  _$$HealthDTOImplCopyWith<_$HealthDTOImpl> get copyWith =>
      __$$HealthDTOImplCopyWithImpl<_$HealthDTOImpl>(this, _$identity);

  @override
  Map<String, dynamic> toJson() {
    return _$$HealthDTOImplToJson(this);
  }
}

abstract class _HealthDTO implements HealthDTO {
  const factory _HealthDTO({
    required final String status,
    final String? message,
    final DateTime? timestamp,
  }) = _$HealthDTOImpl;

  factory _HealthDTO.fromJson(Map<String, dynamic> json) =
      _$HealthDTOImpl.fromJson;

  @override
  String get status;
  @override
  String? get message;
  @override
  DateTime? get timestamp;

  /// Create a copy of HealthDTO
  /// with the given fields replaced by the non-null parameter values.
  @override
  @JsonKey(includeFromJson: false, includeToJson: false)
  _$$HealthDTOImplCopyWith<_$HealthDTOImpl> get copyWith =>
      throw _privateConstructorUsedError;
}
