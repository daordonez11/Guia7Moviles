package com.moviles.domiciliosmoviles.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.FloatMath;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.moviles.domiciliosmoviles.R;
import com.moviles.domiciliosmoviles.entities.Pedido;
import com.moviles.domiciliosmoviles.entities.ResponseMessage;
import com.moviles.domiciliosmoviles.rest.RestClient;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PedidosActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mAcc;
    private static final String TAG = "Pedidos";
    private static final int REQUEST_PHOTO = 111;
    private static final int REQUEST_PERMISSION = 222;
    private String platoName;
    private int platoId;
    CoordinatorLayout coordinatorLayout;
    public final static int PAYMENT = 1111;
    private Button btnPayment, btnGPS, btnImages;
    private ImageView clientIv;
    private Uri imageToUploadUri1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent() != null && getIntent().getExtras().getString("name") != null){
            platoName = getIntent().getExtras().getString("name");
            platoId = getIntent().getExtras().getInt("id");
        }
        getSupportActionBar().setTitle(platoName);

        Button btnComment = (Button) findViewById(R.id.pedir_btn);
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realizarPedido();
            }
        });
        btnPayment = (Button) findViewById(R.id.payment_method);
        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                solicitarMetodo();
            }
        });
        btnGPS = (Button) findViewById(R.id.gps_btn);
        btnGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                encontrarUbicacion();
            }
        });
        btnImages = (Button) findViewById(R.id.image_btn);
        btnImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarImagen();
            }
        });
        clientIv = (ImageView)findViewById(R.id.client_image);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAcc, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    private void seleccionarImagen() {
        PackageManager packageManager = getPackageManager();
        if(packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA) == false){
            Toast.makeText(this, "This device does not have a camera.", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        Intent pickIntent2 = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent2.setType("image/*");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = new File(Environment.getExternalStorageDirectory(), "/monitoria/client.png");
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));

        String pickTitle = "Take or select a photo";
        Intent chooserIntent = Intent.createChooser(pickIntent2, pickTitle);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { takePictureIntent });
        imageToUploadUri1 = Uri.fromFile(f);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(chooserIntent,REQUEST_PHOTO);
        }
    }

    private void encontrarUbicacion() {
        LocationManager mlocManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);
            return;
        }
        Location mLastLocation = mlocManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(mLastLocation!=null){
            btnGPS.setText(mLastLocation.getLatitude() + " - "+mLastLocation.getLongitude());
        } else{
            new AlertDialog.Builder(this)
                    .setTitle(this.getResources().getString(R.string.result))
                    .setMessage(R.string.gps_not_found)
                    .setNegativeButton(getResources().getString(R.string.ok), null)
                    .create().show();
        }
    }

    private void solicitarMetodo() {
        Intent i = new Intent(this, MetodoActivity.class);
        startActivityForResult(i,PAYMENT);
    }

    public void realizarPedido() {

        String cliente = ((TextView) findViewById(R.id.nombre_cliente)).getText().toString();
        String lugar = ((TextView) findViewById(R.id.lugar_pedido)).getText().toString();
        Pedido pedido = new Pedido(cliente, lugar, platoId);

        Call<ResponseMessage> call = RestClient.getInstance().getApiService().createPedido(pedido);
        call.enqueue(new Callback<ResponseMessage>() {
            @Override
            public void onResponse(Call<ResponseMessage> call, Response<ResponseMessage> response) {

                ResponseMessage res = response.body();

                Snackbar snackbar = Snackbar.make(coordinatorLayout, res.getMsg(), Snackbar.LENGTH_LONG);
                snackbar.show();
            }

            @Override
            public void onFailure(Call<ResponseMessage> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG,"Llega resultado de otra actividad");
        if (requestCode==PAYMENT){
            if(resultCode==MetodoActivity.RESULT_OK){
                btnPayment.setText(data.getStringExtra("method"));
            }
        }
        if (requestCode == REQUEST_PHOTO && resultCode == Activity.RESULT_OK) {
            if(data!=null) {
                imageToUploadUri1 = data.getData();
            }
            if(imageToUploadUri1 != null){
                Uri selectedImage = imageToUploadUri1;
                getContentResolver().notifyChange(selectedImage, null);

                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_PERMISSION);

                    Log.d(TAG,"No hay permiso");
                }
                Bitmap reducedSizeBitmap = getBitmap(getRealPathFromURI(imageToUploadUri1));
                if(reducedSizeBitmap != null) {
                    Log.d(TAG,"Llega imagen");
                    clientIv.setImageBitmap(reducedSizeBitmap);
                }
            }else{
                Toast.makeText(this,"Error while capturing Image by Uri",Toast.LENGTH_LONG).show();
            }

        }
    }
    private Bitmap getBitmap(String path) {

        Uri uri = Uri.fromFile(new File(path));
        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
            in = getContentResolver().openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();


            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) >
                    IMAGE_MAX_SIZE) {
                scale++;
            }
            Log.d("", "scale = " + scale + ", orig-width: " + o.outWidth + ", orig-height: " + o.outHeight);

            Bitmap b = null;
            in = getContentResolver().openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                b = BitmapFactory.decodeStream(in, null, o);

                // resize to desired dimensions
                int height = b.getHeight();
                int width = b.getWidth();
                Log.d("", "1th scale operation dimenions - width: " + width + ", height: " + height);

                double y = Math.sqrt(IMAGE_MAX_SIZE
                        / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
                        (int) y, true);
                b.recycle();
                b = scaledBitmap;

                System.gc();
            } else {
                b = BitmapFactory.decodeStream(in);
            }
            in.close();

            Log.d("", "bitmap size - width: " + b.getWidth() + ", height: " +
                    b.getHeight());
            return b;
        } catch (IOException e) {
            Log.e("", e.getMessage(), e);
            return null;
        }
    }
    private String getRealPathFromURI(Uri contentURI) {
        String result ="";
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            try{
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx);

            }catch(Exception e)
            {
                Log.d("data", contentURI.toString());
                return contentURI.getPath();
            }
            cursor.close();
        }
        return result;
    }
    private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F;
    private static final int SHAKE_SLOP_TIME_MS = 500;
    private static final int SHAKE_COUNT_RESET_TIME_MS = 3000;
    private long mShakeTimestamp;
    private int mShakeCount;
    @Override
    public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            float gX = x / SensorManager.GRAVITY_EARTH;
            float gY = y / SensorManager.GRAVITY_EARTH;
            float gZ = z / SensorManager.GRAVITY_EARTH;
            // gForce will be close to 1 when there is no movement.
            double gForce = Math.sqrt(gX * gX + gY * gY + gZ * gZ);
            if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                final long now = System.currentTimeMillis();
                // ignore shake events too close to each other (500ms)
                if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                    return;
                }
                // reset the shake count after 3 seconds of no shakes
                if (mShakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now) {
                    mShakeCount = 0;
                }
                mShakeTimestamp = now;
                mShakeCount++;
               realizarPedido();
            }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
