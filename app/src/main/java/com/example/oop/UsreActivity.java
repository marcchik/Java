package com.example.oop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class UsreActivity extends AppCompatActivity {

    //название выбранного предмета
    String NameSelectedSubject;

    //номер выбранного предмета
    String IDSelectedSubject;

    //количество сданных лаб
    int amountCompletedLab;

    //ID авторизованного студента
    int ID;

    //Имя авторизованного студента
    String NAME;

    //массив с юзерами
    private List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usre);

        //получаем ID студента из JSONE (студент, который авторизовался)
        ID = openId();

        //получаем имя студента из JSONE (студент, который авторизовался)
        NAME = openName();

        //устанавливаем выбранный предмет в TextView
        TextView IdText = findViewById(R.id.idProfile);
        IdText.setText(String.valueOf(NAME));

        //подключение к БД
        SQLiteDatabase db = getBaseContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null);

        //селект предметов из БД
        Cursor query = db.rawQuery("SELECT * FROM SUBJECT;", null);

        //селект сданных лаб из БД
        Cursor queryLab = db.rawQuery("SELECT ID_SUB, ID_STUD, NAME_SUBJECT, QUANTITY, ID_SUBJECT FROM COMPLETED, SUBJECT WHERE ID_SUBJECT = ID_SUB and ID_STUD = " + ID + ";", null);

        //id и название предмета
        int Column1 = query.getColumnIndex("ID_SUBJECT");
        int Column2 = query.getColumnIndex("NAME_SUBJECT");

        //id и название предмета
        int NAME_SUBJECT = queryLab.getColumnIndex("NAME_SUBJECT");
        int QUANTITY = queryLab.getColumnIndex("QUANTITY");

        //структура для записи предметов из БД
        ArrayList<String> sData = new ArrayList<String>();

        //структура для записи сданых лаб из БД
        ArrayList<String> CompletedLab = new ArrayList<String>();

        //счетчик цикла для элементво масива
        int i = 0, j = 0;
        // объявление массива с предметами
        String[] subjectArray = new String[8];
        // объявление массива с ID предметами
        String[] subjectIDArray = new String[8];
        // объявление массива со сданными лабами
        //String[] completedLabArray = new String[4];

        while (query.moveToNext()) {
            String ID = query.getString(Column1);
            String NAME = query.getString(Column2);
            sData.add(ID + " " + NAME);
            subjectArray[i] = NAME;
            subjectIDArray[i] = ID;
            i++;
        }

        while (queryLab.moveToNext()) {
            String NAME_S = queryLab.getString(NAME_SUBJECT);
            String QUANTITY_S = queryLab.getString(QUANTITY);
            CompletedLab.add(NAME_S + "  - " + QUANTITY_S);
            //completedLabArray[j] = QUANTITY_S;
            j++;
        }

        ListView subjectListView = (ListView) findViewById(R.id.subjectList);
        subjectListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, sData));

        ListView statisticsList = (ListView) findViewById(R.id.statisticsList);
        statisticsList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, CompletedLab));


        // добавляем для списка слушатель
        subjectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // по позиции получаем выбранный элемент
                String selectedNameItem = subjectArray[position];
                NameSelectedSubject = selectedNameItem;

                // по позиции получаем выбранный элемент
                String selectedIdItem = subjectIDArray[position];
                IDSelectedSubject = selectedIdItem;

                //устанавливаем выбранный предмет в TextView
                TextView subjectName = findViewById(R.id.subjectName);
                subjectName.setText(NameSelectedSubject);

            }
        });

        SeekBar seekBar = findViewById(R.id.amountLabSeekBar);
        TextView textView = findViewById(R.id.amountLabSeekBarValue);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                textView.setText(String.valueOf(progress));
                amountCompletedLab = progress;

                TextView amountCompletedLab = findViewById(R.id.amountCompletedLab);
                amountCompletedLab.setText(String.valueOf(progress));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }
    //пункты меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        TextView headerView = findViewById(R.id.textView2);
        switch(id){
            case R.id.action_settings :
            case R.id.save_settings:
                //создание нового экрана
                Intent intent = new Intent();
                intent.setClass(UsreActivity.this, LoginActivity.class);
                startActivity(intent);
                return true;
            case R.id.open_settings:
                headerView.setText("Помощь");
                return true;
        }
        //headerView.setText(item.getTitle());
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.header, menu);
        return true;
    }

    //вставка статистики сданных лаб
    public void amountLabResult(View view) {

        // Создайте новую строку со значениями для вставки.
        ContentValues newValues = new ContentValues();

        //подключение к БД
        SQLiteDatabase db = getBaseContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null);

        //селект сданных лаб из БД после вставки
        Cursor queryLabUpdate = db.rawQuery("SELECT ID_SUB, ID_STUD, NAME_SUBJECT, QUANTITY, ID_SUBJECT FROM COMPLETED, SUBJECT WHERE ID_SUBJECT = ID_SUB and ID_STUD = " + ID + ";", null);

        //структура для записи сданых лаб из БД
        ArrayList<String> CompletedLab = new ArrayList<String>();

        //id и название предмета
        int NAME_SUBJECT = queryLabUpdate.getColumnIndex("NAME_SUBJECT");
        int QUANTITY = queryLabUpdate.getColumnIndex("QUANTITY");

        while (queryLabUpdate.moveToNext()) {
            String NAME_S = queryLabUpdate.getString(NAME_SUBJECT);
            String QUANTITY_S = queryLabUpdate.getString(QUANTITY);
            CompletedLab.add(NAME_S + "  - " + QUANTITY_S);
        }

        ListView statisticsList = (ListView) findViewById(R.id.statisticsList);
        statisticsList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, CompletedLab));


        //вставляем в бд в таблицу сданные лабы строку с ответом студента
        ContentValues queryCompletedLab = new ContentValues();
        queryCompletedLab.put("QUANTITY", amountCompletedLab);


        //селект сданных лаб из БД
        int countUpdateRows = db.update("COMPLETED", queryCompletedLab, "ID_STUD = " + ID + " and ID_SUB = '" + IDSelectedSubject + "'", null);

        //если не было статистики по данной лабе то вставить строку
        if (countUpdateRows == 0) {
            //вставляем в бд в таблицу сданные лабы строку с ответом студента
            queryCompletedLab.put("ID_STUD", ID);
            queryCompletedLab.put("ID_SUB", IDSelectedSubject);
            db.insert("COMPLETED", null, queryCompletedLab);
        }
    }

    //восстановления данных
    public int openId() {
        users = JSONHelper.importFromJSON(this);
        if (users != null) {

            int IdJson = users.get(0).getId();
            Toast.makeText(this, "Успешно авторизован", Toast.LENGTH_LONG).show();
            return IdJson;
        } else {
            Toast.makeText(this, "Не удалось авторизоваться", Toast.LENGTH_LONG).show();
            return 1;
        }
    }

    //восстановления данных
    public String openName() {
        users = JSONHelper.importFromJSON(this);
        if (users != null) {
            String IdJson = users.get(0).getName();
            Toast.makeText(this, "Успешно авторизован", Toast.LENGTH_LONG).show();
            return IdJson;
        } else {
            Toast.makeText(this, "Не удалось авторизоваться", Toast.LENGTH_LONG).show();
            return "Имя Фамилия";
        }
    }



}