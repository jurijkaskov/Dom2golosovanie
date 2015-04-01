package jurijkaskov.com.dom2golosovanie;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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

    static int count =1;
    public void downloadHero(String id){
        StringBuilder sb = new StringBuilder();
        sb.append("http://dom2.ru/heroes/");
        sb.append(id);
        String  html = "";
        try {
            html = getPage(new URL(sb.toString()));Log.i("url", sb.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (html.length() == 0 || html == null)return; // если страница не получена или пустая, то пропускается участник

        Hero hero = new Hero();
        hero.setFio(this.parseHeroName(html, "<h2>[A-Za-zА-Яа-я ]{1,}</h2>")+"-]"); // имя фамилия героя

        //Log.i("666", count + "=" + id+ "[-"+this.parseHeroName(html, "<h2>[A-Za-zА-Яа-я ]{1,}</h2>")+"-]");
        count++;

    }

    // сохранение в локальную базу участников
    public ArrayList updateHeroesList(){
        ArrayList heroes = new ArrayList();
        String html =  this.getPage(url);

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
            connection = _url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection)connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        httpConnection.getInputStream(), "UTF-8"));
                String inputLine;
                StringBuilder a = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    a.append(inputLine); //Log.i("html", inputLine);
                }
                in.close();
                return a.toString();
            }
        }catch (Exception ex){
            //
        }
        return "";
    }

    private String parseHeroName(String html, String pattern){
        String result = "";
        Pattern regex = Pattern.compile(pattern);
        Matcher regexMatcher = regex.matcher(html);

        if (regexMatcher.find()) {
            String fio = regexMatcher.group(0).trim();
            if (fio.length() > 0) {
                return fio.substring(4, fio.length()-5);
            }
        }
        return result;
    }
}
