package com.una.takeurpills;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static android.R.attr.id;
import static android.R.attr.name;
import static android.R.attr.value;
import static com.una.takeurpills.R.drawable.dosis;
import static com.una.takeurpills.R.id.lunes;
import static com.una.takeurpills.R.layout.dialog;
import static com.una.takeurpills.R.string.reminder;

public class AddPillActivity extends ParentClass implements
        View.OnClickListener {

    Button button;
    private int mHour, mMinute;
    JSONObject jobject;
    ArrayList<String> horas = new ArrayList<String>();
    ArrayList<String> horasEditar = new ArrayList<String>();
    int i;
    int comparisonHour = 0;
    int comparisonMinute = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pill);
        jobject = new JSONObject();
        CargarSpinner();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.pill_logo);
        getSupportActionBar().setTitle(R.string.title_save);
        getData();
        OnclickDelButton(R.id.btAddPillsCancelar);
        OnclickDelButton(R.id.btAddPillsSave);
    }

    public void OnclickDelButton(int ref) {
        View view = findViewById(ref);
        Button miButton = (Button) view;


        miButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {

                    case R.id.btAddPillsSave:
                        Button save = (Button) findViewById(R.id.btAddPillsSave);
                        String text = save.getText().toString();
                        String value = getResources().getString(R.string.bt_save);
                        if (text.equals(value)) {
                            Guardar();
                        } else {
                            Editar();
                            Intent intento = new Intent(getApplicationContext(), HomeActivity.class);
                            startActivity(intento);
                        }

                        break;

                    case R.id.btAddPillsCancelar:
                        AlertBuilder(v);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void Add() {
        EditText tituloPastilla = (EditText) findViewById(R.id.et_addPill_titulo);
        final String tituloPastilla1 = tituloPastilla.getText().toString();
        EditText dosis = (EditText) findViewById(R.id.et_addPill_dosis);
        String dosis1 = dosis.getText().toString();
        int dosis2 = Integer.parseInt(dosis1);
        RadioButton mililitros = (RadioButton) findViewById(R.id.mililitros);

        RadioButton unidades = (RadioButton) findViewById(R.id.unidades);


        Spinner spinner = (Spinner) findViewById(R.id.VecesDiarias);

        CheckBox lunes = (CheckBox) findViewById(R.id.lunes);
        CheckBox martes = (CheckBox) findViewById(R.id.martes);
        CheckBox miercoles = (CheckBox) findViewById(R.id.miercoles);
        CheckBox jueves = (CheckBox) findViewById(R.id.jueves);
        CheckBox viernes = (CheckBox) findViewById(R.id.viernes);
        CheckBox sabado = (CheckBox) findViewById(R.id.sabado);
        CheckBox domingo = (CheckBox) findViewById(R.id.domingo);
        EditText cantidadRestante = (EditText) findViewById(R.id.et_addPill_cantidadRestante);
        String cantidadRestante1 = cantidadRestante.getText().toString();
        int cantidadRestante2 = Integer.parseInt(cantidadRestante1);
        EditText reminder = (EditText) findViewById(R.id.et_addPill_reminder);
        String reminder1 = reminder.getText().toString();
        int reminder2 = Integer.parseInt(reminder1);
        String vecesDiarias = spinner.getSelectedItem().toString();
        int vecesDiarias2 = Integer.parseInt(vecesDiarias);
        try {
            for (int a = 0; a < vecesDiarias2; a++) {
                Button aux = (Button) findViewById(a);
                horas.add(aux.getText().toString());
                if (jobject == null) jobject = new JSONObject();
                jobject.put("hora" + String.valueOf(a), aux.getText().toString());
            }
        }
        catch (JSONException e) {
            Log.e("Exception", "Unable to create JSONArray: " + e.toString());
        }
        String unidad = "";
        if (mililitros.isChecked())
            unidad = "Mililitros";
        if (unidades.isChecked())
            unidad = "Unidades";
        Boolean monday = false;
        Boolean tuesday = false;
        Boolean wednesday = false;
        Boolean thursday = false;
        Boolean friday = false;
        Boolean saturday = false;
        Boolean sunday = false;

        if (lunes.isChecked()) {
            monday = true;
        }
        if (martes.isChecked()) {
            tuesday = true;
        }
        if (miercoles.isChecked()) {
            wednesday = true;
        }
        if (jueves.isChecked()) {
            thursday = true;
        }
        if (viernes.isChecked()) {
            friday = true;
        }
        if (sabado.isChecked()) {
            saturday = true;
        }
        if (domingo.isChecked()) {
            sunday = true;
        }
        try {
            /*if(persistence == false){
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                persistence = true;
            }*/
            database = FirebaseDatabase.getInstance();
            myRef = database.getReference("Treatments");
            FirebaseUser currentUser = mAuth.getCurrentUser();
            String key = "";
            if(currentUser != null ){
                key = currentUser.getUid();
            }
            Treatment tratamiento = new Treatment(tituloPastilla1, dosis2, cantidadRestante2, vecesDiarias2, unidad, reminder2,
                    monday, tuesday, wednesday, thursday, friday, saturday, sunday, horas);
            myRef.child(key).child(tituloPastilla1).setValue(tratamiento);
            //myRef.child(key).push().setValue(tratamiento);
            if(!treatmentName.contains(tituloPastilla1))
                treatmentName.add(tituloPastilla1);
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Mensaje(databaseError.getMessage());
                    ErrorAdding();
                }
            });
        }
        catch (Exception exc){
            exc.getCause().toString();
        }
        for (int i = 0; i < horas.size(); i++ ) {
            horas.remove(i);
        }

    }

    public void Edit(Treatment treatment){
        try {
            /*if(persistence == false){
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                persistence = true;
            }*/
            database = FirebaseDatabase.getInstance();
            myRef = database.getReference("Treatments");
            FirebaseUser currentUser = mAuth.getCurrentUser();
            String key = "";
            if(currentUser != null ){
                key = currentUser.getUid();
            }
            String titulo = treatment.getTitulo();
            myRef.child(key).child(titulo).setValue(treatment);
            //myRef.child(key).push().setValue(tratamiento);
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Mensaje(databaseError.getMessage());
                    ErrorAdding();
                }
            });
        }
        catch (Exception exc){
            exc.getCause().toString();
        }
    }

    public void Guardar() {
        String mili, uni;
        EditText tituloPastilla = (EditText) findViewById(R.id.et_addPill_titulo);
        String tituloPastilla1 = tituloPastilla.getText().toString();
        EditText dosis = (EditText) findViewById(R.id.et_addPill_dosis);
        String dosis1 = dosis.getText().toString();
        RadioButton mililitros = (RadioButton) findViewById(R.id.mililitros);

        RadioButton unidades = (RadioButton) findViewById(R.id.unidades);
        Spinner spinner = (Spinner) findViewById(R.id.VecesDiarias);

        CheckBox lunes = (CheckBox) findViewById(R.id.lunes);
        CheckBox martes = (CheckBox) findViewById(R.id.martes);
        CheckBox miercoles = (CheckBox) findViewById(R.id.miercoles);
        CheckBox jueves = (CheckBox) findViewById(R.id.jueves);
        CheckBox viernes = (CheckBox) findViewById(R.id.viernes);
        CheckBox sabado = (CheckBox) findViewById(R.id.sabado);
        CheckBox domingo = (CheckBox) findViewById(R.id.domingo);
        EditText cantidadRestante = (EditText) findViewById(R.id.et_addPill_cantidadRestante);
        String cantidadRestante1 = cantidadRestante.getText().toString();
        EditText reminder = (EditText) findViewById(R.id.et_addPill_reminder);
        String reminder1 = reminder.getText().toString();
        String vecesDiarias = spinner.getSelectedItem().toString();
        if (emptyTextFieldsValidator()) {
            DialogEspaciosVacios();

        } else {
            if (tratamienttoExistente(tituloPastilla1)) {
                DialogTratamientoExiste();
            } else {
                try {
                    jobject.put("titulo", tituloPastilla1);
                    jobject.put("dosis", dosis1);
                    jobject.put("cantidadRestante", cantidadRestante1);
                    jobject.put("vecesDiarias", vecesDiarias);
                    if (mililitros.isChecked())
                        jobject.put("Unidad", "Mililitros");
                    if (unidades.isChecked())
                        jobject.put("Unidad", "Unidades");
                    if (lunes.isChecked()) {
                        jobject.put("Dia_1", "lunes");
                    }
                    if (martes.isChecked()) {
                        jobject.put("Dia_2", "martes");
                    }
                    if (miercoles.isChecked()) {
                        jobject.put("Dia_3", "miercoles");
                    }
                    if (jueves.isChecked()) {
                        jobject.put("Dia_4", "jueves");
                    }
                    if (viernes.isChecked()) {
                        jobject.put("Dia_5", "viernes");
                    }
                    if (sabado.isChecked()) {
                        jobject.put("Dia_6", "sabado");
                    }
                    if (domingo.isChecked()) {
                        jobject.put("Dia_7", "doming");
                    }
                    jobject.put("Reminder", reminder1);
                    Add();
                    JSONArray jarray = readFromFile();
                    jarray.put(jobject);
                    writeToFile(jarray.toString());
                    Mensaje(getResources().getString(R.string.save_success));
                    Intent intento = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intento);
                } catch (JSONException e) {
                    Log.e("Exception", "Unable to create JSONArray: " + e.toString());
                }
            }
        }
    }

    private void CargarSpinner() {
        Spinner s1;
        final String[] vecesDiarias = {
                getResources().getString(R.string.daily_times),
                "1",
                "2",
                "3",
                "4",
                "5",
                "6"

        };
        //---Spinner View---
        s1 = (Spinner) findViewById(R.id.VecesDiarias);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, vecesDiarias);
        s1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                LinearLayout ll1 = (LinearLayout) findViewById(R.id.horas_list);
                ll1.removeAllViews();
                for (int i = 0; i < horas.size(); i++ ) {
                    horas.remove(i);
                }
                for (int h = 0; h < position; h++) {
                    Button myButton = new Button(getApplicationContext());
                    myButton.setText(modo == 0 ? getResources().getString(R.string.add_message) : horasEditar.get(h).toString());
                    myButton.setId(h);
                    myButton.setOnClickListener(AddPillActivity.this);
                    LinearLayout ll = (LinearLayout) findViewById(R.id.horas_list);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    ll.addView(myButton, lp);
                }
                modo = 0;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        s1.setAdapter(adapter);
    }// fin de CargarSpinner

    @Override
    public void onClick(View v) {
        i = v.getId();
        button = (Button) findViewById(v.getId());
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        button.setText(hourOfDay + ":" + minute);
                        try {
                            if(comparisonHour == hourOfDay && comparisonMinute == minute) {
                                return;
                            }
                            comparisonHour = hourOfDay;
                            comparisonMinute = minute;

                            c.setTimeInMillis(System.currentTimeMillis());
                            int day1 = c.get(Calendar.DAY_OF_YEAR);
                            c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            c.set(Calendar.MINUTE, minute);
                            c.set(Calendar.SECOND, 0);
                            c.set(Calendar.MILLISECOND, 0);


                            if(c.before(Calendar.getInstance())) {
                                c.set(Calendar.DATE, 1);
                                c.set(Calendar.DAY_OF_YEAR,1);
                            }
                            day1 = c.get(Calendar.DAY_OF_YEAR);
                            long _alarmtrigger = (c.before(Calendar.getInstance()))
                                    ? c.getTimeInMillis() + (AlarmManager.INTERVAL_DAY+1)
                                    : c.getTimeInMillis();
                            /*String text = button.getText().toString();
                            if (jobject == null) jobject = new JSONObject();
                            jobject.put("hora" + String.valueOf(i), text);*/
                            //horas.add(text);
                            Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
                            final int _id = (int) System.currentTimeMillis();
                            final int _idIntent = (int) _alarmtrigger;
                            EditText tituloPastilla = (EditText) findViewById(R.id.et_addPill_titulo);
                            String tituloPastilla1 = tituloPastilla.getText().toString();
                            intent.putExtra("treatment", _id);
                            intent.putExtra("title", tituloPastilla1);

                            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                                    _idIntent, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            AlarmManager am =
                                    (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
                            am.setRepeating(AlarmManager.RTC_WAKEUP,_alarmtrigger,AlarmManager.INTERVAL_DAY,
                                    pendingIntent);
                        }catch (Exception exc) {
                            Log.e("Exception", "Unable to create JSONArray: " + exc.toString());
                        }
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private void Editar() {
        final int len = testjarray.length();
        for (int i = 0; i < len; i++) {
            final JSONObject obj = testjarray.optJSONObject(i);
            try {
                String nombre = String.valueOf(obj.get("titulo"));
                if (String.valueOf(jobject.get("titulo")).toString().equals(nombre)) {
                    EditText tituloPastilla = (EditText) findViewById(R.id.et_addPill_titulo);
                    String tituloPastilla1 = tituloPastilla.getText().toString();

                    EditText dosis = (EditText) findViewById(R.id.et_addPill_dosis);
                    String dosis1 = dosis.getText().toString();
                    RadioButton mililitros = (RadioButton) findViewById(R.id.mililitros);
                    RadioButton unidades = (RadioButton) findViewById(R.id.unidades);
                    Spinner spinner = (Spinner) findViewById(R.id.VecesDiarias);
                    String vecesDiarias = spinner.getSelectedItem().toString();
                    int vecesDiarias2 = Integer.parseInt(vecesDiarias);
                    CheckBox lunes = (CheckBox) findViewById(R.id.lunes);
                    CheckBox martes = (CheckBox) findViewById(R.id.martes);
                    CheckBox miercoles = (CheckBox) findViewById(R.id.miercoles);
                    CheckBox jueves = (CheckBox) findViewById(R.id.jueves);
                    CheckBox viernes = (CheckBox) findViewById(R.id.viernes);
                    CheckBox sabado = (CheckBox) findViewById(R.id.sabado);
                    CheckBox domingo = (CheckBox) findViewById(R.id.domingo);
                    EditText cantidadRestante = (EditText) findViewById(R.id.et_addPill_cantidadRestante);
                    String cantidadRestante1 = cantidadRestante.getText().toString();
                    EditText reminder = (EditText) findViewById(R.id.et_addPill_reminder);
                    String reminder1 = reminder.getText().toString();


                    String unidad = "";
                    Boolean monday = false;
                    Boolean tuesday = false;
                    Boolean wednesday = false;
                    Boolean thursday = false;
                    Boolean friday = false;
                    Boolean saturday = false;
                    Boolean sunday = false;

                    obj.put("titulo", tituloPastilla1);
                    obj.put("dosis", dosis1);
                    obj.put("cantidadRestante", cantidadRestante1);
                    obj.put("vecesDiarias", vecesDiarias);
                    obj.put("Reminder", reminder1);
                    if (mililitros.isChecked()) {
                        unidad = "Mililitros";
                        obj.put("Unidad", "Mililitros");
                    }
                    if (unidades.isChecked()) {
                        unidad = "Unidades";
                        obj.put("Unidad", "Unidades");
                    }
                    if (lunes.isChecked()) {
                        monday = true;
                        obj.put("Dia_1", "lunes");
                    } else {
                        obj.remove("Dia_1");
                    }
                    if (martes.isChecked()) {
                        tuesday = true;
                        obj.put("Dia_2", "martes");
                    } else {
                        obj.remove("Dia_2");
                    }
                    if (miercoles.isChecked()) {
                        wednesday = true;
                        obj.put("Dia_3", "miercoles");
                    } else {
                        obj.remove("Dia_3");
                    }
                    if (jueves.isChecked()) {
                        thursday = true;
                        obj.put("Dia_4", "jueves");
                    } else {
                        obj.remove("Dia_4");
                    }
                    if (viernes.isChecked()) {
                        friday = true;
                        obj.put("Dia_5", "viernes");
                    } else {
                        obj.remove("Dia_5");
                    }
                    if (sabado.isChecked()) {
                        saturday = true;
                        obj.put("Dia_6", "sabado");
                    } else {
                        obj.remove("Dia_6");
                    }
                    if (domingo.isChecked()) {
                        sunday = true;
                        obj.put("Dia_7", "doming");
                    } else {
                        obj.remove("Dia_7");
                    }

                    //Para agregar hora editada
                    /*for (int y = 0; y < vecesDiarias2; y++) {
                        String horas = String.valueOf(obj.get("hora" + i));
                        obj.put("hora" + String.valueOf(y), horas);

                    }*/
                    for (int a = 0; a < vecesDiarias2; a++) {
                        Button aux = (Button) findViewById(a);
                        horas.add(aux.getText().toString());
                        //if (jobject == null) jobject = new JSONObject();
                        obj.put("hora" + String.valueOf(a), aux.getText().toString());
                    }
                    Treatment tratamiento = new Treatment(tituloPastilla1, Integer.parseInt(dosis1), Integer.parseInt(cantidadRestante1)
                            , Integer.parseInt(vecesDiarias), unidad, Integer.parseInt(reminder1),
                            monday, tuesday, wednesday, thursday, friday, saturday, sunday, horas);
                    Edit(tratamiento);
                    for (int x = 0; x < horas.size(); x++ ) {
                        horas.remove(x);
                    }
                    writeToFile(testjarray.toString());
                    Mensaje(getResources().getString(R.string.edit_success));
                }
            } catch (JSONException exc) {
            }
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    private void writeToFile(String data) {
        try {
            if (isExternalStorageWritable()) {
                File tarjeta = Environment.getExternalStorageDirectory();
                File file = new File(tarjeta.getAbsolutePath(), "dataExter.txt");
                OutputStreamWriter osw = new OutputStreamWriter(
                        new FileOutputStream(file));
                osw.write(data);
                osw.close();
            }

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("data.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private JSONArray readFromFile() throws JSONException {

        String ret = "";
        JSONArray jarray;

        try {
            InputStream inputStream = getApplicationContext().openFileInput("data.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        if (ret == null || ret.isEmpty()) {
            jarray = new JSONArray();
        } else {
            jarray = new JSONArray(ret);
        }
        return jarray;
    }

    public void AlertBuilder(View view) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
        builder1.setMessage(R.string.cancel_message);
        builder1.setCancelable(true);
        builder1.setPositiveButton(R.string.yes_message,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intento = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(intento);
                    }
                });
        builder1.setNegativeButton(R.string.no_message,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    ;

    public void getData() {
        Intent callingIntent = getIntent();
        int edicion = callingIntent.getIntExtra("edicion", 0);
        int posicion = callingIntent.getIntExtra("posicion", 0);
        Button save = (Button) findViewById(R.id.btAddPillsSave);
        TextView title = (TextView) findViewById(R.id.tv_addPill_title);
        if (modo == 1) {
            save.setText(R.string.bt_update);
            title.setText(R.string.tv_editPill_header);
            getSupportActionBar().setTitle(R.string.title_update);
            try {
                JSONObject objjson = testjarray.optJSONObject(posicion);
                jobject = objjson;
                String titulo = String.valueOf(objjson.get("titulo"));
                int dosis = Integer.parseInt(String.valueOf(objjson.get("dosis")));
                String unidad = String.valueOf(objjson.get("Unidad"));
                int cantidadRestante = Integer.parseInt(String.valueOf(objjson.get("cantidadRestante")));
                int reminder = Integer.parseInt(String.valueOf(objjson.get("Reminder")));
                int vecesDiarias = Integer.parseInt(String.valueOf(objjson.get("vecesDiarias")));
                String lunes = objjson.has("Dia_1")
                        ? String.valueOf(objjson.get("Dia_1"))
                        : "";
                String martes = objjson.has("Dia_2")
                        ? String.valueOf(objjson.get("Dia_2"))
                        : "";
                String miercoles = objjson.has("Dia_3")
                        ? String.valueOf(objjson.get("Dia_3"))
                        : "";
                String jueves = objjson.has("Dia_4")
                        ? String.valueOf(objjson.get("Dia_4"))
                        : "";
                String viernes = objjson.has("Dia_5")
                        ? String.valueOf(objjson.get("Dia_5"))
                        : "";
                String sabado = objjson.has("Dia_6")
                        ? String.valueOf(objjson.get("Dia_6"))
                        : "";
                String domingo = objjson.has("Dia_7")
                        ? String.valueOf(objjson.get("Dia_7"))
                        : "";

                TextView Mi_textview = (TextView) findViewById(R.id.et_addPill_titulo);
                Mi_textview.setEnabled(false);
                TextView Mi_textview2 = (TextView) findViewById(R.id.et_addPill_dosis);
                TextView Mi_textview3 = (TextView) findViewById(R.id.et_addPill_cantidadRestante);
                TextView Mi_textview4 = (TextView) findViewById(R.id.et_addPill_reminder);
                Spinner spinner = (Spinner) findViewById(R.id.VecesDiarias);

                RadioButton Mi_radiobutton = (RadioButton) findViewById((unidad.equals("Mililitros")
                        ? R.id.mililitros : R.id.unidades));

                CheckBox checkBox = (CheckBox) findViewById(R.id.lunes);
                CheckBox checkBox2 = (CheckBox) findViewById(R.id.martes);
                CheckBox checkBox3 = (CheckBox) findViewById(R.id.miercoles);
                CheckBox checkBox4 = (CheckBox) findViewById(R.id.jueves);
                CheckBox checkBox5 = (CheckBox) findViewById(R.id.viernes);
                CheckBox checkBox6 = (CheckBox) findViewById(R.id.sabado);
                CheckBox checkBox7 = (CheckBox) findViewById(R.id.domingo);

                for(int x = 0; x< horasEditar.size(); x ++)
                    horasEditar.remove(x);

                for (int y = 0; y < vecesDiarias; y++) {
                    String horas = String.valueOf(jobject.get("hora" + y));
                    horasEditar.add(horas);
                }

                Mi_textview.setText(titulo);
                if (dosis != 0)
                    Mi_textview2.setText(String.valueOf(dosis));
                if (cantidadRestante != 0)
                    Mi_textview3.setText(String.valueOf(cantidadRestante));
                if (reminder != 0)
                    Mi_textview4.setText(String.valueOf(reminder));
                if (vecesDiarias != 0)
                    spinner.setSelection(vecesDiarias);

                Mi_radiobutton.setChecked(true);
                if (lunes.contains("lunes"))
                    checkBox.setChecked(true);
                if (martes.contains("martes"))
                    checkBox2.setChecked(true);
                if (miercoles.contains("miercoles"))
                    checkBox3.setChecked(true);
                if (jueves.contains("jueves"))
                    checkBox4.setChecked(true);
                if (viernes.contains("viernes"))
                    checkBox5.setChecked(true);
                if (sabado.contains("sabado"))
                    checkBox6.setChecked(true);
                if (domingo.contains("doming"))
                    checkBox7.setChecked(true);
            } catch (JSONException exc) {
                Log.e("login activity", "Can not read file: " + exc.toString());
            }
            catch (Exception e){
                Log.e("login activity", "Can not read file: " + e.toString());
            }
        } else {
            save.setText(R.string.bt_save);
            title.setText(R.string.tv_addPill_header);
        }
    }

    //Metodos para iconos en action Bar, los siguientes dos
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.iconsmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                Intent intento = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intento);
                break;

            case R.id.back:
                this.onBackPressed();
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                break;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
        return true;
    }

    public void Mensaje(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    ;

    public boolean tratamienttoExistente(String treatment) {
        boolean existe = false;
        final int len = testjarray.length();
        for (int i = 0; i < len; i++) {
            final JSONObject obj = testjarray.optJSONObject(i);
            try {
                if (String.valueOf(obj.get("titulo")).toString().equals(treatment)) {
                    existe = true;
                    break;
                }
            } catch (JSONException e) {
            }
        }
        return existe;
    }
    public boolean tratamientoExisteFirebase(String treatment){
        return true;
    }

    public boolean emptyTextFieldsValidator() {
        boolean vacios = false;
        EditText tituloPastilla = (EditText) findViewById(R.id.et_addPill_titulo);
        EditText dosis = (EditText) findViewById(R.id.et_addPill_dosis);
        RadioButton mililitros = (RadioButton) findViewById(R.id.mililitros);
        RadioButton unidades = (RadioButton) findViewById(R.id.unidades);
        Spinner spinner = (Spinner) findViewById(R.id.VecesDiarias);
        CheckBox lunes = (CheckBox) findViewById(R.id.lunes);
        CheckBox martes = (CheckBox) findViewById(R.id.martes);
        CheckBox miercoles = (CheckBox) findViewById(R.id.miercoles);
        CheckBox jueves = (CheckBox) findViewById(R.id.jueves);
        CheckBox viernes = (CheckBox) findViewById(R.id.viernes);
        CheckBox sabado = (CheckBox) findViewById(R.id.sabado);
        CheckBox domingo = (CheckBox) findViewById(R.id.domingo);
        EditText cantidadRestante = (EditText) findViewById(R.id.et_addPill_cantidadRestante);
        EditText reminder = (EditText) findViewById(R.id.et_addPill_reminder);

        if (tituloPastilla.length() == 0) {
            vacios = true;
        }
        if (dosis.length() == 0) {
            vacios = true;
        }
        if (!mililitros.isChecked() && !unidades.isChecked()) {
            vacios = true;
        }
        if (spinner.getSelectedItem().toString().equals(getResources().getString(R.string.daily_times))) {
            vacios = true;
        }
        if (!lunes.isChecked() && !martes.isChecked() && !miercoles.isChecked() && !jueves.isChecked() && !viernes.isChecked() && !sabado.isChecked() && !domingo.isChecked()) {
            vacios = true;
        }
        if (cantidadRestante.length() == 0) {
            vacios = true;
        }
        if (reminder.length() == 0) {
            vacios = true;
        }
        return vacios;
    }

    public void DialogTratamientoExiste() {
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View customTitle = inflater.inflate(dialog, null);
        AlertDialog.Builder builder1 = new AlertDialog.Builder(AddPillActivity.this);
        builder1.setCustomTitle(customTitle);
        //TextView title = (TextView) customTitle.findViewById(R.id.customtitlebar);
        //title.setText("New Title");
        //builder1.setMessage("No Posees Tratamientos todavia");
        builder1.setMessage(R.string.existing_treatment);
        builder1.setIcon(R.drawable.warning);
        builder1.setCancelable(true);
        builder1.setPositiveButton(R.string.close,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //MensajeToast("Closing Dialog");
                    }
                });
        /*builder1.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {Mensaje("negativo"); } });*/
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void DialogEspaciosVacios() {
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View customTitle = inflater.inflate(dialog, null);
        AlertDialog.Builder builder1 = new AlertDialog.Builder(AddPillActivity.this);
        builder1.setCustomTitle(customTitle);
        //TextView title = (TextView) customTitle.findViewById(R.id.customtitlebar);
        //title.setText("New Title");
        //builder1.setMessage("No Posees Tratamientos todavia");
        builder1.setMessage(R.string.empty_fields);
        builder1.setIcon(R.drawable.warning);
        builder1.setCancelable(true);
        builder1.setPositiveButton(R.string.close,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //MensajeToast("Closing Dialog");
                    }
                });
        /*builder1.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {Mensaje("negativo"); } });*/
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void ErrorAdding() {
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View customTitle = inflater.inflate(dialog, null);
        AlertDialog.Builder builder1 = new AlertDialog.Builder(AddPillActivity.this);
        builder1.setCustomTitle(customTitle);
        //TextView title = (TextView) customTitle.findViewById(R.id.customtitlebar);
        //title.setText("New Title");
        //builder1.setMessage("No Posees Tratamientos todavia");
        builder1.setMessage(R.string.Error_Adding);
        builder1.setIcon(R.drawable.warning);
        builder1.setCancelable(true);
        builder1.setPositiveButton(R.string.close,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //MensajeToast("Closing Dialog");
                    }
                });
        /*builder1.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {Mensaje("negativo"); } });*/
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

}