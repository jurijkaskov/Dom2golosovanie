package jurijkaskov.com.dom2golosovanie;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class Dom2Parser extends Service {
    private static final String TAG = "Dom2Parser";
    private static final String PHOTOFOLDER = "photofolder";
    private static int mHeroesTotal = 0;
    private static int mCursorHero = 0;
    private static final String HERO_UPDATED = "HERO_UPDATED";

    private String mApplicationDir;

    private Service mDom2ParserService = this;
    public Dom2Parser() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand, flags:" + flags + "startId:" + startId);

        AsyncTask<Void, Integer, Void> mTask = new AsyncTask<Void, Integer, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Log.i(TAG, "doInBackground");
                String mHTML = "";
                mHTML = mFunctions.getPage((String)getResources().getText(R.string.heroes_feed)); // страница http://dom2.ru/heroes
                if(mHTML.length() == 0 || mHTML == null)
                    return null;
                ArrayList<String> mHeroes = mFunctions.getGeroesList(mHTML); // список id участников - 120772848
                mHeroesTotal = mHeroes.size();

                Intent intent = new Intent();
                intent.setAction(HERO_UPDATED);
                intent.putExtra("max", mHeroesTotal);
                sendBroadcast(intent);

                if(mHeroesTotal < 1){
                    return null;
                }

                // перебор всех страниц героев
                for(String mS: mHeroes){
                    Hero mCurHero = mFunctions.downloadHero(mS);
                    mCurHero.mPhoto = mFunctions.downloadPhoto(mCurHero.mPhoto, mApplicationDir + "/" + PHOTOFOLDER); // скачивание фотографии
                    //Log.i(TAG, mCurHero.mPhoto + "===" + mApplicationDir);
                    mCursorHero++;
                    publishProgress(mCursorHero);
                }
                mCursorHero = 0; //сброс счетчика

                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                Intent intent = new Intent();
                intent.setAction(HERO_UPDATED);
                intent.putExtra("value", mCursorHero);
                sendBroadcast(intent);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.i(TAG, "onPreExecute");
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Log.i(TAG, "onPostExecute");
                stopSelf();
            }
        };

        if(startId == 1) { // не реагировать на повторные вызовы
            mTask.execute();
        }

        Log.i(TAG, "111111111mTask.execute()");
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
