package dltoy.calpoly.edu.movierecs.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import dltoy.calpoly.edu.movierecs.Api.Models.Movie;

public class DBHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MOVIE_DATABASE";
    private static final String TABLE_MOVIES = "movies";

    //Columns
    private static final String KEY_ID = "id";
    private static final String KEY_MOVIE_ID = "movieID";
    private static final String KEY_TITLE = "title";
    private static final String KEY_IMG = "image";
    private static final String KEY_WATCHED = "isWatched";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_MOVIES + "(" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_MOVIE_ID + " INTEGER," +
                KEY_TITLE + " TEXT," +
                KEY_IMG + " TEXT," +
                KEY_WATCHED + " INTEGER" + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIES);
        onCreate(db);
    }

    public void addMovie(Movie movie) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_MOVIE_ID, movie.getId());
        values.put(KEY_TITLE, movie.getTitle());
        values.put(KEY_IMG, movie.getImagePath());
        values.put(KEY_WATCHED, 0);

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
        movie.setWatched(Integer.parseInt(cursor.getString(1)) > 0);
        return movie;
    }

    public List<Movie> getWatchlist() {
        List<Movie> watchlist = new ArrayList<Movie>();
        String selectQuery = "SELECT  * FROM " + TABLE_MOVIES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Movie movie = new Movie();
                movie.setDbid(Integer.parseInt(cursor.getString(0)));
                movie.setId(Integer.parseInt(cursor.getString(1)));
                movie.setTitle(cursor.getString(2));
                movie.setImagePath(cursor.getString(3));
                movie.setWatched(Integer.parseInt(cursor.getString(1)) > 0);

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

        return db.update(TABLE_MOVIES, values, KEY_ID + " = ?",
                new String[] { String.valueOf(movie.getDbid()) });
    }

    public void deleteMovie(Movie movie) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MOVIES, KEY_ID + " = ?",
                new String[] { String.valueOf(movie.getDbid()) });
        db.close();
    }
}
