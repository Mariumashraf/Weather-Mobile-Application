package com.iti.recyclerview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity2 extends AppCompatActivity implements LocationListener {
    SharedPreferences sharedpreferences;

    public static final String mypreference = "mypref";
    public static final String temperatureKey = "tempKey";
    public static final String pressureKey = "preKey";
    public static final String humiditiyKey = "humKey";
    public static final String statusKey = "statusKey";
    public static final String imageKey = "imageKey";
    TextView textViewCity;
    TextView textViewTemp;
    TextView textViewPressure;
    TextView textViewHumidity;
    TextView textViewStatus;
    TextView dateTime;
    ImageView imageViewWeather;
    String data1;
    LocationManager locationManager;
    String provider;
    int newTemp;
    String URL = "https://api.openweathermap.org/data/2.5/";
    String apikey = "5591cb3fcc2d6c4445455fd59b39b5be";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        textViewCity = findViewById(R.id.country);
        textViewTemp = findViewById(R.id.temp);
        textViewPressure = findViewById(R.id.pressuretxt);
        textViewHumidity = findViewById(R.id.Humiditiytxt);
        textViewStatus = findViewById(R.id.status);
        dateTime = findViewById(R.id.date);
        imageViewWeather= findViewById(R.id.img);

        getLocationsForAPIs();
        saveReturnedData();
        getData();
        setData();

    }


    //Function to display saved data
    public void saveReturnedData(){
        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        if (sharedpreferences.contains(temperatureKey)) {
            textViewTemp.setText(sharedpreferences.getString(temperatureKey, ""));
        }
        if (sharedpreferences.contains(pressureKey)) {
            textViewPressure.setText(sharedpreferences.getString(pressureKey, ""));

        }
        if (sharedpreferences.contains(humiditiyKey)) {
            textViewHumidity.setText(sharedpreferences.getString(humiditiyKey, ""));
        } if (sharedpreferences.contains(statusKey)) {
            textViewStatus.setText(sharedpreferences.getString(statusKey, ""));
        }
        if (sharedpreferences.contains(imageKey)) {
            String imageS = sharedpreferences.getString("imageKey", "");
            Bitmap imageB;
           imageB = decodeToBase64(imageS);
           imageViewWeather.setImageBitmap(imageB);

        }
    }


    //Function to getlocation and get data from api

    public void getLocationsForAPIs(){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = LocationManager.GPS_PROVIDER;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED){
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        onLocationChanged(location);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Weatherapi weatherapi = retrofit.create(Weatherapi.class);
        Call<InitializtionMain> initializtionCall = weatherapi.getWeather(location.getLongitude(),location.getLatitude(), apikey);
        initializtionCall.enqueue(new Callback<InitializtionMain>() {
            @Override
            public void onResponse(Call<InitializtionMain> call, Response<InitializtionMain> response) {
                if (response.code() == 404) {
                    Toast.makeText(MainActivity2.this, "Please Enter a Valid city", Toast.LENGTH_LONG).show();
                } else if (!(response.isSuccessful())) {
                    Toast.makeText(MainActivity2.this, response.code(), Toast.LENGTH_LONG).show();
                }
                InitializtionMain mydata = response.body();
                Main main = mydata.getMain();
                Double temp = main.getTemp();
                int pre = main.getPressure();
                int hum = main.getHumidity();
                newTemp = (int) (temp - 273.15);
                List<Weather> weather = mydata.getWeather();
                String status = weather.get(0).getMain();


                textViewTemp.setText(String.valueOf(newTemp) + "°C");
                textViewPressure.setText("Pressure: " + String.valueOf(pre) + " hPa");
                textViewHumidity.setText("Humidity: " + String.valueOf(hum) + " %");
                textViewStatus.setText(String.valueOf(status));
                if(newTemp<10){
                    imageViewWeather.setImageResource(R.drawable.rain);
                } if(newTemp >10 && newTemp <19){
                    imageViewWeather.setImageResource(R.drawable.clouds);
                } if(newTemp >=20 && newTemp <= 25){
                    imageViewWeather.setImageResource(R.drawable.cloudy);
                }if(newTemp >=26 && newTemp <= 30){
                    imageViewWeather.setImageResource(R.drawable.sun2);
                }if(newTemp > 30){
                    imageViewWeather.setImageResource(R.drawable.sunny);
                }

                Save();

            }

            @Override
            public void onFailure(Call<InitializtionMain> call, Throwable t) {

                Toast.makeText(MainActivity2.this, t.getMessage(), Toast.LENGTH_LONG).show();


            }
        });

    }


    //Function to get data "city name" from recyclerview
    public void getData(){
        if(getIntent().hasExtra("data1")){
            data1 = getIntent().getStringExtra("data1");
        }else{
            Toast.makeText(this,"No data",Toast.LENGTH_SHORT).show();
        }

    }


    //Function to set data to textView
    public void setData(){
        textViewCity.setText(data1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
        String currentDateandTime = sdf.format(new Date());
        dateTime.setText("Last Update: " + currentDateandTime);

    }
    //For save all data to sharedpreferences
    public void Save(){
        String t= textViewTemp.getText().toString();
        String p = textViewPressure.getText().toString();
        String h = textViewHumidity.getText().toString();
        String s = textViewStatus.getText().toString();
        imageViewWeather.invalidate();
        BitmapDrawable drawable = (BitmapDrawable) imageViewWeather.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(temperatureKey, t);
        editor.putString(pressureKey, p);
        editor.putString(humiditiyKey, h);
        editor.putString(statusKey, s);
        editor.putString(imageKey, encodeToBase64(bitmap));
        editor.commit();
    }

    // For save image to sharedpreferences
    public static String encodeToBase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.d("Image Log:", imageEncoded);
        return imageEncoded;
    }
    // For save image to sharedpreferences
    public static Bitmap decodeToBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Log.d("Latitude","disable");
    }
}