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
    TextView mLoadIndicator;
    String text;
    private static int mHeroesTotal = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mLoadIndicator = (TextView) findViewById(R.id.loadindicator);
        text = getString(R.string.splash_screen_loading_status);

        /*
        if(savedInstanceState == null){ // старт сервиса при запуске
            startService(new Intent(this, Dom2Parser.class));
        }*/
        startService(new Intent(this, Dom2Parser.class));
    }

    BroadcastReceiver mHeroUpdateReceiver;
    @Override
    protected void onResume() {
        super.onResume();

        mHeroUpdateReceiver = new UpdaterBroadcastReceiver();
        registerReceiver(
                mHeroUpdateReceiver,
                new IntentFilter(HERO_UPDATED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mHeroUpdateReceiver);
    }

    public class UpdaterBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle mExtras = intent.getExtras();
            if (mExtras != null) {
                if(mExtras.containsKey("max")){
                    mHeroesTotal = (int)mExtras.get("max");//Toast.makeText(SplashScreenActivity.this, String.valueOf(mExtras.get("max")), Toast.LENGTH_LONG).show();
                }
                if(mExtras.containsKey("value")){
                    //System.out.println("Value is:"+extras.get("value"));
                    Log.i(TAG+"123", mExtras.get("value")+"");
                    String mValue = String.valueOf(mExtras.get("value"));
                    StringBuilder mSB = new StringBuilder();
                    mSB.append(text);
                    mSB.append(" ");
                    mSB.append(String.valueOf(Float.valueOf(mValue)/mHeroesTotal*100));
                    mSB.append("%");

                    mLoadIndicator.setText(mSB.toString());
                }
            }
        }

    }
}
