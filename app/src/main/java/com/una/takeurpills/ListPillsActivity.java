package com.una.takeurpills;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static com.una.takeurpills.R.layout.dialog;

public class ListPillsActivity extends ParentClass {
    //private JSONArray testjarray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_pills);
        FillListView();
        OnClickListItems();
        //datosFirebase();

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.pill_logo);
        getSupportActionBar().setTitle(R.string.ab_list_pill_header);


    } // Fin del Oncreate de la Actividad 01

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
            case R.id.sign_out:
                mAuth.signOut();
                finish();
                intento = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intento);
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
        return true;
    }

    /*
        @Override
        public void onStart() {
            super.onStart();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if(currentUser == null ){
                Intent intento = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intento);
            }
        }
    */
    @Override
    public void onBackPressed(){
        Intent intento = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intento);
    }
    //Aca se pueden cargar los tratamientos desde el Json  del app, pero solo los nombres (posible a cambios)
    private void FillListView() {
        String[] test = null;
        try {
            testjarray = readFromFile();
            test = getNames(testjarray);
        } catch (JSONException exc) {

        }

        String[] pills = {
                "Acetaminofen",
                "Paracetamol",
                "Ibuprofeno",
                "Flumocil",
                "Acetaminofen",
                "Paracetamol",
                "Ibuprofeno",
                "Flumocil",
                "Acetaminofen",
                "Paracetamol",
                "Ibuprofeno",
                "Flumocil",
                "Acetaminofen",
                "Paracetamol",
                "Ibuprofeno",
                "Flumocil"
        };
        /*ArrayAdapter<String> adaptador = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, ((test[0].equals("")) ? pills : test));*/
        if (test[0].equals("")) DialogAviso();
        ArrayAdapter<String> adaptador = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, (test));
        ListView milistview = (ListView) findViewById(R.id.listPills);
        milistview.setAdapter(adaptador);
    }
    public void datosFirebase(){
        database = FirebaseDatabase.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String key = "";
        if (currentUser != null) {
            key = currentUser.getUid();
        }
        myRef = database.getReference("Treatments").child(key);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String miscarros ="";
                String aux ="";
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    aux = postSnapshot.getKey();
                    miscarros += String.valueOf(postSnapshot.getValue());
                }
                MensajeOK(miscarros);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void DialogAviso() {
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View customTitle = inflater.inflate(dialog, null);
        AlertDialog.Builder builder1 = new AlertDialog.Builder(ListPillsActivity.this);
        builder1.setCustomTitle(customTitle);
        //TextView title = (TextView) customTitle.findViewById(R.id.customtitlebar);
        //title.setText("New Title");
        //builder1.setMessage("No Posees Tratamientos todavia");
        builder1.setMessage(R.string.empty_treatment);
        builder1.setIcon(R.drawable.warning);
        builder1.setCancelable(true);
        builder1.setPositiveButton(R.string.close,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                       // MensajeToast("Closing Dialog");
                        Intent intento = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(intento);
                    }
                });
        /*builder1.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {Mensaje("negativo"); } });*/
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    //Se cambia este metodo para enviar a pantalla de Detalles del tratamiento
    public void OnClickListItems() {
        ListView list = (ListView) findViewById(R.id.listPills);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> paret, View viewClicked,
                                    int position, long id) {
                //JSONObject objjson = testjarray.optJSONObject(position);
                Intent intento = new Intent(getApplicationContext(), DetailsActivity.class);
                intento.putExtra("posicion", position);
                    /*intento.putExtra("titulo", String.valueOf(objjson.get("titulo")));
                    intento.putExtra("dosis", Integer.parseInt(String.valueOf(objjson.get("dosis"))));
                    intento.putExtra("Unidad",String.valueOf(objjson.get("Unidad")));
                    intento.putExtra("cantidadRestante", Integer.parseInt(String.valueOf(objjson.get("cantidadRestante"))));
                    intento.putExtra("Reminder", Integer.parseInt(String.valueOf(objjson.get("Reminder"))));
                    if (objjson.has("Dia_1"))
                        intento.putExtra("Dia_1", String.valueOf(objjson.get("Dia_1")));
                    if (objjson.has("Dia_2"))
                        intento.putExtra("Dia_2", String.valueOf(objjson.get("Dia_2")));
                    if (objjson.has("Dia_3"))
                        intento.putExtra("Dia_3", String.valueOf(objjson.get("Dia_3")));
                    if (objjson.has("Dia_4"))
                        intento.putExtra("Dia_4", String.valueOf(objjson.get("Dia_4")));
                    if (objjson.has("Dia_5"))
                        intento.putExtra("Dia_5", String.valueOf(objjson.get("Dia_5")));
                    if (objjson.has("Dia_6"))
                        intento.putExtra("Dia_6", String.valueOf(objjson.get("Dia_6")));
                    if (objjson.has("Dia_7"))
                        intento.putExtra("Dia_7", String.valueOf(objjson.get("Dia_7")));*/
                startActivity(intento);
                TextView textView = (TextView) viewClicked;
                //String message = "Tratamiento # " + (1 + position) + ", corresponde a: " + textView.getText().toString();
                //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
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
        //JSONObject test;
        //int tam = jarray.length();
        //test = jarray.getJSONObject(0);
        //String nombre = String.valueOf(test.get("nombre"));
        //int edad = Integer.parseInt(String.valueOf(test.get("edad")));
        //String email = String.valueOf(test.get("correo"));
        //final List<JSONObject> objs = asList(jarray);
        //objs.remove(1);

        return jarray;
    }

    public static String[] getNames(final JSONArray jarray) {
        ArrayList<String> test = getVector(jarray);
        String lala = test.toString().substring(1, test.toString().length() - 1);
        String[] lalala = lala.split(",");
        return lalala;
    }

    public static JSONObject getObject(final JSONArray ja) {
        final int len = ja.length();
        final JSONObject result = null;
        for (int i = 0; i < len; i++) {
            final JSONObject obj = ja.optJSONObject(i);
            try {
                String nombre = String.valueOf(obj.get("titulo"));
            } catch (JSONException exc) {
            }
        }
        return result;
    }

    public static ArrayList<String> getVector(final JSONArray ja) {
        final int len = ja.length();
        final ArrayList<String> result = new ArrayList<String>();
        for (int i = 0; i < len; i++) {
            final JSONObject obj = ja.optJSONObject(i);
            try {
                String nombre = String.valueOf(obj.get("titulo"));
                result.add(nombre);
            } catch (JSONException exc) {
            }
        }
        return result;
    }

    public void Mensaje(String msg) {
        getSupportActionBar().setTitle(msg);
    }

    ;

    public void MensajeToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void MensajeOK(String msg){
        View v1 = getWindow().getDecorView().getRootView();
        AlertDialog.Builder builder1 = new AlertDialog.Builder( v1.getContext());
        builder1.setMessage(msg);
        builder1.setCancelable(true);
        builder1.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {} });
        AlertDialog alert11 = builder1.create();
        alert11.show();
        }

}
