package com.una.takeurpills;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import static com.una.takeurpills.ParentClass.testjarray;
import static com.una.takeurpills.R.id.textView4;

/**
 * Created by Esteban on 4/30/2017.
 */

public class AlarmReceiver extends Activity {
    private MediaPlayer mMediaPlayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.alarm);

        Button stopAlarm = (Button) findViewById(R.id.stopAlarm);
        Button takePillAlarm = (Button) findViewById(R.id.takePill);
        Intent callingIntent = getIntent();
        int id = callingIntent.getIntExtra("treatment", 0);
        final String title = callingIntent.getStringExtra("title");

        stopAlarm.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                mMediaPlayer.stop();
                finish();
                return false;
            }
        });

        takePillAlarm.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {

                final int len = testjarray.length();
                for (int i = 0; i < len; i++) {
                    final JSONObject obj = testjarray.optJSONObject(i);
                    try {
                        if (String.valueOf(obj.get("titulo")).toString().equals(title)) {
                            int dosis = Integer.parseInt(String.valueOf(obj.get("dosis")));
                            int cantidadRestante = Integer.parseInt(String.valueOf(obj.get("cantidadRestante")));
                            int nuevaCantidad = cantidadRestante - dosis;
                            String cantidad = Integer.toString(nuevaCantidad);
                            obj.put("cantidadRestante", cantidad);
                            writeToFile(testjarray.toString());
                        }
                    } catch (JSONException e) {
                    }
                }
                mMediaPlayer.stop();
                finish();
                Intent intento = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intento);
                return false;
            }
        });

        TextView texto = (TextView) findViewById(R.id.textView4);
        texto.setText(texto.getText().toString() + "\n" + title);

        playSound(this, getAlarmUri());
    }

    private void playSound(Context context, Uri alert) {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(context, alert);
            final AudioManager audioManager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        } catch (IOException e) {
            System.out.println("OOPS");
        }
    }

    //Get an alarm sound. Try for an alarm. If none set, try notification,
    //Otherwise, ringtone.
    private Uri getAlarmUri() {
        Uri alert = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alert == null) {
            alert = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alert == null) {
                alert = RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        return alert;
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

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}