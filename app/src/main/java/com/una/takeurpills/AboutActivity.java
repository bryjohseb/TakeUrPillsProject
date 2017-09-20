package com.una.takeurpills;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.una.takeurpills.ParentClass.mAuth;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Mensaje("About us");

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.pill_logo);
        LlenarListaObjetos();
        LlenarListView();
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

    public void Mensaje(String msg){getSupportActionBar().setTitle(msg);};

    private List<ObjetosXDesplegar> misObjetos = new ArrayList<ObjetosXDesplegar>();
    private void LlenarListaObjetos() {
        misObjetos.add(new ObjetosXDesplegar("Bryan Murillo Rodriguez", "402240326", R.drawable.bryan));
        misObjetos.add(new ObjetosXDesplegar("Katherine Solorzano Quintanilla", "115910112", R.drawable.kathy));
        misObjetos.add(new ObjetosXDesplegar("Paolo Vargas Campos", "112040946", R.drawable.paolo));
       misObjetos.add(new ObjetosXDesplegar("Diego Vargas Medrano", "115880814", R.drawable.diego));


    }
    private void LlenarListView() {
        ArrayAdapter<ObjetosXDesplegar> adapter = new MyListAdapter();
        ListView list = (ListView) findViewById(R.id.listview);
        list.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<ObjetosXDesplegar> {
        public MyListAdapter() {
            super(AboutActivity.this, R.layout.listview, misObjetos);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.listview, parent, false);
            }
            ObjetosXDesplegar ObjetoActual = misObjetos.get(position);
            ImageView imageView = (ImageView)itemView.findViewById(R.id.ivdibujo);
            imageView.setImageResource(ObjetoActual.getNumDibujo());
            TextView elatributo01 = (TextView) itemView.findViewById(R.id.paraelatributo01);
            elatributo01.setText(ObjetoActual.getAtributo01());
            TextView elatributo02 = (TextView) itemView.findViewById(R.id.paraelatributo02);
            elatributo02.setText("" + ObjetoActual.getAtributo02());
            return itemView;
        }
    }
}