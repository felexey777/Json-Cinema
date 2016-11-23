package tk.site_guru.kinopoiskkodekolomatskiy;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class City extends AppCompatActivity implements View.OnClickListener{
    ListView lvMain;
    String[] names;
    public static String LOG_TAG = "alex";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        lvMain = (ListView) findViewById(R.id.lvMain);
        // устанавливаем режим выбора пунктов списка
        lvMain.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // Создаем адаптер, используя массив из файла ресурсов
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.city,
                android.R.layout.simple_list_item_single_choice);
        lvMain.setAdapter(adapter);
        lvMain.setItemChecked(0, true);
        // получаем массив из файла ресурсов
        names = getResources().getStringArray(R.array.city);
    }

    public void onClick(View arg0) {
        // пишем в лог выделенный элемент
        Log.d(LOG_TAG, "checked: " + names[lvMain.getCheckedItemPosition()]);
        Intent intent = new Intent(City.this,MainActivity.class);
        intent.putExtra("City",names[lvMain.getCheckedItemPosition()]);
        startActivity(intent);
    }

}
