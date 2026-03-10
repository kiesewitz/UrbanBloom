package com.urbanbloom.action.domain;

import com.urbanbloom.shared.ddd.ValueObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.UUID;

/**
 * Plant information embedded in an action.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class PlantVO extends ValueObject {
    private UUID plantId;
    private String name;
    private String scientificName;
}
