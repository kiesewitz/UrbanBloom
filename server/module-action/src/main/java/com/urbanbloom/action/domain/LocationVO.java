package com.urbanbloom.action.domain;

import com.urbanbloom.shared.ddd.ValueObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.UUID;

/**
 * Location information embedded in an action.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class LocationVO extends ValueObject {
    private double latitude;
    private double longitude;
    private String address;
    private UUID districtId;
}
