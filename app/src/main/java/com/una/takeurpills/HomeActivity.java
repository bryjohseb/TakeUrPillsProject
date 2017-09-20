package com.una.takeurpills;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends ParentClass {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        OnClickButton(R.id.btListPills);
        OnClickButton(R.id.btAddPills);
        OnClickButton(R.id.btFindPills);
        OnClickButton(R.id.btAbout);
    }// fin onCreate
    public void OnClickButton(int ref){
        View view = findViewById(ref);
        Button miButton = (Button) view;
        miButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btListPills:
                        Intent intento2 = new Intent(getApplicationContext(), ListPillsActivity.class);
                        startActivity(intento2);
                        break;
                    case R.id.btAddPills:
                        modo = 0;
                        Intent intento = new Intent(getApplicationContext(), AddPillActivity.class);
                        intento.putExtra("edicion", 0);
                        startActivity(intento);
                        break;
                    case R.id.btFindPills:
                        Intent intento3 = new Intent(getApplicationContext(), FindPillsActivity.class);
                        startActivity(intento3);
                        break;
                    case R.id.btAbout:
                        Intent intento4 = new Intent(getApplicationContext(), AboutActivity.class);
                        startActivity(intento4);
                        break;
                    default: break;
                }// fin switch
            }// fin onClick
        });
    }// fin OnClickButton
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
    public void onBackPressed() {
        //super.finish();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}// fin Activity
