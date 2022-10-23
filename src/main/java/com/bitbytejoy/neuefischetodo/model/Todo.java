package com.bitbytejoy.neuefischetodo.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Todo {
    @Id private String id;
    private String title;
    private String status; // Defined by TodoStatus
    private String userId; // Reference to the user owning the resource
}
