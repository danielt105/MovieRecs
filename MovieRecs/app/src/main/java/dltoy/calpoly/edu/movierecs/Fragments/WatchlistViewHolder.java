package dltoy.calpoly.edu.movierecs.Fragments;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import dltoy.calpoly.edu.movierecs.Api.Models.Movie;
import dltoy.calpoly.edu.movierecs.R;

/**
 * Created by main on 10/26/2016.
 */
public class WatchlistViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Context context;
    private TextView title;
    private CheckBox checkBox;
    private ImageView img;
    public Movie movie;

    public WatchlistViewHolder(View itemView) {
        super(itemView);
        context = itemView.getContext();
        title = (TextView) itemView.findViewById(R.id.watchlist_entry_title);
        img = (ImageView) itemView.findViewById(R.id.watchlist_image);
        checkBox = (CheckBox) itemView.findViewById(R.id.watchlist_checkbox);
        checkBox.setOnCheckedChangeListener(this);
        itemView.setOnClickListener(this);
    }

    public void bind(Movie m) {
        movie = m;
        title.setText(m.getTitle());
        Picasso.with(context).load(genURL(m.getImagePath())).into(img);
        checkBox.setChecked(m.isWatched());
    }

    private String genURL(String base) { //TODO:define this url somewhere
        return "https://image.tmdb.org/t/p/" + "w300" + base;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    @Override
    public void onClick(View v) {

    }
}
