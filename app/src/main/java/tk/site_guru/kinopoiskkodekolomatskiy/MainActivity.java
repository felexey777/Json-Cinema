package tk.site_guru.kinopoiskkodekolomatskiy;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static String LOG_TAG = "alex";
    RatingCompare comp;
    ListView lvMain;
    ArrayList<Film> boxFilm;
    String[] names;
    FloatingActionButton fab;
    boolean knock = true;
    int cityID = 490;
    ArrayList<String> genreFilm;
    ArrayList<String> genreFilmCopy;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //сортируем фильмы по рейтингу и меняем картинку кнопки
                int hop = 0;
                for(int i = 0; i < boxFilm.size(); i ++){
                    if(boxFilm.get(i)!=null){
                        hop ++;
                    }


                }
                if (knock) {

                    for (int i = 0; i < hop; i++) {

                        names[i] = boxFilm.get(i).getName() + "\n" + getString(R.string.rating) + " " + boxFilm.get(i).getRating()
                                + "\n" + getString(R.string.genre) + " " + getgenreFilm(boxFilm.get(i));
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                            R.layout.my_list_item, names);
                    lvMain = (ListView) findViewById(R.id.lvMain);
                    lvMain.setAdapter(adapter);
                    fab.setImageResource(R.drawable.up_arrow);
                    knock = false;
                } else {
                    int k = 0;
                    for (int i = hop - 1; i >= 0; i--) {
                        names[k] = boxFilm.get(i).getName() + "\n" + getString(R.string.rating) + " " + boxFilm.get(i).getRating()
                                + "\n" + getString(R.string.genre) + " " + getgenreFilm(boxFilm.get(i));
                        k++;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                            R.layout.my_list_item, names);
                    lvMain = (ListView) findViewById(R.id.lvMain);
                    lvMain.setAdapter(adapter);
                    fab.setImageResource(R.drawable.down_arrow);
                    knock = true;
                }
            }
        });


        Intent intent = getIntent();

        if (intent.getStringExtra("City") != null) {
            Log.d(LOG_TAG, "выбранный город: " + intent.getStringExtra("City"));
            if (intent.getStringExtra("City").equals("Калининград")) {
                cityID = 490;
            } else if (intent.getStringExtra("City").equals("Москва")) {
                cityID = 1;
            } else {
                cityID = 2;
            }
            new ParseTask().execute();
        }

        if (intent.getStringArrayListExtra("genresOfFilm") != null) {
            genreFilm = intent.getStringArrayListExtra("genresOfFilm");
            new ParseGenreTask().execute();



        } else {
            new ParseTask().execute();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


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
        if (id == R.id.genre) {
            Intent intent = new Intent(MainActivity.this, GenreSelect.class);

            startActivity(intent);
            return true;
        } else {
            Intent intent = new Intent(MainActivity.this, City.class);
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

    }

    public void OnUp(View view) {


        FilterGenre();
    }



    private class ParseGenreTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {

            Calendar cal = Calendar.getInstance();
            //cal.add(Calendar.DATE, 1);
            SimpleDateFormat format1 = new SimpleDateFormat("dd.MM.yyyy");
            String formatted = format1.format(cal.getTime());

            String get_link = "http://api.kinopoisk.cf/getTodayFilms?date=" + formatted + "&cityID=" + cityID;


            // Log.d(LOG_TAG,   formatted);
            // Log.d(LOG_TAG,   get_link);
            // получаем данные с внешнего ресурса
            try {
                URL url = new URL(get_link);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();


            } catch (Exception e) {
                e.printStackTrace();
                Log.d(LOG_TAG, "no connect");
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            // выводим целиком полученную json-строку
            // Log.d(LOG_TAG, strJson);
            //сортируем с помощью Comporator
            comp = new RatingCompare();
            boxFilm = new ArrayList<Film>();
            JSONObject dataJsonObj = null;
            String filmName = "";
            String rating = "";
            String id_film = "";
            String genre = "No genre";
            double count = 0.0;
            int id = 0;

            try {
                dataJsonObj = new JSONObject(strJson);
                JSONArray filmsData = dataJsonObj.getJSONArray("filmsData");
                names = new String[filmsData.length()];
                for (int i = 0; i < filmsData.length(); i++) {

                    JSONObject secondFriend = filmsData.getJSONObject(i);
                    if (secondFriend.has("nameRu")) {
                        filmName = secondFriend.getString("nameRU");

                    }
                    filmName = secondFriend.getString("nameRU");
                    if (secondFriend.has("rating")) {
                        rating = secondFriend.getString("rating");
                        String[] f = rating.split(" ");
                        count = Double.parseDouble(f[0]);

                    }
                    if (secondFriend.has("id")) {
                        id_film = secondFriend.getString("id");
                        id = Integer.parseInt(id_film);


                    }



                    if (secondFriend.has("genre")) {
                        genre = secondFriend.getString("genre");
                        // Log.d(LOG_TAG, genre + " " + i);
                    }
                    String[] g = genre.split(", ");
                    Film film = new Film(id, filmName, count, g);
                    boxFilm.add(film);
                    // Log.d(LOG_TAG, "название фильма: " + filmName + " " + "/" + count + "/" + id + g[0] + i);
                   // names[i] = filmName + "\n" + getString(R.string.rating) + " " + count + "\n" + getString(R.string.genre) + " " + genre;
                    genre = "No genre";
                    filmName = "No film";
                    count = 0.0;

                }
                Collections.sort(boxFilm, comp);
                FilterGenre();
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d(LOG_TAG,   "no JSON");
            }
        }
    }
    private class ParseTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {

            Calendar cal = Calendar.getInstance();
            //cal.add(Calendar.DATE, 1);
            SimpleDateFormat format1 = new SimpleDateFormat("dd.MM.yyyy");
            String formatted = format1.format(cal.getTime());

            String get_link = "http://api.kinopoisk.cf/getTodayFilms?date=" + formatted + "&cityID=" + cityID;


            // Log.d(LOG_TAG,   formatted);
            // Log.d(LOG_TAG,   get_link);
            // получаем данные с внешнего ресурса
            try {
                URL url = new URL(get_link);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();


            } catch (Exception e) {
                e.printStackTrace();
                Log.d(LOG_TAG, "no connect");
            }
            return resultJson;
        }

        @Override
        protected void onPostExecute(String strJson) {
            super.onPostExecute(strJson);
            // выводим целиком полученную json-строку
            // Log.d(LOG_TAG, strJson);
            //сортируем с помощью Comporator
            comp = new RatingCompare();
            // TreeMap<Film, Film> boxFilm = new TreeMap<Film,Film>(comp);
            boxFilm = new ArrayList<Film>();


            JSONObject dataJsonObj = null;
            String filmName = "";
            String rating = "";
            String id_film = "";
            String genre = "No genre";
            double count = 0.0;
            int id = 0;

            try {
                dataJsonObj = new JSONObject(strJson);
                JSONArray filmsData = dataJsonObj.getJSONArray("filmsData");
                names = new String[filmsData.length()];
                for (int i = 0; i < filmsData.length(); i++) {

                    JSONObject secondFriend = filmsData.getJSONObject(i);
                    if (secondFriend.has("nameRu")) {
                        filmName = secondFriend.getString("nameRU");

                    }
                    filmName = secondFriend.getString("nameRU");
                    if (secondFriend.has("rating")) {
                        rating = secondFriend.getString("rating");
                        String[] f = rating.split(" ");
                        count = Double.parseDouble(f[0]);

                    }
                    if (secondFriend.has("id")) {
                        id_film = secondFriend.getString("id");
                        id = Integer.parseInt(id_film);


                    }



                    if (secondFriend.has("genre")) {
                        genre = secondFriend.getString("genre");
                        // Log.d(LOG_TAG, genre + " " + i);
                    }
                    String[] g = genre.split(", ");
                    Film film = new Film(id, filmName, count, g);
                    boxFilm.add(film);
                    // Log.d(LOG_TAG, "название фильма: " + filmName + " " + "/" + count + "/" + id + g[0] + i);
                    names[i] = filmName + "\n" + getString(R.string.rating) + " " + count + "\n" + getString(R.string.genre) + " " + genre;
                    genre = "No genre";
                    filmName = "No film";
                    count = 0.0;

                }
                Collections.sort(boxFilm, comp);
                // находим список
                lvMain = (ListView) findViewById(R.id.lvMain);

                // создаем адаптер
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                        R.layout.my_list_item, names);

                // присваиваем адаптер списку
                lvMain.setAdapter(adapter);
                lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        TextView textView = (TextView)view;
                        String s = (String)textView.getText();
                        String[] name = s.split("\n");
                        Film film = searchFilm(name[0]);
                        Log.d(LOG_TAG, "itemClick: position = " + position + ", id = " + id  + " "  + film.getName());

                        Log.d(LOG_TAG, " To info film");
                        int idnewfilm = film.getId();

                        Intent intent = new Intent(MainActivity.this, InfoFilm.class);
                        intent.putExtra("idnewFilm",idnewfilm);
                        intent.putExtra("idCity", cityID);
                        startActivity(intent);
                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
                Log.d(LOG_TAG,   "no JSON");
            }
        }
    }

    public String getgenreFilm(Film film) {
        String[] genre = film.getGenre();
        StringBuilder g = new StringBuilder();
        for (int i = 0; i < genre.length; i++) {
            g.append(genre[i]);
            if (i != genre.length - 1)
                g.append(", ");

        }
        String f = "" + g;
        return f;
    }

    private ArrayList<Film> FilterGenre() {
        ArrayList<String> a = genreFilm;
        ArrayList<Film> newFilm = new ArrayList<>();

        boolean svitch = true;


        for (int i = 0; i < boxFilm.size(); i++) {
            Film f = boxFilm.get(i);
            svitch = true;
            for (int y = 0; y < f.getGenre().length; y++) {
                if (svitch) {
                    String[] g = f.getGenre();
                    String genre = g[y];
                    for (int e = 0; e < a.size(); e++) {
                        if (genre.equals(a.get(e))) {
                            Log.d(LOG_TAG, "film has a genre");
                            newFilm.add(f);
//                          names[i] = boxFilm.get(i).getName() + "\n" + getString(R.string.rating) + " " + boxFilm.get(i).getRating()
//                                + "\n" + getString(R.string.genre) + " " + getgenreFilm(boxFilm.get(i));
                            svitch = false;
                            break;
                        } else {
                            svitch = true;
                        }
                    }
                }

            }

        }
        Log.d(LOG_TAG, "" + names.length);
        String[] newName = new String[newFilm.size()];
        for (int i = 0; i < newFilm.size(); i++) {

            newName[i] = newFilm.get(i).getName() + "\n" + getString(R.string.rating) + " " + newFilm.get(i).getRating()
                    + "\n" + getString(R.string.genre) + " " + getgenreFilm(newFilm.get(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.my_list_item, newName);
        lvMain = (ListView) findViewById(R.id.lvMain);

        lvMain.setAdapter(adapter);
        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                TextView textView = (TextView) view;
                String s = (String) textView.getText();
                String[] name = s.split("\n");
                Film film = searchFilm(name[0]);
                Log.d(LOG_TAG, "itemClick: position = " + position + ", id = " + id + " " + film.getName());

                Log.d(LOG_TAG, " To info film");
                int idnewfilm = film.getId();

                Intent intent = new Intent(MainActivity.this, InfoFilm.class);
                intent.putExtra("idnewFilm", idnewfilm);
                intent.putExtra("idCity", cityID);
                startActivity(intent);
            }
        });
        boxFilm.clear();
        boxFilm = newFilm;
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        return newFilm;
    }
    public  Film searchFilm(String film){
        for(int i = 0; i < boxFilm.size(); i ++) {

            if (film.equals(boxFilm.get(i).getName())) {
                return boxFilm.get(i);

            }
        }
        return null;
    }

    public Object onRetainCustomNonConfigurationInstance() {
        super.onRetainCustomNonConfigurationInstance();
        Log.d(LOG_TAG, "onRetainCustomNonConfigurationInstance()");

        return boxFilm;
    }
    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://tk.site_guru.kinopoiskkodekolomatskiy/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
        Log.d(LOG_TAG, "onStart");
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://tk.site_guru.kinopoiskkodekolomatskiy/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
        Log.d(LOG_TAG, "onStop");
    }

    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause");
    }
    protected void onRestart() {
        super.onRestart();
        Log.d(LOG_TAG, "onRestart");
    }
    protected void onResume() {
        super.onResume();

        Log.d(LOG_TAG, "onResume ");
    }
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(LOG_TAG, "onSaveInstanceState");
    }
}
