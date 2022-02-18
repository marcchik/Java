package com.example.oop;


public class User {

    private String name;
    private int id;

    User(String name, int id){
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public  String toString(){
        return "Имя: " + name + " Возраст: " + id;
    }

}
