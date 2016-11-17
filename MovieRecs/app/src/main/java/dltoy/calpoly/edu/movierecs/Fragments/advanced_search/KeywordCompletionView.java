package dltoy.calpoly.edu.movierecs.Fragments.advanced_search;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tokenautocomplete.TokenCompleteTextView;

import dltoy.calpoly.edu.movierecs.Api.Models.Keyword;
import dltoy.calpoly.edu.movierecs.Constants;
import dltoy.calpoly.edu.movierecs.MainActivity;
import dltoy.calpoly.edu.movierecs.R;

public class KeywordCompletionView extends TokenCompleteTextView<Keyword> {
    private String NO_ENTRY = getResources().getString(R.string.keyword_not_found);
    public KeywordCompletionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View getViewForObject(Keyword kw) {

        LayoutInflater l = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        TextView view = (TextView) l.inflate(R.layout.keyword_token, (ViewGroup) getParent(), false);
        view.setText(kw.getName());

        int curTheme = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt(Constants.THEME_KEY, 0);
        switch (curTheme) {
            case 2:
                view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.cruDarkGold));
                view.setTextColor(ContextCompat.getColor(getContext(), R.color.cruBlack));
                break;
            case 3:
                view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.fauna));
                view.setTextColor(ContextCompat.getColor(getContext(), R.color.lime));
                break;
            case 4:
                view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.glacierBlue));
                view.setTextColor(ContextCompat.getColor(getContext(), R.color.warmGray));
                break;
            case 5:
                view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.swizzySweetGold));
                view.setTextColor(ContextCompat.getColor(getContext(), R.color.blueTitle));
                break;
            default:
                view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                view.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        }
        return view;
    }

    //Pretty sure this is just transferring the text back to the screen
    @Override
    protected Keyword defaultObject(String completionText) {
        if (completionText == null || completionText.equals("")) {
            return new Keyword(-1, NO_ENTRY);
        } else {
            return new Keyword(-1, completionText);
        }
    }
}
