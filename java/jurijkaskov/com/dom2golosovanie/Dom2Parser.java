package jurijkaskov.com.dom2golosovanie;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import java.io.File;
import java.util.ArrayList;

public class Dom2Parser extends Service {
    private static final String TAG = "Dom2Parser";
    private static final String PHOTOFOLDER = "photofolder";
    private static int mHeroesTotal = 0;
    private static int mCursorHero = 0;
    private static final String NEW_DATA_HERO = "NEW_DATA_HERO";

    private String mApplicationDir;
    public Dom2Parser() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        AsyncTask<Void, Void, Void> mTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                String mHTML = "";
                mHTML = mFunctions.getPage((String)getResources().getText(R.string.heroes_feed)); // страница http://dom2.ru/heroes
                if(mHTML.length() == 0 || mHTML == null)
                    return null;
                ArrayList<String> mHeroes = mFunctions.getGeroesList(mHTML); // список id участников - 120772848
                mHeroesTotal = mHeroes.size();

                if(mHeroesTotal < 1){
                    return null;
                }

                // перебор всех страниц героев
                for(String mS: mHeroes){
                    Hero mCurHero = mFunctions.downloadHero(mS);
                    mCurHero.mPhoto = mFunctions.downloadPhoto(mCurHero.mPhoto, mApplicationDir + "/" + PHOTOFOLDER); // скачивание фотографии

                    mCursorHero++;

                    newDataHero(mCurHero); // отправка намерения при получении данных о герое
                }
                mCursorHero = 0; //сброс счетчика

                return null;
            }

            private void newDataHero(Hero her){
                Intent mIntent = new Intent(NEW_DATA_HERO);
                mIntent.putExtra("Fio", her.getmFio());
                mIntent.putExtra("DaysOfTheShow", her.getmDaysOfTheShow());
                mIntent.putExtra("StartDate", her.getmStartDate());
                mIntent.putExtra("AgeHero", her.getmAgeHero());
                mIntent.putExtra("City", her.getmCity());
                mIntent.putExtra("SignOfTheZodiac", her.getmSignOfTheZodiac());
                mIntent.putExtra("Description", her.getmDescription());
                mIntent.putExtra("Photo", her.getmPhoto());
                mIntent.putExtra("HeroId", her.getmHeroId());
                sendBroadcast(mIntent);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                stopSelf();
            }
        };

        if(startId == 1) { // не реагировать на повторные вызовы
            //mTask.execute();
        }mTask.execute();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationDir = this.getApplicationInfo().dataDir;

        // папка для загрузки фотографий
        File mDir = new File(mApplicationDir + "/" + PHOTOFOLDER);
        if(mDir.exists() && mDir.isDirectory()) {
            Log.i(TAG, "Папка для фотографий уже существует");
        }else{
            mDir.mkdir();
            Log.i(TAG, "Папка для фотографий создана");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}