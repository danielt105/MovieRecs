package dltoy.calpoly.edu.movierecs.Api.Models;

import java.util.ArrayList;


/*
 * This class is a only used to save data to restore during advanced searches
 */
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
