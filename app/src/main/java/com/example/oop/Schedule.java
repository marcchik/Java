package com.example.oop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class Schedule extends AppCompatActivity {

    //id дня недели расписания
    int idDay;

    //ID авторизованного студента
    int ID;

    //ID специальности авторизованного студента
    int idSpec;

    //номер выбранной пары
    String IDSelectedPair;

    //выбранная дата
    String date;

    //массив с юзерами
    private List<User> users;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        TextView toolbar = (TextView) findViewById(R.id.day);
        TextView dateTextView = (TextView) findViewById(R.id.dateTextView);

        //получаем ID студента из JSONE (студент, который авторизовался)
        ID = openId();

        Bundle bundle = getIntent().getExtras();
        String day = bundle.getString("day");
        toolbar.setText("День: "+ day);

        dateTextView.setText("Дата: " + bundle.getString("date"));
        date = bundle.getString("date");

        if (day.equals("понедельник"))
            idDay = 1;
        else if (day.equals("вторник"))
            idDay = 2;
        else if (day.equals("среда"))
            idDay = 3;
        else if (day.equals("четверг"))
            idDay = 4;
        else if (day.equals("пятница"))
            idDay = 5;
        else if (day.equals("суббота"))
            idDay = 6;


        //подключение к БД
        SQLiteDatabase db = getBaseContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null);

        //получаем id группы авторизованного студента
        Cursor queryIdSpec = db.rawQuery("SELECT ID_SPEC FROM STUDENTS WHERE ID_STUDENT = " + ID + ";", null);

        //id группы строка
        int idSpecCursor = queryIdSpec.getColumnIndex("ID_SPEC");

        //id группы
        idSpec = -1;


        while (queryIdSpec.moveToNext()) {
            idSpec = queryIdSpec.getInt(idSpecCursor);
        }

        //селект предметов из БД
        Cursor query = db.rawQuery("SELECT NUMBER_DAY, CODE_SUBJECT, ID_S, NAME_SUBJECT FROM SCHEDULE, SUBJECT WHERE CODE_SUBJECT = ID_SUBJECT and NUMBER_DAY = " + idDay + " and ID_S = " + idSpec + ";", null);

        //id и название предмета
        int Column1 = query.getColumnIndex("NUMBER_DAY");
        int Column2 = query.getColumnIndex("CODE_SUBJECT");
        int Column3 = query.getColumnIndex("ID_S");
        int Column4 = query.getColumnIndex("NAME_SUBJECT");


        ArrayList<String> sData = new ArrayList<String>();

        //счетчик цикла для элементво масива
        int i = 0;
        // объявление массива с парами
        String[] pairArray = new String[10];

        //номер пары
        int number = 0;

        //время для расписания
        //начало пары
        Calendar beginPair = new GregorianCalendar(2017, Calendar.JANUARY , 25);
        beginPair.set(Calendar.HOUR, 8);
        beginPair.set(Calendar.MINUTE, 10);
        //конец пары
        Calendar endPair = new GregorianCalendar(2017, Calendar.JANUARY , 25);
        endPair.set(Calendar.HOUR, 9);
        endPair.set(Calendar.MINUTE, 30);

        while (query.moveToNext()) {
            number++;
            String NUMBER_DAY = query.getString(Column1);
            String CODE_SUBJECT = query.getString(Column2);
            String ID_S = query.getString(Column3);
            String NAME_SUBjECT = query.getString(Column4);
            sData.add(number + ". " + beginPair.get(Calendar.HOUR) + ":" + beginPair.get(Calendar.MINUTE) + " - "
                    + endPair.get(Calendar.HOUR) + ":" + endPair.get(Calendar.MINUTE) + "   "
                    + NAME_SUBjECT + "  | Забронировать место");

            //добавляем время одной пары и перерыва к началу
            beginPair.roll(Calendar.HOUR, +1);
            beginPair.roll(Calendar.MINUTE, +35);

            //добавляем время одной пары и перерыва к концу
            endPair.roll(Calendar.HOUR, +1);
            endPair.roll(Calendar.MINUTE, +35);

            pairArray[i] = NAME_SUBjECT;
            i++;
        }

        // получаем элемент ListView для расписания
        ListView scheduleListView = (ListView) findViewById(R.id.scheduleList);
        //заполняем ListView элементами из БД
        scheduleListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, sData));


        // добавляем для списка слушатель
        scheduleListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                // по позиции получаем выбранный элемент
                String selectedItem = pairArray[position];
                IDSelectedPair = selectedItem;

                //создание нового экрана
                Intent intent = new Intent();
                intent.setClass(Schedule.this, reservedActivity.class);

                Bundle b = new Bundle();
                //передаем выбранную пару
                b.putString("subjectID", IDSelectedPair);
                //передаем выбранную дату
                b.putString("date", date);

                //передаем выбранный предмет
                b.putString("subject", IDSelectedPair);

                intent.putExtras(b);
                startActivity(intent);
            }
        });
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
                intent.setClass(Schedule.this, LoginActivity.class);
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
}