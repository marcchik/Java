package com.example.oop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    //объекты кнопок
    Button ButtonStart, ButtonProfile;

    //массив с юзерами
    private List<User> users;



    //погода
    String apikey = "fedc36ed7df3300b6db8c1a6a622740b";
    String city = "London";
    String readyurl = "https://api.openweathermap.org/data/2.5/weather?q="
            +city+"&appid="+apikey+"&units=metric&lang=ru";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //переброс пользователя при первом входе
        checkFirstStart();


        //вызов обработки погоды
        try {
            new GetURLData().execute(readyurl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //кнопка "Посмотреть распиание"
        ButtonStart = (Button)findViewById(R.id.startBtn);

        //кнопка "Заполнить профиль"
        ButtonProfile = (Button)findViewById(R.id.userBtn);

        //обработчик события по клику на кнопку "Посмотреть расписание" с переходом на новый экран
        ButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //создание нового экрана
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, StartMain.class);

                Bundle b = new Bundle();
                b.putString("classID", "11");
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        //обработчик события по клику на кнопку "Заполнить профиль" с переходом на новый экран
        ButtonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //создание нового экрана
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, UsreActivity.class);

                Bundle b = new Bundle();
                b.putString("classID", "11");
                intent.putExtras(b);
                startActivity(intent);
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
                intent.setClass(MainActivity.this, LoginActivity.class);
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

    //первый вход пользователя или нет?
    private void checkFirstStart() {

        SharedPreferences sp = getSharedPreferences("hasVisited",
                Context.MODE_PRIVATE);
        // проверяем, первый ли раз открывается программа (Если вход первый то вернет false)
        boolean hasVisited = sp.getBoolean("hasVisited", false);

        if (!hasVisited) {
            // Сработает если Вход первый

            //Ставим метку что вход уже был
            SharedPreferences.Editor e = sp.edit();
            e.putBoolean("hasVisited", true);
            e.commit(); //После этого hasVisited будет уже true и будет означать, что вход уже был

            //Ниже запускаем активность которая нужна при первом входе
            //создание нового экрана
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, LoginActivity.class);
            startActivity(intent);

        } else {
            //Сработает если вход в приложение уже был

        }
    }

    public void Start(View view) {
        //подключение к БД
        SQLiteDatabase db = getBaseContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null);

        //селект факультетов из БД
        Cursor query = db.rawQuery("SELECT * FROM FACULTY;", null);
    }




    private class GetURLData extends AsyncTask<String, String, String> {

        TextView weatherinfo = findViewById(R.id.WI);

        protected void onPreExecute(){
            super.onPreExecute();
            //вьюшка для результата
            weatherinfo.setText("Загружаю данные о погоде");
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line=reader.readLine()) != null)
                    buffer.append(line).append("\n");

                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(connection != null)
                    connection.disconnect();

                try {
                    if (reader != null) {
                        reader.close();
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                weatherinfo.setText(jsonObject.getString("name") + " " + jsonObject.getJSONObject("main").getDouble("temp")+"\n" + jsonObject.getJSONArray("weather").getJSONObject(0).getString("description"));
            } catch(Exception e) {

            }
        }
    }
}