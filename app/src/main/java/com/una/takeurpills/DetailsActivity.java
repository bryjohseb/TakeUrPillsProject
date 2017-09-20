package com.una.takeurpills;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.IntegerRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.Key;

import static com.una.takeurpills.R.id.domingo;
import static com.una.takeurpills.R.id.jueves;
import static com.una.takeurpills.R.id.lunes;
import static com.una.takeurpills.R.id.martes;
import static com.una.takeurpills.R.id.miercoles;
import static com.una.takeurpills.R.id.sabado;
import static com.una.takeurpills.R.id.viernes;

public class DetailsActivity extends ParentClass {
    private String unidadMedida = "";
    private int posicion = 0;
    private String veces = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.pill_logo);
        getSupportActionBar().setTitle(R.string.ab_detail_pill_header);
        getData();
        Button cancelar = (Button) findViewById(R.id.bt_detailsPill_delete);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertBuilder(v);
            }
        });

        OnclickDelButton(R.id.bt_detailsPill_edit);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modo = 0;
                Intent intento = new Intent(getApplicationContext(), AddPillActivity.class);
                startActivity(intento);
            }
        });
    } // Fin del Oncreate


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

    public void Mensaje(String msg) {
        getSupportActionBar().setTitle(msg);
    }

    public void getData() {
        Intent callingIntent = getIntent();
        posicion = callingIntent.getIntExtra("posicion", 0);
        JSONObject objjson = testjarray.optJSONObject(posicion);
        try {
            String titulo = String.valueOf(objjson.get("titulo"));
            int dosis = Integer.parseInt(String.valueOf(objjson.get("dosis")));
            String unidad = String.valueOf(objjson.get("Unidad"));
            unidadMedida = unidad;
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

            TextView Mi_textview = (TextView) findViewById(R.id.tv_detailsPill_nombreTratamiento);
            TextView Mi_textview2 = (TextView) findViewById(R.id.tv_detailsPill_dosis2);
            TextView Mi_textview3 = (TextView) findViewById(R.id.tv_detailsPill_cantidadRestante2);
            TextView Mi_textview4 = (TextView) findViewById(R.id.tv_detailsPill_reminder2);
            TextView Mi_textview5 = (TextView) findViewById(R.id.tv_detailsPill_frecuencia2);
            TextView Mi_textview6 = (TextView) findViewById(R.id.tv_detailsPill_repeticion2);
            TextView Mi_textview7 = (TextView) findViewById(R.id.tv_detailsPill_horas2);

            Mi_textview.setText(titulo);

            Mi_textview2.setText((dosis == 1) ? String.valueOf(dosis) + (unidad.equals("Unidades")
                    ? " " + getResources().getString(R.string.unidad_mesage) : " " + getResources().getString(R.string.mililitro_mesage)) :
                    String.valueOf(dosis) + (unidad.equals("Unidades")
                            ? " "+ getResources().getString(R.string.unidades_mesage): " "+ getResources().getString(R.string.mililitros_mesage)));
            Mi_textview3.setText((cantidadRestante == 1) ? String.valueOf(cantidadRestante) + (unidad.equals("Unidades")
                    ? " " + getResources().getString(R.string.unidad_mesage) : " " + getResources().getString(R.string.mililitro_mesage)) :
                    String.valueOf(cantidadRestante) + (unidad.equals("Unidades")
                            ? " " + getResources().getString(R.string.unidades_mesage) : " " + getResources().getString(R.string.mililitros_mesage)));
            Mi_textview4.setText((reminder == 1) ? String.valueOf(reminder) + (unidad.equals("Unidades")
                    ? " " + getResources().getString(R.string.unidad_mesage) : " " + getResources().getString(R.string.mililitro_mesage))  :
                    String.valueOf(reminder) + (unidad.equals("Unidades")
                            ? " " + getResources().getString(R.string.unidades_mesage) : " " + getResources().getString(R.string.mililitros_mesage)));

           /* Mi_textview2.setText((dosis == 1) ? String.valueOf(dosis) + (unidad.equals("Unidades")
                    ? " unidad" : " mililitro") : String.valueOf(dosis) + " " + unidad);
            Mi_textview3.setText((cantidadRestante == 1) ? String.valueOf(cantidadRestante) + (unidad.equals("Unidades")
                    ? " unidad" : " mililitro") : String.valueOf(cantidadRestante) + " " + unidad);
            Mi_textview4.setText((reminder == 1) ? String.valueOf(reminder) + (unidad.equals("Unidades")
                    ? " unidad" : " mililitro") : String.valueOf(reminder) + " " + unidad);*/

            Mi_textview6.setText((vecesDiarias != 1) ? String.valueOf(vecesDiarias) + " " + getResources().getString(R.string.tv_detailsPill_veces_dia)
                    : String.valueOf(vecesDiarias) + " " + getResources().getString(R.string.tv_detailsPill_una_vez_dia_));
            Mi_textview5.setText("");
            Mi_textview5.append((!lunes.equals("")) ? String.valueOf(getResources().getString(R.string.dias_lunes)) + " / " : String.valueOf("") + "");
            Mi_textview5.append((!martes.equals("")) ? String.valueOf(getResources().getString(R.string.dias_martes)) + " / " : String.valueOf("") + "");
            Mi_textview5.append((!miercoles.equals("")) ? String.valueOf(getResources().getString(R.string.dias_miercoles)) + " / " : String.valueOf("") + "");
            Mi_textview5.append((!jueves.equals("")) ? String.valueOf(getResources().getString(R.string.dias_jueves)) + " / " : String.valueOf("") + "");
            Mi_textview5.append((!viernes.equals("")) ? String.valueOf(getResources().getString(R.string.dias_viernes)) + " / " : String.valueOf("") + "");
            Mi_textview5.append((!sabado.equals("")) ? String.valueOf(getResources().getString(R.string.dias_sabado)) + " / " : String.valueOf("") + "");
            Mi_textview5.append((!domingo.equals("")) ? String.valueOf(getResources().getString(R.string.dias_domingo)) + " ." : String.valueOf("") + "");


            //Esto es para quitar el "/" del final
            String diasFinales = Mi_textview5.getText().toString();
            diasFinales = diasFinales.substring(0, diasFinales.length() - 2);
            Mi_textview5.setText(diasFinales);

            //Esto es para setear la hora a 12 horas.
            Mi_textview7.setText("");
            for (int i = 0; i < vecesDiarias; i++) {
                String horas = String.valueOf(objjson.get("hora" + i));
                //String hora = horas.substring(0, 2);
                String hora = getHours(horas);
                //String minutos = horas.substring(2, horas.length());
                String minutos = getMinutes(horas);
                int doceHoras = Integer.parseInt(hora);
                if (doceHoras >= 12) {
                    switch (doceHoras) {
                        case 13:
                            hora = "01";
                            break;
                        case 14:
                            hora = "02";
                            break;
                        case 15:
                            hora = "03";
                            break;
                        case 16:
                            hora = "04";
                            break;
                        case 17:
                            hora = "05";
                            break;
                        case 18:
                            hora = "06";
                            break;
                        case 19:
                            hora = "07";
                            break;
                        case 20:
                            hora = "08";
                            break;
                        case 21:
                            hora = "09";
                            break;
                        case 22:
                            hora = "10";
                            break;
                        case 23:
                            hora = "11";
                            break;
                        default:
                            break;
                    }
                    horas = hora + ":" + minutos + " pm";
                } else {
                    if (doceHoras == 0) {
                        hora = "12";
                        horas = hora + ":" + minutos + " am";
                    } else
                        horas = hora + ":" + minutos + " am";
                }

                Mi_textview7.append((!horas.equals("")) ? String.valueOf(horas) + "/" : String.valueOf("") + "");

                //Esto es para quitar el "/" del final
                if (i == vecesDiarias - 1) {
                    String horasFinal = Mi_textview7.getText().toString();
                    horasFinal = horasFinal.substring(0, horasFinal.length() - 1);
                    Mi_textview7.setText(horasFinal);
                }


            }

        } catch (Exception exc) {
        }
    }

    public void Mensaje2(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    ;

    public void AlertBuilder(View view) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
        builder1.setMessage(R.string.delete_message);
        builder1.setCancelable(true);
        builder1.setPositiveButton(R.string.yes_message,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Mensaje2(getResources().getString(R.string.delete_success));
                        RemoveObj(); //Archivo
                        Remove();//Firebase
                        //Aca agregamos el metodo para eliminar el tratamiento de la lista.
                        Intent intento = new Intent(getApplicationContext(), ListPillsActivity.class);
                        startActivity(intento);
                    }
                });
        builder1.setNegativeButton(R.string.no_message,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Nada
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void Remove() {
        try {
            if(persistence == false){
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                persistence = true;
            }
            database = FirebaseDatabase.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            String key = "";
            if (currentUser != null) {
                key = currentUser.getUid();
            }
            final TextView Mi_textview = (TextView) findViewById(R.id.tv_detailsPill_nombreTratamiento);
            myRef = database.getReference("Treatments");
            //String keyTreatment = myRef.child(key).child(Mi_textview.getText().toString()).getKey();
            myRef.child(key).child(Mi_textview.getText().toString()).removeValue();
            treatmentName.remove(Mi_textview.getText().toString());
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Mensaje(databaseError.getMessage());
                }
            });
        } catch (Exception exc) {
            exc.getCause().toString();
        }
    }

    public void OnclickDelButton(int ref) {
        // Ejemplo  OnclickDelButton(R.id.MiButton);
        // 1 Doy referencia al Button
        View view = findViewById(ref);
        Button miButton = (Button) view;
        //  final String msg = miButton.getText().toString();
        // 2.  Programar el evento onclick
        miButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if(msg.equals("Texto")){Mensaje("Texto en el botón ");};
                switch (v.getId()) {
                    case R.id.bt_detailsPill_edit:
                        //Aca se implementa los PutExtra para enviar a la pantalla de AddPills en modo edicion
                        /*TextView Mi_textview = (TextView) findViewById(R.id.tv_detailsPill_nombreTratamiento);
                        TextView Mi_textview2 = (TextView) findViewById(R.id.tv_detailsPill_dosis2);
                        TextView Mi_textview3 = (TextView) findViewById(R.id.tv_detailsPill_cantidadRestante2);
                        TextView Mi_textview4 = (TextView) findViewById(R.id.tv_detailsPill_reminder2);
                        TextView Mi_textview5 = (TextView) findViewById(R.id.tv_detailsPill_frecuencia2);

                        String dosis = splitNumbers(Mi_textview2.getText().toString());
                        int dosisN = Integer.parseInt(dosis);
                        String cantRestante = splitNumbers(Mi_textview3.getText().toString());
                        int cantRestanteN = Integer.parseInt(cantRestante.replace("es",""));
                        String reminder = splitNumbers(Mi_textview4.getText().toString());
                        int reminderN = Integer.parseInt(reminder);
                        String lunes = Mi_textview5.getText().toString().contains("lunes") ?
                                Mi_textview5.getText().toString().replace("lunes", "1"):
                                Mi_textview5.getText().toString().replace("lunes", "");
                        String martes = Mi_textview5.getText().toString().contains("martes") ?
                                Mi_textview5.getText().toString().replace("martes", "2"):
                                Mi_textview5.getText().toString().replace("martes", "");
                        String miercoles = Mi_textview5.getText().toString().contains("miercoles") ?
                                Mi_textview5.getText().toString().replace("miercoles", "3"):
                                Mi_textview5.getText().toString().replace("miercoles", "");
                        String jueves = Mi_textview5.getText().toString().contains("jueves") ?
                                Mi_textview5.getText().toString().replace("jueves", "4"):
                                Mi_textview5.getText().toString().replace("jueves", "");
                        String viernes = Mi_textview5.getText().toString().contains("viernes") ?
                                Mi_textview5.getText().toString().replace("viernes", "5"):
                                Mi_textview5.getText().toString().replace("viernes", "");
                        String sabado = Mi_textview5.getText().toString().contains("sabado") ?
                                Mi_textview5.getText().toString().replace("sabado", "6"):
                                Mi_textview5.getText().toString().replace("sabado", "");
                        String domingo = Mi_textview5.getText().toString().contains("domingo") ?
                                Mi_textview5.getText().toString().replace("domingo", "7"):
                                Mi_textview5.getText().toString().replace("domingo", "");*/


                        //Transicion de datos
                        modo = 1;
                        Intent intento = new Intent(getApplicationContext(), AddPillActivity.class);
                        intento.putExtra("posicion", posicion);
                        /*intento.putExtra("edicion", 1);
                        intento.putExtra("titulo", Mi_textview.getText());
                        intento.putExtra("dosis", dosisN);
                        intento.putExtra("Unidad", unidadMedida);
                        intento.putExtra("cantidadRestante", cantRestanteN);
                        intento.putExtra("Reminder", reminderN);
                        intento.putExtra("Dia_1", lunes);
                        intento.putExtra("Dia_2", martes);
                        intento.putExtra("Dia_3", miercoles);
                        intento.putExtra("Dia_4", jueves);
                        intento.putExtra("Dia_5", viernes);
                        intento.putExtra("Dia_6", sabado);
                        intento.putExtra("Dia_7", domingo);*/
                        startActivity(intento);
                        break;
                    default:
                        break;
                }// fin de casos
            }// fin del onclick
        });
    }// fin de OnclickDelButton

    private void RemoveObj() {
        final int len = testjarray.length();
        JSONArray list = new JSONArray();
        if (testjarray != null)
            for (int i = 0; i < len; i++)
                if (i != posicion)
                    list.put(testjarray.optJSONObject(i));
        writeToFile(list.toString());
    }

    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("data.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public String splitNumbers(String cadena) {
        return cadena.replaceAll("[^0-9]", "");
    }

    public String getHours(String cadena) {
        String hora = "";
        for (int i = 0; cadena.charAt(i) != ':'; i++) {
            hora = hora + cadena.charAt(i);
        }
        if (hora.length() < 2)
            hora = "0" + hora;
        return hora;
    }

    public String getMinutes(String cadena) {
        String minutos = "";
        for (int i = 0; i < cadena.length(); i++) {
            if (cadena.charAt(i) == ':') {
                minutos = cadena.substring(i + 1, cadena.length());
                if (minutos.length() < 2)
                    minutos = "0" + minutos;
                break;
            }
        }
        return minutos;
    }
}