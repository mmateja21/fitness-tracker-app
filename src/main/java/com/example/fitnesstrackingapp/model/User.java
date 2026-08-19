package com.example.fitnesstrackingapp.model;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class User implements Serializable {

    private static final long serialVersionUID=1L;

    private int id;

    private String fullName;

    private String email;

    private LocalDateTime createdAt;

    //prazan user
    public User(){

    }
    //user koji nije jos sacuvan
    public User(String fullName, String email){
        this.fullName=fullName;
        this.email=email;
    }

    //user iz baze

    public User(int id, String fullName, String email, LocalDateTime createdAt){
        this.id=id;
        this.fullName=fullName;
        this.email=email;
        this.createdAt=createdAt;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id=id;
    }

    public String getFullName(){
        return fullName;
    }

    public void setFullName(String fullName){
        this.fullName=fullName;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(){
        this.email=email;
    }

    public LocalDateTime getCreatedAt(){
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt){
        this.createdAt=createdAt;
    }


}
