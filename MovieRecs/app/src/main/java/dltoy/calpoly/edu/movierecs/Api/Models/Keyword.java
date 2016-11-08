package dltoy.calpoly.edu.movierecs.Api.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Keyword implements Serializable{
    private @SerializedName("id") int id;
    private @SerializedName("name") String name;

    public Keyword(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
