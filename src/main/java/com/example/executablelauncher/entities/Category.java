package com.example.executablelauncher.entities;

public class Category {
    public String name;
    public boolean showOnFullscreen = false;

    public Category(String n, boolean s){
        name = n;
        showOnFullscreen = s;
    }
}