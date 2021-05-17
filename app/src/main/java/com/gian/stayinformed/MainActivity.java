package com.gian.stayinformed;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import Adapters.PaisesFavAdapter;
import ProjectClasses.Covid19ApiSingleton;
import ProjectClasses.DisplayAlertDialog;
import covid19apiClasses.ByCountry;
import covid19apiClasses.Countries;
import CoronaLmaoNinjaApiClasses.CustomSpinnerAdapter;
import ProjectClasses.PaisesFavoritosData;
import covid19apiClasses.Summary;
import Interfaces.CovidApi;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements PaisesFavAdapter.OnItemClickListener {
    @BindView(R.id.buscar_paises_spinner) Spinner spinner_buscar_paises;
    @BindView(R.id.casosActivosText) TextView casosActivosTxt;
    @BindView(R.id.casosConfirmadosText) TextView casosConfirmadosTxt;
    @BindView(R.id.casosMuertesText) TextView casosMuertesTxt;
    @BindView(R.id.nuevosCasosText) TextView nuevosCasosTxt;
    @BindView(R.id.nuevasMuertesText) TextView nuevasMuertesTxt;
    @BindView(R.id.card) CardView card;
    @BindView(R.id.recycler_buscar_paises) RecyclerView recyclerView;
    @BindView(R.id.pais) TextView pais;
    private static final CovidApi recipeServiceCovid19Api = Covid19ApiSingleton.getService();
    DisplayAlertDialog displayAlertDialog;
    private String casosConfirmados;
    private String casosActivos;
    private String muertes;
    private String nuevosCasos;
    private String nuevasMuertes;
    private String spinnerSelectedItem;
    private String keyPaises;
    private String key;
    private List<PaisesFavoritosData> mlist = new ArrayList<>();
    private PaisesFavAdapter adapter;
    ProgressDialog progressDialog;
    private InterstitialAd mInterstitialAd;
    private int clicks;
    private int dismissShare;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initializeInterstitial();
        executeMethodsAccordingInternetConnection();
    }

    private void executeMethodsAccordingInternetConnection() {
        if(checkInternetConnection()){
            progressDialog = ProgressDialog.show(this, "Wait...", "Loading data...",true);
            progressDialog.show();
            checkearSharedPreference();
            cargarFavoritos();
            initializeUI();
            getCountries();
        }else{
            showAlertDialogOnResponse();
        }
    }

    public void showAlertDialogOnResponse() {
        if(!checkInternetConnection()){
            new AlertDialog.Builder(this)
                    .setTitle("Internet connection")
                    .setMessage("You must have an internet connection to be able to see the statistics of the other countries")
                    .setPositiveButton(R.string.Continuar, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.Salir, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.this.finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

    }


    private void initializeInterstitial() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                createPersonalizedAd();
            }
        });
    }

    private void createPersonalizedAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        createInterstisialAd(adRequest);
    }

    private void createInterstisialAd(AdRequest adRequest) {
        //original ca-app-pub-4185358034958198/9153246696
        //Prueba ca-app-pub-3940256099942544/1033173712
        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                Log.i("---AdMob", "onAdLoaded");
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        if(dismissShare==1){
                            Log.d("TAG", "The ad was dismissed.");
                            Intent myIntent = new Intent (Intent.ACTION_SEND);
                            myIntent.setType("text/plain");
                            String shareBody = "Country: " + pais.getText().toString()+ "\n"
                                    +"Confirmed: " + casosConfirmadosTxt.getText().toString() + "\n"
                                    +"Active: " + casosActivosTxt.getText().toString() + "\n"
                                    +"Deaths: " + casosMuertesTxt.getText().toString() + "\n"
                                    +"New cases: " + nuevosCasosTxt.getText().toString() + "\n"
                                    +"New deaths: " +nuevasMuertesTxt.getText().toString();
                            String shareSubject = pais.getText().toString();
                            myIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
                            myIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                            startActivity(Intent.createChooser(myIntent,"Share"));
                            dismissShare=0;
                        }
                        createPersonalizedAd();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        Log.d("TAG", "The ad failed to show.");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        mInterstitialAd = null;
                        Log.d("TAG", "The ad was shown.");
                    }
                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.i("---AdMob", loadAdError.getMessage());
                mInterstitialAd = null;
            }
        });

    }
    private boolean checkInternetConnection() {
            boolean connected;
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

                connected = true;
            } else{
                connected = false;
            }

            return connected;

        }

    private void cargarFavoritos() {

            DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("PaisesFavoritos").child(key);
            database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        PaisesFavoritosData paisesFavoritosData = dataSnapshot.getValue(PaisesFavoritosData.class);
                        getMyFlag(paisesFavoritosData.getNombre(),paisesFavoritosData);
                    }
                    generarRecycler();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }




    private void getMyFlag(String nombre, PaisesFavoritosData paisesFavoritosData) {
        String URL = "https://corona.lmao.ninja/v2/countries";
        StringRequest request = new StringRequest(Request.Method.GET, URL,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String theFlag;
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for(int i = 0; i <jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                if(nombre.equals(jsonObject.getString("country"))){
                                    JSONObject jsonObject1 = jsonObject.getJSONObject("countryInfo");
                                    theFlag = jsonObject1.getString("flag");
                                    if(theFlag != null){
                                        mlist.add(new PaisesFavoritosData(paisesFavoritosData.getCasosActivos(),
                                                paisesFavoritosData.getCasosConfirmados(),
                                                paisesFavoritosData.getMuertes(),
                                                paisesFavoritosData.getNombre(),paisesFavoritosData.getNuevasMuertes()
                                                ,paisesFavoritosData.getNuevosCasos(),theFlag));
                                    }else{
                                        mlist.add(new PaisesFavoritosData(paisesFavoritosData.getCasosActivos(),
                                                paisesFavoritosData.getCasosConfirmados(),
                                                paisesFavoritosData.getMuertes(),
                                                paisesFavoritosData.getNombre(),paisesFavoritosData.getNuevasMuertes()
                                                ,paisesFavoritosData.getNuevosCasos(),""));
                                    }
                                    generarRecycler();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);

    }

    private void generarRecycler() {
        adapter = new PaisesFavAdapter(MainActivity.this,mlist);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this,GridLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new PaisesFavAdapter.OnItemClickListener() {
            @Override
            public void onitemClick(int position) {

            }


            @Override
            public void onDelateClick(int position) {
                removeItem(position);

            }
            @Override
            public void onShareClick(int position) {
                Intent myIntent = new Intent (Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String shareBody = "Country: " + mlist.get(position).getNombre()+ "\n"
                        +"Confirmed: " + mlist.get(position).getCasosConfirmados() + "\n"
                        +"Active: " + mlist.get(position).getCasosActivos() + "\n"
                        +"Deaths: " + mlist.get(position).getMuertes() + "\n"
                        +"New cases: " + mlist.get(position).getNuevosCasos() + "\n"
                        +"New deaths: " + mlist.get(position).getNuevasMuertes();
                String shareSubject = pais.getText().toString();
                myIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
                myIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(myIntent,"Share"));

            }

            private void removeItem(int position) {
                eliminarItemDeFirebase(position);
                mlist.remove(position);
                adapter.notifyItemRemoved(position);
            }

            private void eliminarItemDeFirebase(int position) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Query applesQuery = ref.child("PaisesFavoritos").child(key).orderByChild("nombre").equalTo(mlist.get(position).getNombre());

                applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                            appleSnapshot.getRef().removeValue();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    private void checkearSharedPreference() {
        SharedPreferences prefs = getSharedPreferences("shared", MODE_PRIVATE);
        if(!prefs.contains("key")){
            key = FirebaseDatabase.getInstance().getReference("Paises").push().getKey();
            SharedPreferences.Editor editor = getSharedPreferences("shared", MODE_PRIVATE).edit();
            editor.putString("key",key);
            editor.apply();
        }else{
            SharedPreferences prefsGet = getSharedPreferences("shared", MODE_PRIVATE);
            key = prefsGet.getString("key","");
        }
    }

    private void initializeUI() {
        card.setBackgroundResource(R.drawable.card_location);
    }

    private void getCountries(){
        Call<LinkedList<Countries>> callCountries = recipeServiceCovid19Api.getCountries();
        callCountries.enqueue(new Callback<LinkedList<Countries>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<LinkedList<Countries>> call, Response<LinkedList<Countries>> response) {

                setearSpinner(response);
                spinner_buscar_paises.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        clicks++;
                        if(clicks == 4){
                            if(mInterstitialAd!=null){
                                mInterstitialAd.show(MainActivity.this);
                                clicks=0;
                            }
                        }


                        Call<List<ByCountry>> callByCountry = recipeServiceCovid19Api.getByCountry
                                ("https://api.covid19api.com/live/country/"+
                                        spinner_buscar_paises.getItemAtPosition(position)+"/"+"status"+"/"+"confirmed");

                        callByCountry.enqueue(new Callback<List<ByCountry>>() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onResponse(Call<List<ByCountry>> call, Response<List<ByCountry>> response) {
                                if(response.isSuccessful()){
                                    if(response.body().size() > 0 ){
                                        setearTextosCardViewPaises(response);

                                        Call<Summary> callSummary= recipeServiceCovid19Api.getSummary("https://api.covid19api.com/summary");

                                        callSummary.enqueue(new Callback<Summary>() {
                                            @Override
                                            public void onResponse(Call<Summary> call, Response<Summary> response) {
                                                if(response.isSuccessful()){
                                                    setearTextosCardViewPaisesConDatosNuevos(response);
                                                }
                                            }

                                            private void setearTextosCardViewPaisesConDatosNuevos(Response<Summary> response) {
                                                for(int i = 0; i < response.body().getCountries().size(); i++){
                                                    if(response.body().getCountries().get(i).getCountry()
                                                            .equals(spinner_buscar_paises.getItemAtPosition(position))){
                                                        pais.setText(spinner_buscar_paises.getItemAtPosition(position).toString());
                                                        nuevosCasos = String.valueOf(response.body().getCountries().get(i).getNewConfirmed());
                                                        nuevosCasosTxt.setText(nuevosCasos);
                                                        nuevasMuertes = String.valueOf(response.body().getCountries().get(i).getNewDeaths());
                                                        nuevasMuertesTxt.setText(nuevasMuertes);
                                                    }
                                                }
                                            }


                                            @Override
                                            public void onFailure(Call<Summary> call, Throwable t) {

                                            }
                                        });
                                    }else{
                                        casosActivosTxt.setText("0");
                                        casosConfirmadosTxt.setText("0");
                                        casosMuertesTxt.setText("0");
                                        nuevosCasosTxt.setText("0");
                                        nuevasMuertesTxt.setText("0");
                                        casosActivosTxt.setGravity(Gravity.CENTER);
                                    }
                                }

                            }

                            private void setearTextosCardViewPaises(Response<List<ByCountry>> response) {
                                ByCountry countrySelected= new ByCountry();
                                countrySelected = response.body().get(response.body().size()-1);
                                casosActivos = String.valueOf(countrySelected.getActive());
                                casosActivosTxt.setText(casosActivos);
                                casosConfirmados = String.valueOf(countrySelected.getConfirmed());
                                casosConfirmadosTxt.setText(casosConfirmados);
                                muertes = String.valueOf(countrySelected.getDeaths());
                                casosMuertesTxt.setText(muertes);
                            }

                            @Override
                            public void onFailure(Call<List<ByCountry>> call, Throwable t) {
                                System.out.println("The response failed");

                            }
                        });
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            private void setearSpinner(Response<LinkedList<Countries>> response) {
                List<String> spinnerArray =  new ArrayList<String>();

                for(int i = 0; i < response.body().size(); i++){
                    spinnerArray.add(response.body().get(i).getCountry());
                }
                spinnerArray.sort(String::compareToIgnoreCase);
                CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(MainActivity.this, android.R.layout.simple_spinner_item, spinnerArray);
                spinner_buscar_paises.setAdapter(customSpinnerAdapter);
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<LinkedList<Countries>> call, Throwable t) {

            }
        });

    }

    public void agregarAfavoritos(View view) {
        if(checkInternetConnection()){

            almacenarValoresEnFirebase(key);
        }

    }

    private void almacenarValoresEnFirebase(String key) {
        spinnerSelectedItem = spinner_buscar_paises.getSelectedItem().toString();
        keyPaises = FirebaseDatabase.getInstance().getReference("key").push().getKey();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().
                child("PaisesFavoritos").child(key);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean existe = false;
                for (DataSnapshot snap: snapshot.getChildren()){
                    if(String.valueOf(snap.child("nombre").getValue()).equals(spinnerSelectedItem)){
                        existe = true;
                        Toast.makeText(MainActivity.this,"you have already added the country: " + spinnerSelectedItem + " " + "to your favorites list" , Toast.LENGTH_SHORT).show();
                    }
                }

                if(!existe){
                    DatabaseReference nombreDelPaisRef = FirebaseDatabase.getInstance().getReference().child("PaisesFavoritos").child(key).child(keyPaises).child("nombre");
                    DatabaseReference casosActivosRef= FirebaseDatabase.getInstance().getReference().child("PaisesFavoritos").child(key).child(keyPaises).child("casosActivos");
                    DatabaseReference casosConfirmadosRef= FirebaseDatabase.getInstance().getReference().child("PaisesFavoritos").child(key).child(keyPaises).child("casosConfirmados");
                    DatabaseReference muertesRef= FirebaseDatabase.getInstance().getReference().child("PaisesFavoritos").child(key).child(keyPaises).child("muertes");
                    DatabaseReference nuevosCasosRef= FirebaseDatabase.getInstance().getReference().child("PaisesFavoritos").child(key).child(keyPaises).child("nuevosCasos");
                    DatabaseReference nuevasMuertesRef= FirebaseDatabase.getInstance().getReference().child("PaisesFavoritos").child(key).child(keyPaises).child("nuevasMuertes");
                    nombreDelPaisRef.setValue(spinnerSelectedItem);
                    casosActivosRef.setValue(casosActivos);
                    casosConfirmadosRef.setValue(casosConfirmados);
                    muertesRef.setValue(muertes);
                    nuevosCasosRef.setValue(nuevosCasos);
                    nuevasMuertesRef.setValue(nuevasMuertes);
                    Toast.makeText(MainActivity.this,"Added!",Toast.LENGTH_SHORT).show();
                    getMyFlagFavorite(spinnerSelectedItem);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private void getMyFlagFavorite(String nombre) {
        String URL = "https://corona.lmao.ninja/v2/countries";
        StringRequest request = new StringRequest(Request.Method.GET, URL,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String theFlag;
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for(int i = 0; i <jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                if(nombre.equals(jsonObject.getString("country"))){
                                    JSONObject jsonObject1 = jsonObject.getJSONObject("countryInfo");
                                    theFlag = jsonObject1.getString("flag");
                                    mlist.add(new PaisesFavoritosData(casosActivos
                                            ,casosConfirmados,
                                            muertes,spinnerSelectedItem
                                            ,nuevasMuertes,nuevosCasos,theFlag));
                                    generarRecycler();

                                }


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);

    }

    @Override
    public void onitemClick(int position) {

    }

    @Override
    public void onDelateClick(int position) {

    }

    @Override
    public void onShareClick(int position) {

    }

    public void compartirEnRedes(View view) {
        if (mInterstitialAd!=null) {
            mInterstitialAd.show(this);
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
            Intent myIntent = new Intent (Intent.ACTION_SEND);
            myIntent.setType("text/plain");
            String shareBody = "Country: " + pais.getText().toString()+ "\n"
                    +"Confirmed: " + casosConfirmadosTxt.getText().toString() + "\n"
                    +"Active: " + casosActivosTxt.getText().toString() + "\n"
                    +"Deaths: " + casosMuertesTxt.getText().toString() + "\n"
                    +"New cases: " + nuevosCasosTxt.getText().toString() + "\n"
                    +"New deaths: " +nuevasMuertesTxt.getText().toString();
            String shareSubject = pais.getText().toString();
            myIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
            myIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(myIntent,"Share"));
        }
        dismissShare = 1;

    }
}