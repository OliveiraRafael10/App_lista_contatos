package com.example.listadecontatos;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Contato implements Serializable {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("phone")
    private String phone;
    @SerializedName("favorite")
    private boolean favorite;


    public Contato(String name, String phone, boolean favorite) {
        this.name = name;
        this.phone = phone;
        this.favorite = favorite;
    }

    public int getId() { return id; }

    public String getName() { return name; }

    public String getPhone() { return phone; }

    public boolean isFavorite() { return favorite; }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

}
