package dltoy.calpoly.edu.movierecs.Api.Models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Keyword extends TokenData {
    public Keyword(int id, String name) {
        super(id, name);
    }
}
