package com.bitbytejoy.neuefischetodo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotBlank;

@Data
public class Todo {
    @Id private String id;
    private String title;
    @NotBlank private String status; // Defined by TodoStatus
    private String userId; // Reference to the user owning the resource
}
