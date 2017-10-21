package com.una.takeurpills;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import static android.R.attr.id;

public class HomeActivity extends ParentClass {
    private FirebaseUser fUser;
    private DatabaseReference mDatabase;
    private User user;
    private ValueEventListener mUserListener;

    private String userName;
    private String lastName;
    private String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        OnClickButton(R.id.btListPills);
        OnClickButton(R.id.btAddPills);
        OnClickButton(R.id.btFindPills);
        OnClickButton(R.id.btAbout);
        OnClickButton(R.id.home_logout_button);
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
                    case R.id.home_logout_button:
                        mAuth.signOut();
                        finish();
                        intento = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intento);
                    default: break;
                }// fin switch
            }// fin onClick
        });
    }// fin OnClickButton

    public boolean isActiveUser(){
        /*if(persistence == false){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            persistence = true;
        }*/
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        return (fUser != null);
    }

    public void setField(){
        userName = user.firstname;
        lastName = user.lastname;
        userID = user.username;
        TextView text = (TextView)findViewById(R.id.usernameText);
        text.setText(getResources().getString(R.string.home_user_name) + ": " + userName + " " + lastName + " " + "\n"
                + getResources().getString(R.string.home_user_id) + ": " + userID);
    }

    public void retrieveUserInfo(){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid());
        mUserListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                setField();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.addValueEventListener(mUserListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(isActiveUser()){
            retrieveUserInfo();
        }
    }

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
