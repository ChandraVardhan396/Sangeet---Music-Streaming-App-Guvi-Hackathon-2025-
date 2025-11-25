package com.Sangeet.models;

/* Abstract user to demonstrate inheritance & polymorphism */
public abstract class User {
    protected final String username;
    protected final int id;

    public User(int id, String username) {
        this.id = id;
        this.username = username;
    }
    public int getId() { return id; }
    public String getUsername() { return username; }
    public abstract String getRole();
}
