package com.example.oop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class StartMain extends AppCompatActivity {

    //факультет
    String facultyID, tabID; Bundle b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_main);


        TextView toolbar = (TextView) findViewById(R.id.toolbar);

        Bundle bundle = getIntent().getExtras();
        facultyID = bundle.getString("classID");



        TextView dateTextView = findViewById(R.id.dateTextView);
        DatePicker datePicker = this.findViewById(R.id.datePicker);

        // Месяц начиная с нуля. Для отображения добавляем 1.
        datePicker.init(2021, 12, 01, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {


                SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
                Date date = new Date( view.getYear(), view.getMonth(), view.getDayOfMonth() - 1);
                String dayOfWeek = simpledateformat.format(date);

                String day = Integer.toString(view.getDayOfMonth());
                Intent intent = new Intent();
                intent.setClass(StartMain.this, Schedule.class);

                Bundle b = new Bundle();
                b.putString("day", dayOfWeek);
                b.putString("date", view.getDayOfMonth() + "/" +
                        (view.getMonth() + 1) + "/" + view.getYear());
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
                intent.setClass(StartMain.this, LoginActivity.class);
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