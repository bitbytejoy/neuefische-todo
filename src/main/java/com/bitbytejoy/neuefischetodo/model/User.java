package com.bitbytejoy.neuefischetodo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotBlank;
import java.util.Map;

@Data
public class User {
    @Id private String id;
    @NotBlank private String name;
    @NotBlank private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank
    private String password;
}
