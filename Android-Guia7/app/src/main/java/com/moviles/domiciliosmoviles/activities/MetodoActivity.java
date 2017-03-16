package com.moviles.domiciliosmoviles.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.moviles.domiciliosmoviles.R;

public class MetodoActivity extends AppCompatActivity {
    private static String TAG = "Metodo TAG";
    private ProgressDialog progressDialog;
    private RadioGroup rg;
    @Override
    protected void onStart() {
        super.onStart();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.espera_metodo));
        progressDialog.show();
        Log.d(TAG,"Metodo on start");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"Metodo on create");
        setContentView(R.layout.activity_metodo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        simulateRest();
        rg = (RadioGroup)findViewById(R.id.rg_metodos);
        findViewById(R.id.select_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String opcion = ((RadioButton)findViewById(rg.getCheckedRadioButtonId())).getText().toString();
                setResult(RESULT_OK,new Intent().putExtra("method",opcion));
                finish();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG,"Método on stop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"Método on pause");
    }

    private void simulateRest() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Log.d(TAG,"Ocultar diálogo");
                progressDialog.dismiss();
            }
        }.execute();
    }



}
