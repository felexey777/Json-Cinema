package tk.site_guru.kinopoiskkodekolomatskiy;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

public class InfoFilm extends AppCompatActivity {
    int idFilm = 0;
    TextView textView;
    public static String LOG_TAG = "alex";
    int idCity = 490;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_film);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InfoFilm.this, Seances.class);
                String f = ""+idFilm;
                intent.putExtra("Film", f);
                String c = ""+ idCity;
                intent.putExtra("City", c);
                        
                startActivity(intent);
            }
        });
        Intent intent = getIntent();
       // idFilm = Integer.parseInt(intent.getStringExtra("idnewFilm"));
        Log.d(LOG_TAG, "" + idFilm);
        idFilm = intent.getIntExtra("idnewFilm", 100);
        Log.d(LOG_TAG, "" + idFilm);
        idCity = intent.getIntExtra("idCity", 1);
        Log.d(LOG_TAG, "" + idCity);

        textView = (TextView)findViewById(R.id.idFilm);
        //textView.setText(intent.getIntExtra("idnewFilm",100));
        new ParseTask().execute();

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

            String get_link = "http://api.kinopoisk.cf/getFilm?filmID=" + idFilm;


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
             Log.d(LOG_TAG, strJson);



            JSONObject dataJsonObj = null;
            String filmName = "";
            String rating = "";
            String id_film = "";
            String genre = "No genre";
            double count = 0.0;
            int id = 0;

            try {
                dataJsonObj = new JSONObject(strJson);
                String nameFilm = dataJsonObj.getString("nameRU");
                textView.setText(nameFilm);
                textView.append("\n");
                String year = dataJsonObj.getString("year");
                textView.append(year);
                textView.append("\n");
                String country = dataJsonObj.getString("country");
                textView.append(country);
                textView.append("\n");
                if(dataJsonObj.has("genre")) {
                    genre = dataJsonObj.getString("genre");
                    textView.append(genre);
                    textView.append("\n");
                }
                if(dataJsonObj.has("filmLength")) {
                    String filmLength = dataJsonObj.getString("filmLength");
                    textView.append(filmLength);
                    textView.append("\n");
                }

                String des = dataJsonObj.getString("description");
                textView.append(des);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d(LOG_TAG,   "no JSON");
            }
        }
    }

}
