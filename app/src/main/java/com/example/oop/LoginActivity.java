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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    //меню
    private Menu menu;

    //массив с юзерами
    private List<User> users;

    //объекты кнопок
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //кнопка "Найти себя"
        loginButton = (Button)findViewById(R.id.loginButton);

        //обработчик события по клику на кнопку "Найти себя" с переходом на новый экран
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //создание нового экрана
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, StartMain.class);

                Bundle b = new Bundle();
                b.putString("classID", "11");
                intent.putExtras(b);
                //startActivity(intent);

                SearchF();

            }
        });
    }


    //поиск факультета в БД
    public void SearchF() {
        //выводим название
        TextView article = (TextView) findViewById(R.id.article);
        article.setText("Выберите факультет");

        //подключение к БД
        SQLiteDatabase db = getBaseContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null);

        //селект предметов из БД
        Cursor query = db.rawQuery("SELECT * FROM FACULTY;", null);

        //id и название факультета
        int Column1 = query.getColumnIndex("ID_FACULTY");
        int Column2 = query.getColumnIndex("NAME_FACULTY");

        //структура для записи факультетов из БД
        ArrayList<String> sData = new ArrayList<String>();

        //счетчик цикла для элементво масива
        int i = 0;
        // объявление массива с предметами
        String[] facultyArray = new String[4];

        while (query.moveToNext()) {
            String ID = query.getString(Column1);
            String NAME = query.getString(Column2);
            sData.add(ID + " " + NAME);
            facultyArray[i] = ID;
            i++;
        }

        // получаем элемент ListView для расписания
        ListView facultyListView = (ListView) findViewById(R.id.facultyList);
        //заполняем ListView элементами из БД
        facultyListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, sData));

        // добавляем для списка слушатель
        facultyListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                // по позиции получаем выбранный элемент
                String selectedItem = facultyArray[position];
                String ID = selectedItem;
                article.setText("Выберите факультет " + ID);

                //переход к списку специальностей
                SearchS(ID);
            }
        });

    }

    //поиск специальности в БД
    public void SearchS(String Id) {
        //выводим название
        TextView article = (TextView) findViewById(R.id.article);
        article.setText("Выберите специальность");

        //подключение к БД
        SQLiteDatabase db = getBaseContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null);

        //селект предметов из БД
        Cursor query = db.rawQuery("SELECT * FROM SPECIALITY WHERE ID_F = " + Id + ";", null);

        //id и название предмета
        int Column1 = query.getColumnIndex("ID_SPECIALITY");
        int Column2 = query.getColumnIndex("NAME_SPECIALITY");

        //структура для записи предметов из БД
        ArrayList<String> sData = new ArrayList<String>();

        //счетчик цикла для элементво масива
        int i = 0;
        // объявление массива со специальностями
        String[] specArray = new String[4];

        while (query.moveToNext()) {
            String ID = query.getString(Column1);
            String NAME = query.getString(Column2);
            sData.add(ID + " " + NAME);
            specArray[i] = ID;
            i++;
        }

        // получаем элемент ListView для расписания
        ListView facultyListView = (ListView) findViewById(R.id.facultyList);
        //заполняем ListView элементами из БД
        facultyListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, sData));

        // добавляем для списка слушатель
        facultyListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                // по позиции получаем выбранный элемент
                String selectedItem = specArray[position];
                String ID = selectedItem;
                article.setText("Выберите специальность " + ID);

                //переход к списку студентов
                SearchSt(ID);
            }
        });

    }

    //поиск студента в БД
    public void SearchSt(String Id) {
        //выводим название
        TextView article = (TextView) findViewById(R.id.article);
        article.setText("Кто вы?");

        //подключение к БД
        SQLiteDatabase db = getBaseContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null);

        //селект предметов из БД
        Cursor query = db.rawQuery("SELECT * FROM STUDENTS WHERE ID_SPEC = " + Id + ";", null);

        //id и название предмета
        int Column1 = query.getColumnIndex("ID_STUDENT");
        int Column2 = query.getColumnIndex("NAME_STUDENT");

        //структура для записи предметов из БД
        ArrayList<String> sData = new ArrayList<String>();

        //счетчик цикла для элементво масива
        int i = 0;
        // объявление массива со студентами
        String[] studArray = new String[10];
        // объявление массива с ID студентов
        String[] studNameArray = new String[10];

        while (query.moveToNext()) {
            String ID = query.getString(Column1);
            String NAME = query.getString(Column2);
            sData.add(ID + " " + NAME);
            studArray[i] = ID;
            studNameArray[i] = NAME;
            i++;
        }

        // получаем элемент ListView для расписания
        ListView facultyListView = (ListView) findViewById(R.id.facultyList);
        //заполняем ListView элементами из БД
        facultyListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, sData));

        // добавляем для списка слушатель
        facultyListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                // по позиции получаем выбранный элемент
                String selectedItem = studArray[position];
                String ID = selectedItem;

                // по позиции получаем выбранный элемент
                String selectedItemN = studNameArray[position];
                String NAME = selectedItemN;

                article.setText("Выберите себя " + ID);

                //вход в кабинет
                InitSt(ID, NAME);
            }
        });

    }


    //инициализация студента в БД
    public void InitSt(String Id, String Name) {
        //выводим название
        TextView article = (TextView) findViewById(R.id.article);
        article.setText("Вы зашли под логином - " + Name);



        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, MainActivity.class);

        //сериализация объекта
        //создаем юзера
        users = new ArrayList<>();

        addUser(this.findViewById(0), Name, Id);
        save(this.findViewById(0));


        Bundle b = new Bundle();
        intent.putExtras(b);
        startActivity(intent);
    }

    public void addUser (View view, String Name, String Id) {
        String name = Name;
        int  age = Integer.parseInt (Id);
        User user = new User(name, age);
        users.add(user);
    }

    public void save (View view) {

        boolean result = JSONHelper.exportToJSON(this, users);
        if (result) {
            Toast.makeText(this, "Авторизация прошла успешно", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Ошибка авторизации", Toast.LENGTH_LONG).show();
        }
    }
}