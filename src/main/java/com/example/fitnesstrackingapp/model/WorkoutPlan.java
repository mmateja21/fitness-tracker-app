package com.example.fitnesstrackingapp.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;



public class WorkoutPlan implements Serializable {



    private static final long serialVersionUID = 1L;


    private int id;


    private int userId;


    private String name;


    private String description;


    private LocalDateTime createdAt;


    public WorkoutPlan() {
    }


    public WorkoutPlan(
            int userId,
            String name,
            String description
    ) {
        this.userId = userId;
        this.name = name;
        this.description = description;
    }


    public WorkoutPlan(
            int id,
            int userId,
            String name,
            String description,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
