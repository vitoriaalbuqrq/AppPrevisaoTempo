package com.project.appprevisaotempo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {


    private final String url = "https://api.openweathermap.org/data/2.5/weather";
    private final String appid = "ab320bffd0a858d7cce0f43efba1ab7a";

    final long MIN_TIME = 5000;
    final float MIN_DISTANCE = 1000;
    final int REQUEST_CODE = 101;

    private RelativeLayout backgroundLayout;

    String Location_Provider = LocationManager.GPS_PROVIDER;

    EditText cityFinder;
    TextView txt_temperatura, txt_valor_sensacao_term, txt_valor_umidade,
            txt_valor_vento, txt_valor_pressao, txt_cidade, txt_condicao;
    ImageView img_icon_clima;

    LocationManager locationManager;
    LocationListener locationListener;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();
        finderNewCity();
        changeBackground();

    }

    private void initComponents() {
        txt_temperatura = findViewById(R.id.txt_temperatura);
        txt_valor_sensacao_term = findViewById(R.id.txt_valor_sensacao_term);
        txt_valor_umidade = findViewById(R.id.txt_valor_umidade);
        txt_valor_vento = findViewById(R.id.txt_valor_vento);
        txt_condicao = findViewById(R.id.txt_condicao);
        txt_valor_pressao = findViewById(R.id.txt_valor_pressao);
        txt_cidade = findViewById(R.id.txt_cidade);
        img_icon_clima = findViewById(R.id.img_icon_clima);

        cityFinder = findViewById(R.id.cityFinder);

        backgroundLayout = findViewById(R.id.background_layout);
    }

    private void changeBackground(){
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        if (hourOfDay >= 6 && hourOfDay < 18) {
            // é dia
            backgroundLayout.setBackgroundResource(R.drawable.background_dia);
        } else {
            // é noite
            backgroundLayout.setBackgroundResource(R.drawable.background_noite);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent mIntent=getIntent();
        String city= mIntent.getStringExtra("City");
        if(city!=null)
        {
            getClimaNovaCidade(city);
        }
        else
        {
            getClimaLocalizacaoAtual();
        }

    }
    private void finderNewCity(){
        cityFinder.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getAction() == KeyEvent.KEYCODE_ENTER){
                    // Ocultar o teclado virtual
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(cityFinder.getWindowToken(), 0);
                    // Realizar a busca
                    String cidade = cityFinder.getText().toString();
                    getClimaNovaCidade(cidade);
                    return true;
                }
                return false;
            }
        });

        //Detectar quando o usuário clicar no ícone de busca
        //verificando se a ação do toque é ACTION_UP e se a coordenada X do toque é maior do que a largura do EditText menos a largura do ícone de busca (obtido usando getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()).
        cityFinder.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP &&
                        event.getRawX() >= (cityFinder.getRight() - cityFinder.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    // Realizar a busca
                    String cidade = cityFinder.getText().toString();
                    getClimaNovaCidade(cidade);
                    return true;
                }
                return false;
            }
        });
    }

    private void getClimaNovaCidade(String cidade){
        RequestParams params=new RequestParams();
        params.put("q",cidade);
        params.put("appid",appid);
        letsdoSomeNetworking(params);

    }

    private void getClimaLocalizacaoAtual() {

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                String Latitude = String.valueOf(location.getLatitude());
                String Longitude = String.valueOf(location.getLongitude());

                RequestParams params =new RequestParams();
                params.put("lat" ,Latitude);
                params.put("lon",Longitude);
                params.put("appid",appid);
                letsdoSomeNetworking(params);


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                //LocationListener.super.onStatusChanged(provider, status, extras);
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
                //LocationListener.super.onProviderEnabled(provider);
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
        locationManager.requestLocationUpdates(Location_Provider, MIN_TIME, MIN_DISTANCE, locationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==REQUEST_CODE){

            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(MainActivity.this, "Locationget Succesffully", Toast.LENGTH_SHORT).show();
                getClimaLocalizacaoAtual();
            }else{
                //Permissoes negadas
            }
        }
    }

    private void letsdoSomeNetworking(RequestParams params){

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url,params,new JsonHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Toast.makeText(MainActivity.this,"Data Get Success",Toast.LENGTH_SHORT).show();

                WeatherData weatherData=WeatherData.fromJson(response);
                atualizarUI(weatherData);
                //super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
               // super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    private void atualizarUI(WeatherData weather){

        txt_temperatura.setText(weather.getTemperatura());
        txt_cidade.setText(weather.getCidade());
        txt_condicao.setText(weather.getTipoClima());
        txt_valor_vento.setText(weather.getVento());
        txt_valor_umidade.setText(weather.getUmidade());
        txt_valor_sensacao_term.setText(weather.getSensacaoTermica());
        txt_valor_pressao.setText(weather.getPressao());

        int resourceID=getResources().getIdentifier(weather.getIconClima(),"drawable",getPackageName());
        img_icon_clima.setImageResource(resourceID);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(locationManager!=null){
            locationManager.removeUpdates(locationListener);
        }
    }

}