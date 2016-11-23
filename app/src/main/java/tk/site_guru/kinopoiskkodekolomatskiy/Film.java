package tk.site_guru.kinopoiskkodekolomatskiy;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

/**
 * Created by Alex on 08.10.2016.
 */
public class Film {
    private int id;
    private String name;
     Double rating;
    private String[] genre;
    public Film(int id, String name, double rating, String[] genre){
        this.id = id;
        this.name = name;
        this.rating = rating;
        this.genre = genre;
    }
    public int getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public double getRating(){
        return rating;
    }
    public String[] getGenre(){
        return genre;
    }



}
