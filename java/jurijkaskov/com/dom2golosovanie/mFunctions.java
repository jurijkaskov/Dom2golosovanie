package jurijkaskov.com.dom2golosovanie;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by raccoon on 07.04.2015.
 */
public class mFunctions{
    public static String getPage(String url){
        String mResult = "";

        try {
            URL mUrl = new URL(url);
            URLConnection mConnection;
            mConnection = mUrl.openConnection();
            HttpURLConnection mHttpConnection = (HttpURLConnection) mConnection;
            int mResponseCode = mHttpConnection.getResponseCode();
            if (mResponseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader mIn = new BufferedReader(new InputStreamReader(
                        mHttpConnection.getInputStream(), "UTF-8"));
                String mInputLine;
                StringBuilder mSB = new StringBuilder();
                while ((mInputLine = mIn.readLine()) != null) {
                    mSB.append(mInputLine);
                }
                mIn.close();
                mResult = mSB.toString();
            }
        }
        catch (Exception ex){
            Log.i("ERR", ex.getMessage());
            return mResult;
        }
        return  mResult;
    } // get запрос
    public static ArrayList<String> getGeroesList(String html){
        ArrayList<String> mHeroes = new ArrayList();

        Pattern mRegex = Pattern.compile("<a href=\"/heroes/[0-9]{1,}\"><div"); //<a href="/heroes/[0-9]{1,}"><div
        Matcher mRegexMatcher = mRegex.matcher(html);

        // парсинг id участников со страницы http://dom2.ru/heroes
        while(true) {
            if (mRegexMatcher.find()) {
                String link = mRegexMatcher.group(0).trim();
                if (link.length() > 0) {
                    String mTmpString = mRegexMatcher.group(0).trim();
                    String hero = mTmpString.substring(17, mTmpString.length()-6);
                    mHeroes.add(hero);
                }
            }else{
                break;
            }
        }
        return  mHeroes;
    } // список id участников
    public static Hero downloadHero(String id){
        Hero mHero = new Hero();
        mHero.setmFio("");
        mHero.setmDaysOfTheShow("0");
        mHero.setmStartDate("0");
        mHero.setmAgeHero("0");
        mHero.setmCity("");
        mHero.setmSignOfTheZodiac("");
        mHero.setmDescription("");
        mHero.setmPhoto("");
        mHero.setmHeroId("");

        StringBuilder mSB = new StringBuilder();
        mSB.append("http://dom2.ru/heroes/");
        mSB.append(id);
        String  mHTML = "";

        mHTML = mFunctions.getPage(mSB.toString());
        if (mHTML.length() == 0 || mHTML == null)
            return mHero;

        mHero.setmFio(mFunctions.parseHeroName(mHTML, "<h2>[A-Za-zА-Яа-я ]{1,}</h2>")); // имя фамилия героя
        mHero.setmDaysOfTheShow(mFunctions.parseDaysOfTheShow(mHTML, "[0-9]{1,}\\s[а-я]{1,}<p class='date'>c\\s\\s<nobr>(.*?)</nobr></p>")); // всего дней на шоу
        mHero.setmStartDate(mFunctions.parseStartDate(mHTML, "[0-9]{1,}\\s[а-я]{1,}<p class='date'>c\\s\\s<nobr>(.*?)</nobr></p>")); // день прихода
        mHero.setmAgeHero(mFunctions.parseAgeHero(mHTML, "<td class='right'>(.*?)\\s(лет|года|год)\\s</td>")); // возраст
        mHero.setmCity(mFunctions.parseCity(mHTML, "<td class='left'>город</td><td class='right'>(.*?)</td>")); // город
        mHero.setmSignOfTheZodiac(mFunctions.parseSignOfTheZodiac(mHTML, "<td class='right'><span class='relative'><span>(.*?)<img src")); // зодиак
        mHero.setmDescription(mFunctions.parseDescription(mHTML, "<div class=\"content-text\">\\s+<p(.*?)>(.*?)<\\/div>")); // описание героя
        mHero.setmPhoto(mFunctions.parsePhoto(mHTML, "<link rel=\"image_src\" type=\"image\\/jpeg\" href=\"(.*?)\"\\/>")); // фото
        mHero.setmHeroId(id);



        return mHero;
    }

    private static String parseHeroName(String html, String pattern){
        String mResult = "";
        Pattern mRegex = Pattern.compile(pattern);
        Matcher regexMatcher = mRegex.matcher(html);

        if (regexMatcher.find()) {
            String mFio = regexMatcher.group(0).trim();
            if (mFio.length() > 0) {
                return mFio.substring(4, mFio.length()-5).trim();
            }
        }
        return mResult;
    }

    private static String parseDaysOfTheShow(String html, String pattern){
        String mResult = "";

        Pattern mRegex = Pattern.compile(pattern);
        Matcher regexMatcher = mRegex.matcher(html);

        if (regexMatcher.find()) {
            String mDaysOfTheShow = regexMatcher.group(0).trim();
            if (mDaysOfTheShow.length() > 0) {
                String[] mParts = mDaysOfTheShow.split(" ");
                return mParts[0].trim();
            }
        }

        return mResult;
    }

    private static String parseStartDate(String html, String pattern){
        String mResult = "";

        Pattern mRegex = Pattern.compile(pattern);
        Matcher mRegexMatcher = mRegex.matcher(html);

        if (mRegexMatcher.find()) {
            String mStartDate = mRegexMatcher.group(1).trim();
            if (mStartDate.length() > 0) {
                return mStartDate.trim();
            }
        }

        return mResult;
    }

    private static String parseAgeHero(String html, String pattern){
        String mResult = "";

        Pattern mRegex = Pattern.compile(pattern);
        Matcher mRegexMatcher = mRegex.matcher(html);

        if (mRegexMatcher.find()) {
            String mAgeHero = mRegexMatcher.group(1).trim();
            if (mAgeHero.length() > 0) {
                return mAgeHero.trim();
            }
        }

        return mResult;
    }

    private static String parseCity(String html, String pattern){
        String mResult = "";

        Pattern mRegex = Pattern.compile(pattern);
        Matcher mRegexMatcher = mRegex.matcher(html);

        if (mRegexMatcher.find()) {
            String mHCity = mRegexMatcher.group(1).trim();
            if (mHCity.length() > 0) {
                return mHCity.trim();
            }
        }

        return mResult;
    }

    private static String parseSignOfTheZodiac(String html, String pattern){
        String mResult = "";

        Pattern mRegex = Pattern.compile(pattern);
        Matcher mRegexMatcher = mRegex.matcher(html);

        if (mRegexMatcher.find()) {
            String mSignOfTheZodiac = mRegexMatcher.group(1).trim();
            if (mSignOfTheZodiac.length() > 0) {
                return mSignOfTheZodiac.trim();
            }
        }

        return mResult;
    }

    private static String parseDescription(String html, String pattern){
        String mResult = "";

        Pattern mRegex = Pattern.compile(pattern);
        Matcher mRegexMatcher = mRegex.matcher(html);

        if (mRegexMatcher.find()) {
            String mDescription = mRegexMatcher.group(0).trim();
            if (mDescription.length() > 0) {
                return mDescription.replaceAll("<!--(.*)-->", "").replaceAll("<span(.*?)>", "<span>").replaceAll("<p(.*?)>", "<p>").replaceAll("<a(.*?)</a>", "").replaceAll("<br />", "").replaceAll("</div>", "").replaceAll("<div class=\"content-text\">", "").replaceAll("<p></p>", "").replaceAll("<p>&nbsp;</p>", "").trim();
            }
        }

        return mResult;
    }

    private static String parsePhoto(String html, String pattern){
        String mResult = "";

        Pattern mRegex = Pattern.compile(pattern);
        Matcher mRegexMatcher = mRegex.matcher(html);

        if (mRegexMatcher.find()) {
            String mPhoto = mRegexMatcher.group(1).trim();
            if (mPhoto.length() > 0) {
                return mPhoto.trim();
            }
        }

        return mResult;
    }

    public static String downloadPhoto(String urlPhoto, String photoPath) { // фото перезаписываются
        String mFileName = "";
        String mResult = "";

        String[] mSplit = urlPhoto.split("/");
        mFileName = mSplit[mSplit.length-1];

        try {
            File mDestFile = new File(photoPath + "/" + mFileName);

            URL mU = new URL(urlPhoto);
            URLConnection mConn = mU.openConnection();
            int contentLength = mConn.getContentLength();
            DataInputStream mStream = new DataInputStream(mU.openStream());
            byte[] buffer = new byte[contentLength];
            mStream.readFully(buffer);
            mStream.close();
            DataOutputStream mFos = new DataOutputStream(new FileOutputStream(mDestFile));
            mFos.write(buffer);
            mFos.flush();
            mFos.close();

            if(mDestFile.exists()) {
                mResult = photoPath + "/" + mFileName;
            }
        }
        catch (Exception ex){
            return mResult;
        }

        return mResult;
    }
}
