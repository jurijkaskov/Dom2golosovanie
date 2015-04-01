package jurijkaskov.com.dom2golosovanie;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by raccoon on 29.03.2015.
 */
public class Dom2ruParser {
    private URL url = null;

    public Dom2ruParser(String _url) {
        try {
            url = new URL(_url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void updateDB(String id){
        //
    }

    // сохранение в локальную базу участников
    public ArrayList updateHeroesList(){
        ArrayList heroes = new ArrayList();
        String html =  getPage(url);

        Pattern regex = Pattern.compile("<a href=\"/heroes/[0-9]{1,}\"><div"); //<a href="/heroes/[0-9]{1,}"><div
        Matcher regexMatcher = regex.matcher(html);

        // парсинг id участников со страницы http://dom2.ru/heroes
        while(true) {
            if (regexMatcher.find()) {
                String link = regexMatcher.group(0).trim();
                if (link.length() > 0) {
                    String s = regexMatcher.group(0).trim();
                    String hero = s.substring(17, s.length()-6);
                    heroes.add(hero);
                }
            }else{
                break;
            }
        }
        return heroes;
    }

    // get запрос
    public String getPage(URL _url){
        try{
            URLConnection connection;
            connection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection)connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        httpConnection.getInputStream(), "UTF-8"));
                String inputLine;
                StringBuilder a = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    a.append(inputLine);
                }
                in.close();
                return a.toString();
            }
        }catch (Exception ex){
            //
        }
        return "";
    }
}
