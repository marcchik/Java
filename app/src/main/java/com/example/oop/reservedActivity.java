package com.example.oop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
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

import java.util.ArrayList;
import java.util.List;

public class reservedActivity extends AppCompatActivity {

    //массив с юзерами
    private List<User> users;

    //ID авторизованного студента
    int ID;

    //ID специальности авторизованного студента
    int idSpec;

    //выбранная дата
    String date;

    //выбранный предмет
    String pair;

    //выбранный предмет ID
    int pairID;

    //номер очереди
    int Number;

    //количество сданных лаб
    int quanity;

    //кнопка "Уступить место"
    Button btnIeldPlace;

    int flag = 0;

    //id выбранного свободного студента
    int freedomID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserved);

        TextView pairSelected = (TextView) findViewById(R.id.pairSelected);

        Bundle bundle = getIntent().getExtras();
        pair = bundle.getString("subjectID");

        date = bundle.getString("date");
        pairSelected.setText(pair);

        ///подключение к БД
        SQLiteDatabase db = getBaseContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null);

        //получаем ID студента из JSONE (студент, который авторизовался)
        ID = openId();

        //получаем id выбранного предмета
        Cursor queryIdSubject = db.rawQuery("SELECT ID_SUBJECT FROM SUBJECT WHERE NAME_SUBJECT = '" + pair + "';", null);

        //id выбранного предмета
        int pairIdCursor = queryIdSubject.getColumnIndex("ID_SUBJECT");


        while (queryIdSubject.moveToNext()) {
            pairID = queryIdSubject.getInt(pairIdCursor);
        }



        //получаем id группы авторизованного студента
        Cursor queryIdSpec = db.rawQuery("SELECT ID_SPEC FROM STUDENTS WHERE ID_STUDENT = " + ID + ";", null);

        //id группы строка
        int idSpecCursor = queryIdSpec.getColumnIndex("ID_SPEC");

        //id группы
        idSpec = -1;


        while (queryIdSpec.moveToNext()) {
            idSpec = queryIdSpec.getInt(idSpecCursor);
        }


        //селект студентов группы из БД
        Cursor query = db.rawQuery("SELECT ID_STUDENT,ID_SPEC, NAME_STUDENT FROM STUDENTS WHERE ID_SPEC = " + idSpec + " EXCEPT SELECT ID_ST, ID_SPEC, NAME_STUDENT  FROM ORDERLAB, STUDENTS WHERE ID_ST = ID_STUDENT and ID_SPEC = " + idSpec + " and ID_SUB = " + pairID + " and DATE_LAB = '" + date + "';", null);

        //id и название предмета
//        int Column1 = query.getColumnIndex("ID_STUDENT");
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
//            String ID = query.getString(Column1);
            String NAME = query.getString(Column2);
            sData.add(NAME.substring(0, NAME.indexOf(' ')));
//            studArray[i] = ID;
            studNameArray[i] = NAME;
            i++;
        }

        // получаем элемент ListView для студентов
        ListView studentListFree = (ListView) findViewById(R.id.studentListFree);
        //заполняем ListView элементами из БД
        studentListFree.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sData));


        //селект очереди из БД
        Cursor queryOrder = db.rawQuery("SELECT * FROM ORDERLAB, STUDENTS WHERE ID_ST = ID_STUDENT and ID_SPEC =  " + idSpec + " and DATE_LAB = '" + date + "' and ID_SUB = " + pairID + ";", null);

        //id и название предмета
        int Column1Order = queryOrder.getColumnIndex("NUMBERORDER");
        int Column2Order = queryOrder.getColumnIndex("NAME_STUDENT");

        //структура для записи предметов из БД
        ArrayList<String> sDataOrder = new ArrayList<String>();

        //счетчик цикла для элементво масива
        int iOrder = 0;
        // объявление массива со студентами
        String[] studArrayOrder = new String[10];
        // объявление массива с ID студентов
        String[] studNameArrayOrder = new String[10];

        while (queryOrder.moveToNext()) {
            String id = queryOrder.getString(Column1Order);
            String NAME = queryOrder.getString(Column2Order);
            sDataOrder.add(NAME.substring(0, NAME.indexOf(' ')));
            studArrayOrder[iOrder] = id;
            if (Integer.parseInt(id) == ID) {

                pairSelected.setText(id);
                btnIeldPlace = (Button)findViewById(R.id.yieldPlace);
                btnIeldPlace.setEnabled(false);
            }
            studNameArrayOrder[iOrder] = NAME;
            iOrder++;
        }

        // получаем элемент ListView для расписания
        ListView studentListOrder = (ListView) findViewById(R.id.studentListOrder);
        //заполняем ListView элементами из БД
        studentListOrder.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sDataOrder));


    }


    //занять место на паре
    public void topPlace(View view) {

        //подключение к БД
        SQLiteDatabase db = getBaseContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null);

        //ПРОГРЕСС В СДАЧЕ ЛАБ
        Cursor completedLab = db.rawQuery("SELECT * FROM COMPLETED WHERE ID_SUB =  " + pairID + " and ID_STUD = " + ID + " ;", null);

        int QUANITY = completedLab.getColumnIndex("QUANITY");

//        TextView pairSelected = (TextView) findViewById(R.id.pairSelected);
//        pairSelected.setText(String.valueOf(quanity));



        //счетчик цикла для элементво масива
//        int i2 = 0;
//        // объявление массива со студентами
//        String[] studArray2 = new String[10];
//        // объявление массива с ID студентов
//        String[] studNameArray2 = new String[10];
//
//        while (completedLab.moveToNext()) {
//            String id_stud = completedLab.getString(ID_STUD);
//            String quanity = completedLab.getString(QUANITY);
//            String id_sub = completedLab.getString(ID_SUB);
//
//            studNameArray2[i2] = NAME;
//            i2++;
//        }

        ////
        //количество мест занятых в очереди
        Cursor amountStudentOrder = db.rawQuery("SELECT COUNT(*) [NUMBER] FROM ORDERLAB WHERE ID_SSPEC =  " + idSpec + " ;", null);

        //id выбранного предмета
        amountStudentOrder.moveToFirst();
        Number = amountStudentOrder.getInt(0);
        amountStudentOrder.close();

        //вставка в очередь на лабу
        db.execSQL("INSERT INTO ORDERLAB(ID_ST, ID_SUB, NUMBERORDER, DATE_LAB, ID_SSPEC) SELECT " + ID + ", " + pairID + ", " + Number + 1 + ", '" + date + "', " + idSpec + " WHERE NOT EXISTS(SELECT 1 FROM ORDERLAB WHERE ID_ST = " + ID + " and ID_SSPEC = " + idSpec + " and DATE_LAB = '" + date + "');");




        //селект студентов из БД
        Cursor query = db.rawQuery("SELECT * FROM ORDERLAB, STUDENTS WHERE ID_ST = ID_STUDENT and ID_SPEC =  " + idSpec + " and DATE_LAB = '" + date + "' and ID_SUB = " + pairID + ";", null);


        //id и название предмета
        int Column1 = query.getColumnIndex("NUMBERORDER");
        int Column2 = query.getColumnIndex("NAME_STUDENT");

        //структура для записи предметов из БД
        ArrayList<String> sData = new ArrayList<String>();

        //счетчик цикла для элементво масива
        int i = 0;
        // объявление массива со студентами
        String[] studArray = new String[15];
        // объявление массива с ID студентов
        String[] studNameArray = new String[15];

        while (query.moveToNext()) {
            String ID = query.getString(Column1);
            String NAME = query.getString(Column2);
            sData.add(NAME.substring(0, NAME.indexOf(' ')));
            studArray[i] = ID;
            studNameArray[i] = NAME;
            i++;
        }

        // получаем элемент ListView для расписания
        ListView facultyListView = (ListView) findViewById(R.id.studentListOrder);
        //заполняем ListView элементами из БД
        facultyListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sData));

        //пересчет свободных студентов
        //селект студентов группы из БД
        Cursor queryS = db.rawQuery("SELECT ID_STUDENT,ID_SPEC, NAME_STUDENT FROM STUDENTS WHERE ID_SPEC = " + idSpec + " EXCEPT SELECT ID_ST, ID_SPEC, NAME_STUDENT  FROM ORDERLAB, STUDENTS WHERE ID_ST = ID_STUDENT and ID_SPEC = " + idSpec + " and ID_SUB = " + pairID + " and DATE_LAB = '" + date + "';", null);

        //id и название предмета
        int Column2S = queryS.getColumnIndex("NAME_STUDENT");

        //структура для записи предметов из БД
        ArrayList<String> sDataS = new ArrayList<String>();

        //счетчик цикла для элементво масива
        int iS = 0;
        // объявление массива с ID студентов
        String[] studNameArrayS = new String[10];

        while (queryS.moveToNext()) {
            String NAMES = queryS.getString(Column2S);
            sDataS.add(NAMES.substring(0, NAMES.indexOf(' ')));
            studNameArrayS[iS] = NAMES;
            iS++;
        }

        // получаем элемент ListView для студентов
        ListView studentListFreeS = (ListView) findViewById(R.id.studentListFree);
        //заполняем ListView элементами из БД
        studentListFreeS.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sDataS));

    }

    //устпуить очередь
    public void yieldPlace(View view) {

        Toast.makeText(this, "Выберите свободного студента", Toast.LENGTH_LONG).show();


        //подключение к БД
        SQLiteDatabase db = getBaseContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null);

        //пересчет свободных студентов
        //селект студентов группы из БД
        Cursor queryS = db.rawQuery("SELECT ID_STUDENT,ID_SPEC, NAME_STUDENT FROM STUDENTS WHERE ID_SPEC = " + idSpec + " EXCEPT SELECT ID_ST, ID_SPEC, NAME_STUDENT  FROM ORDERLAB, STUDENTS WHERE ID_ST = ID_STUDENT and ID_SPEC = " + idSpec + " and ID_SUB = " + pairID + " and DATE_LAB = '" + date + "';", null);

        //id и название студента
        int Column2S = queryS.getColumnIndex("NAME_STUDENT");
        int Column1S = queryS.getColumnIndex("ID_STUDENT");

        //структура для записи предметов из БД
        ArrayList<String> sDataS = new ArrayList<String>();

        //счетчик цикла для элементво масива
        int iS = 0;
        // объявление массива с ID студентов
        String[] studIdArrayS = new String[15];

        while (queryS.moveToNext()) {
            String NAMES = queryS.getString(Column2S);
            String IDS = queryS.getString(Column1S);
            sDataS.add(NAMES.substring(0, NAMES.indexOf(' ')));
            studIdArrayS[iS] = IDS;
            iS++;
        }

        // получаем элемент ListView для студентов
        ListView studentListFreeS = (ListView) findViewById(R.id.studentListFree);
        //заполняем ListView элементами из БД
        studentListFreeS.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, sDataS));



        // добавляем для списка слушатель
        studentListFreeS.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // по позиции получаем выбранный элемент
                String selectedItem = studIdArrayS[position];
                flag = 1;
                freedomID = Integer.parseInt(selectedItem) ;
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Подтвердите ваше действие!\nНажмите на кнопку 'Уступить место' еще раз!", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        //пересчет свободных студентов при уступке места
        if (flag == 1) {

            //удаление из очередь на лабу
            db.execSQL("DELETE FROM ORDERLAB WHERE ID_ST = " + ID + " and ID_SUB = " + pairID + " and DATE_LAB = '" + date + "';");


            //пересчет списков после изменений
            //вставка в очередь на лабу
            db.execSQL("INSERT INTO ORDERLAB (ID_ST, ID_SUB, ID_DAYY, NUMBERORDER, DATE_LAB, ID_SSPEC) VALUES (" + freedomID + ", " + pairID + ", 4, 4, '" + date + "', " + idSpec + ");");

            //селект студентов группы из БД
            Cursor query = db.rawQuery("SELECT ID_STUDENT,ID_SPEC, NAME_STUDENT FROM STUDENTS WHERE ID_SPEC = " + idSpec + " EXCEPT SELECT ID_ST, ID_SPEC, NAME_STUDENT  FROM ORDERLAB, STUDENTS WHERE ID_ST = ID_STUDENT and ID_SPEC = " + idSpec + " and ID_SUB = " + pairID + " and DATE_LAB = '" + date + "';", null);

            //id и название предмета
//        int Column1 = query.getColumnIndex("ID_STUDENT");
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
//            String ID = query.getString(Column1);
                String NAME = query.getString(Column2);
                sData.add(NAME.substring(0, NAME.indexOf(' ')));
//            studArray[i] = ID;
                studNameArray[i] = NAME;
                i++;
            }

            // получаем элемент ListView для студентов
            ListView studentListFree = (ListView) findViewById(R.id.studentListFree);
            //заполняем ListView элементами из БД
            studentListFree.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sData));


            //селект очереди из БД
            Cursor queryOrder = db.rawQuery("SELECT * FROM ORDERLAB, STUDENTS WHERE ID_ST = ID_STUDENT and ID_SPEC =  " + idSpec + " and DATE_LAB = '" + date + "' and ID_SUB = " + pairID + ";", null);

            //id и название предмета
            int Column1Order = queryOrder.getColumnIndex("NUMBERORDER");
            int Column2Order = queryOrder.getColumnIndex("NAME_STUDENT");

            //структура для записи предметов из БД
            ArrayList<String> sDataOrder = new ArrayList<String>();

            //счетчик цикла для элементво масива
            int iOrder = 0;
            // объявление массива со студентами
            String[] studArrayOrder = new String[10];
            // объявление массива с ID студентов
            String[] studNameArrayOrder = new String[10];

            while (queryOrder.moveToNext()) {
                String id = queryOrder.getString(Column1Order);
                String NAME = queryOrder.getString(Column2Order);
                sDataOrder.add(NAME.substring(0, NAME.indexOf(' ')));
                studArrayOrder[iOrder] = id;
                if (Integer.parseInt(id) == ID) {

                    //pairSelected.setText(id);
                    btnIeldPlace = (Button)findViewById(R.id.yieldPlace);
                    btnIeldPlace.setEnabled(false);
                }
                studNameArrayOrder[iOrder] = NAME;
                iOrder++;
            }

            // получаем элемент ListView для расписания
            ListView studentListOrder = (ListView) findViewById(R.id.studentListOrder);
            //заполняем ListView элементами из БД
            studentListOrder.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sDataOrder));


        }


    }


    //покинуть очередь
    public void leaveOrder(View view) {

        //подключение к БД
        SQLiteDatabase db = getBaseContext().openOrCreateDatabase("app.db", MODE_PRIVATE, null);

        //вставка в очередь на лабу
        db.execSQL("DELETE FROM ORDERLAB WHERE ID_ST = " + ID + " and ID_SUB = " + pairID + " and DATE_LAB = '" + date + "';");


        //селект очереди из БД
        Cursor queryOrder = db.rawQuery("SELECT * FROM ORDERLAB, STUDENTS WHERE ID_ST = ID_STUDENT and ID_SPEC =  " + idSpec + " and DATE_LAB = '" + date + "' and ID_SUB = " + pairID + ";", null);

        //id и название предмета
        int Column1Order = queryOrder.getColumnIndex("NUMBERORDER");
        int Column2Order = queryOrder.getColumnIndex("NAME_STUDENT");

        //структура для записи предметов из БД
        ArrayList<String> sDataOrder = new ArrayList<String>();

        //счетчик цикла для элементво масива
        int iOrder = 0;
        // объявление массива со студентами
        String[] studArrayOrder = new String[10];
        // объявление массива с ID студентов
        String[] studNameArrayOrder = new String[10];

        while (queryOrder.moveToNext()) {
            String ID = queryOrder.getString(Column1Order);
            String NAME = queryOrder.getString(Column2Order);
            sDataOrder.add(NAME.substring(0, NAME.indexOf(' ')));
            studArrayOrder[iOrder] = ID;
            studNameArrayOrder[iOrder] = NAME;
            iOrder++;
        }

        // получаем элемент ListView для расписания
        ListView studentListOrder = (ListView) findViewById(R.id.studentListOrder);
        //заполняем ListView элементами из БД
        studentListOrder.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sDataOrder));

        //пересчет свободных студентов
        //селект студентов группы из БД
        Cursor queryS = db.rawQuery("SELECT ID_STUDENT,ID_SPEC, NAME_STUDENT FROM STUDENTS WHERE ID_SPEC = " + idSpec + " EXCEPT SELECT ID_ST, ID_SPEC, NAME_STUDENT  FROM ORDERLAB, STUDENTS WHERE ID_ST = ID_STUDENT and ID_SPEC = " + idSpec + " and ID_SUB = " + pairID + " and DATE_LAB = '" + date + "';", null);

        //id и название предмета
        int Column2S = queryS.getColumnIndex("NAME_STUDENT");

        //структура для записи предметов из БД
        ArrayList<String> sDataS = new ArrayList<String>();

        //счетчик цикла для элементво масива
        int iS = 0;
        // объявление массива с ID студентов
        String[] studNameArrayS = new String[10];

        while (queryS.moveToNext()) {
            String NAMES = queryS.getString(Column2S);
            sDataS.add(NAMES.substring(0, NAMES.indexOf(' ')));
            studNameArrayS[iS] = NAMES;
            iS++;
        }

        // получаем элемент ListView для студентов
        ListView studentListFreeS = (ListView) findViewById(R.id.studentListFree);
        //заполняем ListView элементами из БД
        studentListFreeS.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sDataS));


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
                intent.setClass(reservedActivity.this, LoginActivity.class);
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
}