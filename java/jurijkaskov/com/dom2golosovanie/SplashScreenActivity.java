package jurijkaskov.com.dom2golosovanie;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.ConsoleHandler;

import static java.lang.Thread.sleep;


public class SplashScreenActivity extends Activity{

    private static final String TAG = "Dom2Parser";
    private static final String HERO_UPDATED = "HERO_UPDATED";
    private TextView mLoadIndicator;
    private String mText;
    private static int mHeroesTotal = 0;
    private String UPDATE_DATE = "UPDATE_DATE";
    private Long mRenewalPeriod = 60L; // 1=1s 86400=24h
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mLoadIndicator = (TextView) findViewById(R.id.loadindicator);
        mText = getString(R.string.splash_screen_loading_status);


        Date mDate = new Date();
        Long mCurTime = mDate.getTime()/1000; //sec
        SharedPreferences mSharedPrefs = getSharedPreferences(UPDATE_DATE, Activity.MODE_PRIVATE);
        Long mUpdateDate = mSharedPrefs.getLong("updatedate", -1);

        if(mUpdateDate == -1 || (mUpdateDate-mCurTime)>mRenewalPeriod || (mCurTime-mUpdateDate)>mRenewalPeriod) { // первое обновление или прошло с моследнего обновления больше 24ч

            startService(new Intent(this, Dom2Parser.class));

            SharedPreferences.Editor mEditor = mSharedPrefs.edit(); // после каждого обновления дата меняется
            mEditor.putLong("updatedate", mCurTime);
            mEditor.apply();
        }else{ // если обновление было недавно, то запускается сразу mainactivity
            startMainActivity();
        }
    }

    private BroadcastReceiver mHeroUpdateReceiver;
    @Override
    protected void onResume() {
        super.onResume();

        mHeroUpdateReceiver = new UpdaterBroadcastReceiver();
        registerReceiver( // прием данных из сервиса
                mHeroUpdateReceiver,
                new IntentFilter(HERO_UPDATED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mHeroUpdateReceiver); // отписаться от приема широковещательных намерений из сервиса
    }

    public class UpdaterBroadcastReceiver extends BroadcastReceiver{ // применик намерений из сервиса

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle mExtras = intent.getExtras();
            if (mExtras != null) {
                if(mExtras.containsKey("max")){ // максимальное количество героев
                    mHeroesTotal = (int)mExtras.get("max");
                }
                if(mExtras.containsKey("finish")){ // если true, то сервис закончил работу
                    if((boolean)mExtras.get("finish")) {
                        startMainActivity(); // запуск главной активити после обновления
                    }
                }
                if(mExtras.containsKey("value")){
                    // отображение процентов обновления информации об участниках
                    String mValue = String.valueOf(mExtras.get("value"));
                    StringBuilder mSB = new StringBuilder();
                    mSB.append(mText);
                    mSB.append(" ");
                    mSB.append(String.valueOf(Float.valueOf(mValue)/mHeroesTotal*100));
                    mSB.append("%");

                    Log.i("2222222", mValue);
                    mLoadIndicator.setText(mSB.toString());
                }
            }
        }

    }

    void startMainActivity(){ // запуск главной активити после обновления
        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
