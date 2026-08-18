package com.example.fitnesstrackingapp.model;
import java.io.Serial;
import java.io.Serializable;

public class Exercise implements Serializable{
    private static final long serialVersionUID = 1L;
    private int id;
    private String name;
    private MuscleGroup muscleGroup;
    private String equipment;
    private String description;

    public Exercise(){}

//konstruktor po kome se pravi objekat pre dodele id
public Exercise(String name, MuscleGroup muscleGroup, String equipment, String description){
    this.name=name;
    this.muscleGroup=muscleGroup;
    this.equipment=equipment;
    this.description=description;
}
//konstruktor gde baza dodeljuje id
public Exercise(int id, String name, MuscleGroup muscleGroup, String equipment, String description){
        this.id=id;
        this.name=name;
        this.muscleGroup=muscleGroup;
        this.equipment=equipment;
        this.description=description;
}
//geteri i seteri
public int getId(){
        return id;
}

public void setId(int id){
        this.id=id;
}

public String getName(){
        return name;
}
public void setName(String name){
        this.name=name;
}
public MuscleGroup getMuscleGroup(){
        return muscleGroup;
}
public void setMuscleGroup(MuscleGroup muscleGroup){
        this.muscleGroup=muscleGroup;
}

public String getEquipment(){
        return equipment;
}
public void setEquipment(String equipment){
        this.equipment=equipment;
}

public String getDescription(){
        return description;
}
public void setDescription(String description){
        this.description=description;
}

}