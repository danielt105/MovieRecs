package dltoy.calpoly.edu.movierecs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        ((CustomFontTextView)findViewById(R.id.first_line)).setTextSize(Constants.SPLASH_TEAM_NAME * getResources().getDisplayMetrics().density);
        ((CustomFontTextView)findViewById(R.id.second_line)).setTextSize(Constants.SPLASH_PRESENTS * getResources().getDisplayMetrics().density);
        ((CustomFontTextView)findViewById(R.id.app_name_line)).setTextSize(Constants.SPLASH_TEXT_SIZE * getResources().getDisplayMetrics().density);

        Observable.timer(Constants.SPLASH_DELAY, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {
                        Log.e("Error", "in splash screen");
                    }

                    @Override
                    public void onNext(Long aLong) {
                        startMain();
                    }
                });
    }

    private void startMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
