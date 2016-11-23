package tk.site_guru.kinopoiskkodekolomatskiy;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;

import java.util.ArrayList;

public class GenreSelect extends AppCompatActivity implements View.OnClickListener {
    ListView lvMain;
    String[] genre;
    ArrayList<Film> genreFilm;
    public static String LOG_TAG = "alex";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre_select);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        lvMain = (ListView) findViewById(R.id.lvMain);
        // устанавливаем режим выбора пунктов списка
        lvMain.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        // Создаем адаптер, используя массив из файла ресурсов
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.genre,
                android.R.layout.simple_list_item_multiple_choice);
        lvMain.setAdapter(adapter);
        // получаем массив из файла ресурсов
        genre = getResources().getStringArray(R.array.genre);
    }

    public void onClick(View arg0) {
        ArrayList<String> genreF = new ArrayList<String>();
        // пишем в лог выделенные элементы
       // Log.d(LOG_TAG, "checked: ");
        SparseBooleanArray sbArray = lvMain.getCheckedItemPositions();
        for (int i = 0; i < sbArray.size(); i++) {
            int key = sbArray.keyAt(i);
            if (sbArray.get(key))
               // Log.d(LOG_TAG, genre[key]);
            genreF.add(genre[key]);

        }
        Intent intent = new Intent(GenreSelect.this,MainActivity.class);
        intent.putExtra("genresOfFilm",genreF);
        startActivity(intent);
    }
}


