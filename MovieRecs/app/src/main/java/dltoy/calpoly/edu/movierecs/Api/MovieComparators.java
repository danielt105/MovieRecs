package dltoy.calpoly.edu.movierecs.Api;

import java.util.Comparator;

import dltoy.calpoly.edu.movierecs.Api.Models.Movie;

public class MovieComparators {
    public static Comparator<Movie> topRated = new Comparator<Movie>() {
        @Override
        public int compare(Movie lhs, Movie rhs) {
            return Float.compare(rhs.getRating(), lhs.getRating());
        }
    };

    public static Comparator<Movie> recent = new Comparator<Movie>() {
        @Override
        public int compare(Movie lhs, Movie rhs) {
            return rhs.getDate().compareTo(lhs.getDate());
        }
    };
}
