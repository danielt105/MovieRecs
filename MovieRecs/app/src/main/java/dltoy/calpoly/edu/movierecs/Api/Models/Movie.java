package dltoy.calpoly.edu.movierecs.Api.Models;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Movie {
    private @SerializedName("id") int id;
    private @SerializedName("title") String title;
    private @SerializedName("poster_path") String imagePath;
    private @SerializedName("overview") String description;
    private @SerializedName("vote_average") float rating;
    private @SerializedName("release_date") String date;
    private @SerializedName("runtime") int runtime;
    private @SerializedName("genres") List<Genre> genres;

    private int dbid;
    private boolean isWatched;
    private Date dateAdded;
    private Date lastDate; //This is only for undo when changing lists

    public int getDbid() {
        return dbid;
    }

    public void setDbid(int dbid) {
        this.dbid = dbid;
    }

    public boolean isWatched() {
        return isWatched;
    }

    public void setWatched(boolean watched) {
        isWatched = watched;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getDate() {
        SimpleDateFormat original = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat better = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
        Date done;
        try {
            done = original.parse(date);
        } catch (ParseException stupid) {
            // dumb
            done = new Date();
            stupid.printStackTrace();
        }

        return better.format(done);
    }

    public String getRawDate() {
        return date;
    }

    public void setRawDate(String newDate) {
        this.date = newDate;
    }

    public int getRuntime() {
        return runtime;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date newDate) {
        dateAdded = newDate;
    }

    public Date getLastDate() {
        return lastDate;
    }

    public void setLastDate(Date lastDate) {
        this.lastDate = lastDate;
    }

    public boolean equals(Movie other) {
        if (other == null)
            return false;
        return (this.id == other.id &&
                this.title.equals(other.getTitle()) &&
                this.imagePath.equals(other.getImagePath()) &&
                this.description.equals(other.getDescription()) &&
                this.rating == other.getRating() &&
                this.getDate().equals(other.getDate()) &&
                this.runtime == other.getRuntime());
    }
}
