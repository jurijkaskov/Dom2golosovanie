package jurijkaskov.com.dom2golosovanie;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

/**
 * Created by raccoon on 12.04.2015.
 */
public class HeroesListAdapter extends CursorAdapter {

    public HeroesListAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //return null;
        LayoutInflater mInflater = LayoutInflater.from(context);
        View mView = mInflater.inflate(R.layout.heroes_list_one_item, parent, false);

        ViewHolder mHolder = new ViewHolder();

            mHolder.ivPhotoHero = (ImageView) mView.findViewById(R.id.photo_hero);
            mHolder.tvFio = (TextView) mView.findViewById(R.id.fio);
            mHolder.tvAgeHero = (TextView) mView.findViewById(R.id.age_hero);
            mHolder.tvCity = (TextView) mView.findViewById(R.id.city);
            mHolder.tvDaysOfTheShow = (TextView) mView.findViewById(R.id.days_of_the_show);
            mView.setTag(mHolder);


        return mView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder mHolder = (ViewHolder)view.getTag();

        String mPhotoUrl = cursor.getString(cursor.getColumnIndexOrThrow("photo"));

        File mFile = new File(mPhotoUrl);
        if(mFile.exists()) {
            mHolder.ivPhotoHero.setImageURI(Uri.parse(mPhotoUrl));
        }else{
            Drawable mDefPhoto = context.getResources().getDrawable(R.drawable.defaultphoto);
            mHolder.ivPhotoHero.setImageDrawable(mDefPhoto);
        }


        mHolder.tvFio.setText(cursor.getString(cursor.getColumnIndexOrThrow("fio")));
        mHolder.tvAgeHero.setText(cursor.getString(cursor.getColumnIndexOrThrow("ageHero")));
        mHolder.tvCity.setText(cursor.getString(cursor.getColumnIndexOrThrow("city")));
        mHolder.tvDaysOfTheShow.setText(cursor.getString(cursor.getColumnIndexOrThrow("daysOfTheShow")));
    }

    private static class ViewHolder {
        ImageView ivPhotoHero;
        TextView tvFio;
        TextView tvAgeHero;
        TextView tvCity;
        TextView tvDaysOfTheShow;
    }
}
