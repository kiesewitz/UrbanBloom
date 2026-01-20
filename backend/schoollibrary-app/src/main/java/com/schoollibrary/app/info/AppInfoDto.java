package com.schoollibrary.app.info;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.List;

/**
 * DTO for application information response.
 *
 * @author Backend Team
 * @version 1.0
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AppInfoDto {
    private String name;
    private String version;

    @JsonProperty("bounded_contexts")
    private List<String> boundedContexts;

    /**
     * Factory method to create app info response.
     *
     * @return AppInfoDto with application information
     */
    public static AppInfoDto create() {
        return new AppInfoDto(
            "Digital School Library",
            "0.0.1-SNAPSHOT",
            Arrays.asList("lending", "catalog", "notification", "reminding", "user")
        );
    }
}
