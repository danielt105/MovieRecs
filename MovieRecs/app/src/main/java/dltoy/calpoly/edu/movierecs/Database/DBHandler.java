package dltoy.calpoly.edu.movierecs.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dltoy.calpoly.edu.movierecs.Api.DateConverter;
import dltoy.calpoly.edu.movierecs.Api.Models.Genre;
import dltoy.calpoly.edu.movierecs.Api.Models.Movie;

public class DBHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    //movie db
    private static final String MOVIE_DATABASE = "MOVIE_DATABASE";
    private static final String TABLE_MOVIES = "movies";
    private static final String KEY_ID = "id";
    private static final String KEY_MOVIE_ID = "movieID";
    private static final String KEY_TITLE = "title";
    private static final String KEY_IMG = "image";
    private static final String KEY_WATCHED = "isWatched";
    private static final String KEY_RATING = "rating";
    private static final String KEY_DESC = "description";
    private static final String KEY_RELEASE = "releaseDate";
    private static final String KEY_DATE_ADDED = "dateAdded";

    //genre db
    private static final String TABLE_GENERES = "genres";
    private static final String KEY_NAME = "name";

    public DBHandler(Context context) {
        super(context, MOVIE_DATABASE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MOVIE_TABLE = "CREATE TABLE " + TABLE_MOVIES + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_MOVIE_ID + " INTEGER," +
                KEY_TITLE + " TEXT," +
                KEY_IMG + " TEXT," +
                KEY_WATCHED + " INTEGER," +
                KEY_RATING + " TEXT," +
                KEY_DESC + " TEXT," +
                KEY_RELEASE + " TEXT," +
                KEY_DATE_ADDED + " TEXT " + ")";
        db.execSQL(CREATE_MOVIE_TABLE);

        String CREATE_GENRE_TABLE = "CREATE TABLE " + TABLE_GENERES + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_NAME + " TEXT" + ")";
        db.execSQL(CREATE_GENRE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GENERES);
        onCreate(db);
    }

    public void addMovie(Movie movie) {
        addMovie(movie, DateConverter.getCurrentDate());
    }

    public void addMovie(Movie movie, Date added) {
        if (containsMovie(movie))
            return;

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_MOVIE_ID, movie.getId());
        values.put(KEY_TITLE, movie.getTitle());
        values.put(KEY_IMG, movie.getImagePath());
        values.put(KEY_WATCHED, 0);
        values.put(KEY_RATING, movie.getRating() + "");
        values.put(KEY_DESC, movie.getDescription());
        values.put(KEY_RELEASE, movie.getRawDate());
        values.put(KEY_DATE_ADDED, DateConverter.dateToString(added));

        db.insert(TABLE_MOVIES, null, values);
        db.close();
    }

    public Movie getMovie(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_MOVIES, new String[] { KEY_ID,
                        KEY_MOVIE_ID, KEY_TITLE, KEY_IMG }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Movie movie = new Movie();
        movie.setDbid(Integer.parseInt(cursor.getString(0)));
        movie.setId(Integer.parseInt(cursor.getString(1)));
        movie.setTitle(cursor.getString(2));
        movie.setImagePath(cursor.getString(3));
        movie.setWatched(Integer.parseInt(cursor.getString(4)) > 0);
        movie.setRating(Float.parseFloat(cursor.getString(5)));
        movie.setDescription(cursor.getString(6));
        movie.setRawDate(cursor.getString(7));
        movie.setDateAdded(DateConverter.stringToDate(cursor.getString(8)));
        return movie;
    }

    public boolean containsMovie(Movie m) {
        String selectQuery = "SELECT * FROM " + TABLE_MOVIES +
                " WHERE " + KEY_MOVIE_ID + " == " + m.getId();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        boolean isInList = cursor.moveToFirst();
        cursor.close();
        return isInList;
    }

    public List<Movie> getWatchlist(int notWatched) {
        List<Movie> watchlist = new ArrayList<Movie>();
        String selectQuery = "SELECT * FROM " + TABLE_MOVIES +
                " WHERE " + KEY_WATCHED + " == " + notWatched;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Movie movie = new Movie();
                movie.setDbid(Integer.parseInt(cursor.getString(0)));
                movie.setId(Integer.parseInt(cursor.getString(1)));
                movie.setTitle(cursor.getString(2));
                movie.setImagePath(cursor.getString(3));
                movie.setWatched(Integer.parseInt(cursor.getString(4)) > 0);
                movie.setRating(Float.parseFloat(cursor.getString(5)));
                movie.setDescription(cursor.getString(6));
                movie.setRawDate(cursor.getString(7));
                movie.setDateAdded(DateConverter.stringToDate(cursor.getString(8)));

                watchlist.add(movie);
            } while (cursor.moveToNext());
        }

        return watchlist;
    }

    public int updateMovie(Movie movie) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_MOVIE_ID, movie.getId());
        values.put(KEY_TITLE, movie.getTitle());
        values.put(KEY_IMG, movie.getImagePath());
        values.put(KEY_WATCHED, movie.isWatched());
        values.put(KEY_RATING, movie.getRating());
        values.put(KEY_DESC, movie.getDescription());
        values.put(KEY_RELEASE, movie.getRawDate());
        values.put(KEY_DATE_ADDED, DateConverter.dateToString(movie.getDateAdded()));

        return db.update(TABLE_MOVIES, values, KEY_ID + " = ?",
                new String[] { String.valueOf(movie.getDbid()) });
    }

    public void deleteMovie(Movie movie) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MOVIES, KEY_ID + " = ?",
                new String[] { String.valueOf(movie.getDbid()) });
        db.close();
    }

    public void deleteMovieById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MOVIES, KEY_MOVIE_ID + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
    }

    public void addGenre(Genre genre) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, genre.getId());
        values.put(KEY_NAME, genre.getName());

        db.insert(TABLE_GENERES, null, values);
        db.close();
    }

    public Genre getGenre(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_GENERES, new String[] { KEY_ID,
                        KEY_NAME }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Genre genre = new Genre();
        genre.setId(Integer.parseInt(cursor.getString(0)));
        genre.setName(cursor.getString(1));
        return genre;
    }

    public List<Genre> getGenres() {
        List<Genre> genres = new ArrayList<Genre>();
        String selectQuery = "SELECT  * FROM " + TABLE_GENERES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Genre genre = new Genre();
                genre.setId(Integer.parseInt(cursor.getString(0)));
                genre.setName(cursor.getString(1));

                genres.add(genre);
            } while (cursor.moveToNext());
        }

        return genres;
    }

    public int updateGenre(Genre genre) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, genre.getId());
        values.put(KEY_NAME, genre.getName());

        return db.update(TABLE_GENERES, values, KEY_ID + " = ?",
                new String[] { String.valueOf(genre.getId()) });
    }

    public void deleteMovie(Genre genre) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GENERES, KEY_ID + " = ?",
                new String[] { String.valueOf(genre.getId()) });
        db.close();
    }
}
