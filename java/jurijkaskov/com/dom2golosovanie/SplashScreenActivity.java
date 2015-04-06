package jurijkaskov.com.dom2golosovanie;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.ConsoleHandler;

import static java.lang.Thread.sleep;


public class SplashScreenActivity extends Activity {

    TextView load_indicator;
    private String FIRST_TIME = "FIRST_TIME"; // первый запуск приложения
    private String PROJECT_SETTINGS = "PROJECT_SETTINGS";

    protected String text; // текст статуса

    private String UPDATE_DATE = "UPDATE_DATE"; // последнее обновление

    private Long oneDay = 30L; // 1=1s 86400=24h
    private String photodir;
    private int totalHeroes;

    ThreadControl tControl = new ThreadControl(); // управление потоком
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);





        photodir = (String)getResources().getText(R.string.photo_folder);
        if(isFirstTime()) { // если запускается впервые, то создается папка для изображений
     //
            SharedPreferences projectSettings = getSharedPreferences(PROJECT_SETTINGS, Activity.MODE_PRIVATE);
            File dir = SplashScreenActivity.this.getDir(photodir, SplashScreenActivity.this.MODE_PRIVATE); // папка с фотографиями
            projectSettings.edit().putString("internalphotodir",  photodir).apply();
        }

        load_indicator = (TextView) findViewById(R.id.loadindicator);

        // парсинг участников
        AsyncTask<Void, Integer, Void> task = new AsyncTask<Void, Integer, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                text = getString(R.string.splash_screen_loading_status);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);

                // строка "обновление участников"
                StringBuilder s = new StringBuilder();
                s.append(text);
                s.append(" ");
                s.append(values[0]);

                load_indicator.setText(s.toString() + "/" + totalHeroes);
            }

            @Override
            protected Void doInBackground(Void... params) {
                Date date = new Date();
                Long curTime = date.getTime()/1000; //sec

                SharedPreferences sharedPrefs = getSharedPreferences(UPDATE_DATE, Activity.MODE_PRIVATE);
                Long updateDate = sharedPrefs.getLong("updatedate", -1);
                Log.i("lll", updateDate.toString());

                if(updateDate == -1 || (updateDate-curTime)>oneDay || (curTime-updateDate)>oneDay) { // первое обновление или прошло с моследнего обновления больше 24ч

                    Dom2ruParser d2p = new Dom2ruParser(getString(R.string.heroes_feed), SplashScreenActivity.this, photodir);
                    ArrayList heroes = d2p.updateHeroesList(); // список id участников - 120772848

                    totalHeroes = heroes.size();
                    for (int i = 0; i < totalHeroes; i++) {
                        try { // ожидать, если активити скрыто
                            tControl.waitIfPaused();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if(tControl.isCancelled()){ // остановить, если отменено
                            break;
                        }

                        d2p.downloadHero((String) heroes.get(i)); // обновление и вставка в базу героя

                        publishProgress(i);
                    }

                    SharedPreferences.Editor editor = sharedPrefs.edit(); // после каждого обновления дата меняется
                    editor.putLong("updatedate", curTime);
                    editor.apply();

                }else{
                    try {
                        sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        };

        task.execute();
    }

    public boolean isFirstTime(){ // если установлено впервые
        SharedPreferences settings = getSharedPreferences(FIRST_TIME, Activity.MODE_PRIVATE);

        if(settings.getBoolean("first_time", true)){
            settings.edit().putBoolean("first_time", false).apply();
            return true;
        }

        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tControl.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        tControl.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        tControl.pause();
    }


}
