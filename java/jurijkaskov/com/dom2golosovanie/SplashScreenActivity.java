package jurijkaskov.com.dom2golosovanie;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import static java.lang.Thread.sleep;


public class SplashScreenActivity extends Activity {

    TextView load_indicator;
    private String FIRST_TIME = "FIRST_TIME"; // первый запуск приложения
    private String PROJECT_SETTINGS = "PROJECT_SETTINGS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        SharedPreferences projectSettings = getSharedPreferences(PROJECT_SETTINGS, Activity.MODE_PRIVATE);

        if(isFirstTime()) { // если запускается впервые, то создается папка для изображений
            File dir = getApplicationContext().getDir("imgheroesphoto", getApplicationContext().MODE_PRIVATE);
            projectSettings.edit().putString("internalphotodir", "imgheroesphoto").apply();
        }

        load_indicator = (TextView) findViewById(R.id.loadindicator);
        new SplashScreenTask().execute();
    }

    public boolean isFirstTime(){
        SharedPreferences settings = getSharedPreferences(FIRST_TIME, Activity.MODE_PRIVATE);

        if(settings.getBoolean("first_time", true)){
            settings.edit().putBoolean("first_time", false).apply();
            return true;
        }

        return false;
    }
    // парсинг участников
    public class SplashScreenTask extends AsyncTask<Void, Integer, Void> {
        protected String text;

        private String UPDATE_DATE = "UPDATE_DATE"; // последнее обновление

        private Long oneDay = 30L; // 1=1s 86400=24h
        @Override
        protected Void doInBackground(Void... params) {
            Date date = new Date();
            Long curTime = date.getTime()/1000; //sec

            SharedPreferences sharedPrefs = getSharedPreferences(UPDATE_DATE, Activity.MODE_PRIVATE);
            Long updateDate = sharedPrefs.getLong("updatedate", -1);
            Log.i("lll", updateDate.toString());

            if(updateDate == -1 || (updateDate-curTime)>oneDay || (curTime-updateDate)>oneDay) { // первое обновление или прошло с моследнего обновления больше 24ч

                Dom2ruParser d2p = new Dom2ruParser(getString(R.string.heroes_feed), getApplicationContext());
                ArrayList heroes = d2p.updateHeroesList(); // список id участников - 120772848

                for (int i = 0; i < heroes.size(); i++) {
                    /*try {
                        d2p.getPage(new URL("http://dom2.ru/heroes/" + (String) heroes.get(i)));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }*/
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

            load_indicator.setText(s.toString());
        }
    }
}
