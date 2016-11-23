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
import java.util.Calendar;

public class Seances extends AppCompatActivity {
    int idFilm = 0;
    int idCity = 490;
    public static String LOG_TAG = "alex";
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seances);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, getString(R.string.dev), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        Intent intent = getIntent();
        Log.d(LOG_TAG, intent.getStringExtra("Film"));
        Log.d(LOG_TAG, intent.getStringExtra("City"));
        idFilm = Integer.parseInt( intent.getStringExtra("Film"));
        idCity = Integer.parseInt(intent.getStringExtra("City"));
        Log.d(LOG_TAG, "" + idFilm);
        Log.d(LOG_TAG, "" + idCity);

        textView = (TextView)findViewById(R.id.seances);
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

            String get_link = "http://api.kinopoisk.cf//getSeance?filmID=" + idFilm + "&cityID=" + idCity + "&date=" + formatted ;


            // Log.d(LOG_TAG,   formatted);
             Log.d(LOG_TAG,   get_link);
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

            String genre = "No genre";


            try {
                dataJsonObj = new JSONObject(strJson);
                String date = dataJsonObj.getString("date");
                textView.setText(date);
                textView.append("\n");
                String city = dataJsonObj.getString("cityName");
                textView.append(city);
                textView.append("\n");
                String nameFilm = dataJsonObj.getString("nameRU");
                textView.append(nameFilm);
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
                    textView.append("\n");
                }
                if(dataJsonObj.has("filmLength")) {
                    String filmLength = dataJsonObj.getString("filmLength");
                    textView.append(filmLength);
                    textView.append("\n");
                }

                JSONArray filmsData = dataJsonObj.getJSONArray("items");
                for(int i = 0; i < filmsData.length(); i ++){
                    JSONObject cinema = filmsData.getJSONObject(i);
                    String cinemaName = cinema.getString("cinemaName");
                    textView.append(cinemaName);
                    textView.append("\n");
                    String address = cinema.getString("address");
                    textView.append(address);
                    textView.append("\n");
                    textView.append("\n");
                    JSONArray seance = cinema.getJSONArray("seance");
                    for(int x = 0;x < seance.length(); x ++){
                        JSONObject time = seance.getJSONObject(x);
                        String timeFilm = time.getString("time");
                        textView.append(timeFilm);
                        textView.append("\n");
                        textView.append("\n");
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
                Log.d(LOG_TAG,   "no JSON");
            }
        }
    }

}
