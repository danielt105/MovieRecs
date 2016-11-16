package dltoy.calpoly.edu.movierecs.Api.Models;

import java.util.ArrayList;

public class AdvSearch {

    public String[] query;
    public ArrayList<Keyword> keywords;
    public ArrayList<Person> cast;

    public AdvSearch(String[] query, ArrayList<Keyword> keywords, ArrayList<Person> cast) {
        this.query = query;
        this.keywords = keywords;
        this.cast = cast;
    }
}
