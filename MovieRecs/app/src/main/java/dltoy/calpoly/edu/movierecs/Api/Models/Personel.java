package dltoy.calpoly.edu.movierecs.Api.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by connor on 11/21/16.
 */
public class Personel {
    @SerializedName("cast") private List<Person> cast;
    @SerializedName("crew") private List<Person> crew;

    public List<Person> getCast() {
        return cast;
    }

    public List<Person> getCrew() {
        return crew;
    }
}
