package jurijkaskov.com.dom2golosovanie;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends ActionBarActivity {

    private String UPDATE_DATE = "UPDATE_DATE";
    private Long mRenewalPeriod = 20L; // 1=1s 86400=24h
    private static final String NEW_DATA_HERO = "NEW_DATA_HERO";

    private SQLiteDatabase mHeroesDB;
    private static final String DATABASE_NAME = "allheroes.db";
    private static final String DATABASE_TABLE = "heroes";
    private static final String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + DATABASE_TABLE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, heroId INT UNIQUE ON CONFLICT REPLACE, fio STRING, daysOfTheShow INTEGER, startDate STRING, ageHero INTEGER, city STRING, signOfTheZodiac STRING, description TEXT, photo STRING, dateOfRenovation DATETIME)";
    private static final String SELECT_ALL_HEROES = "SELECT * FROM heroes";

    private HeroesListAdapter mHeroesListAdapter;

    private static ListView lvItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        lvItems = (ListView) findViewById(R.id.heroesList);

        Date mDate = new Date();
        Long mCurTime = mDate.getTime()/1000; //sec
        SharedPreferences mSharedPrefs = getSharedPreferences(UPDATE_DATE, Activity.MODE_PRIVATE);
        Long mUpdateDate = mSharedPrefs.getLong("updatedate", -1);

        if(mUpdateDate == -1 || (mUpdateDate-mCurTime)>mRenewalPeriod || (mCurTime-mUpdateDate)>mRenewalPeriod) { // первое обновление или прошло с последнего обновления больше 24ч

            if(savedInstanceState == null) {
                startService(new Intent(this, Dom2Parser.class));

                SharedPreferences.Editor mEditor = mSharedPrefs.edit(); // после каждого обновления дата меняется
                mEditor.putLong("updatedate", mCurTime);
                mEditor.apply();
            }
        }

        if(mHeroesDB == null) { // подключить базу, если еще не подключена
            createDatabase();
        }

        refreshHeroesList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class HeroesReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Hero mHero = new Hero();

            mHero.setmFio(intent.getStringExtra("Fio"));
            mHero.setmDaysOfTheShow(intent.getStringExtra("DaysOfTheShow"));
            mHero.setmStartDate(intent.getStringExtra("StartDate"));
            mHero.setmAgeHero(intent.getStringExtra("AgeHero"));
            mHero.setmCity(intent.getStringExtra("City"));
            mHero.setmSignOfTheZodiac(intent.getStringExtra("SignOfTheZodiac"));
            mHero.setmDescription(intent.getStringExtra("Description"));
            mHero.setmPhoto(intent.getStringExtra("Photo"));
            mHero.setmHeroId(intent.getStringExtra("HeroId"));

            writtenToTheDatabase(mHero);
            refreshHeroesList();
        }
    }

    private HeroesReceiver mReceiver;

    @Override
    protected void onResume() {
        IntentFilter mFilter = new IntentFilter(NEW_DATA_HERO);
        mReceiver = new HeroesReceiver();
        registerReceiver(mReceiver, mFilter);
        createDatabase();
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mReceiver);
        if(mHeroesDB != null) {
            mHeroesDB.close();
        }
        super.onPause();
    }

    @Override
    protected void finalize() throws Throwable {
        if(mHeroesDB != null) {
            mHeroesDB.close();
        }
        super.finalize();
    }

    private void createDatabase(){ // база для хранения участников
        mHeroesDB = openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
        mHeroesDB.execSQL(TABLE_CREATE);
    }

    private void writtenToTheDatabase(Hero hero){ // вставить в базу если есть id и фамилия участника
        if(hero.getmHeroId().length()>0 && hero.getmFio().length()>0){
            mHeroesDB.execSQL("INSERT OR REPLACE INTO heroes (heroId, fio, daysOfTheShow, startDate, ageHero, city, signOfTheZodiac, description, photo, dateOfRenovation) VALUES (\""+hero.getmHeroId()+"\", \""+hero.getmFio()+"\", \""+hero.getmDaysOfTheShow()+"\", \""+hero.getmStartDate()+"\", \""+hero.getmAgeHero()+"\", \""+hero.getmCity()+"\", \""+hero.getmSignOfTheZodiac()+"\", \""+new String(Base64.encode(hero.getmDescription().getBytes(), 0))+"\", \""+hero.getmPhoto()+"\", datetime())");
        }
    }



    private void refreshHeroesList(){
        Cursor mAllHeroesTableCursor = mHeroesDB.query(DATABASE_TABLE, new String[]{"_id", "fio", "ageHero", "city", "daysOfTheShow", "photo"}, null, null, null, null, "daysOfTheShow DESC");

        mHeroesListAdapter = new HeroesListAdapter(MainActivity.this, mAllHeroesTableCursor, false);

        lvItems.setAdapter(mHeroesListAdapter);
    }
}
