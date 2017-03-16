package com.moviles.domiciliosmoviles.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.moviles.domiciliosmoviles.R;
import com.moviles.domiciliosmoviles.adapters.PlatoAdapter;
import com.moviles.domiciliosmoviles.entities.Plato;
import com.moviles.domiciliosmoviles.rest.RestClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private List<Plato> platos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView=(ListView)findViewById(R.id.platos_listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this,PedidosActivity.class);
                i.putExtra("name", ((Plato)platos.get(position)).getNombre());
                i.putExtra("id", ((Plato)platos.get(position)).getId());
                startActivity(i);
            }
        });

        getPlatos();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_alert:
                AlertDialog dialog = createAlertDialog();
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public AlertDialog createAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Hello World")
                .setTitle("Dialogo de Alerta")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Do something
                    }
                });
        return builder.create();
    }

    public void getPlatos() {
        Call<List<Plato>> call = RestClient.getInstance().getApiService().getPlatos();
        call.enqueue(new Callback<List<Plato>>() {
            @Override
            public void onResponse(Call<List<Plato>> call, Response<List<Plato>> response) {
                platos = response.body();
                PlatoAdapter itemsAdapter = new PlatoAdapter(MainActivity.this, platos);
                listView.setAdapter(itemsAdapter);
            }

            @Override
            public void onFailure(Call<List<Plato>> call, Throwable t) {

            }
        });
    }
}
