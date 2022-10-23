package com.bitbytejoy.neuefischetodo.model;

public enum TodoStatus {
    TODO("TODO"),
    IN_PROGRESS("IN PROGRESS"),
    DONE("DONE");

    private final String title;

    TodoStatus(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
