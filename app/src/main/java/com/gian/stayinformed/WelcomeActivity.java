package com.gian.stayinformed;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import Adaptadores.SliderPagerAdapter;
import Clases.SlideWelcome;

public class WelcomeActivity extends AppCompatActivity {

    private List<SlideWelcome> lstSlides;
    private ViewPager sliderPager2;
    private LinearLayout mDotLinearLayout;
    private TextView[] mDots;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        checkSharedPreference();
        getValues();
        setSlidePager();
    }

    private void checkSharedPreference() {
        SharedPreferences prefs = getSharedPreferences("checkLoginPrefs", MODE_PRIVATE);
        int firstTime = prefs.getInt("firstTime", 1);//"No name defined" is the default value.
        if(firstTime == 0){
            Intent i = new Intent(this,MainActivity.class);
            startActivity(i);
            this.finish();
        }
    }

    private void setSharedPreference() {
        SharedPreferences.Editor editor = getSharedPreferences("checkLoginPrefs", MODE_PRIVATE).edit();
        editor.putInt("firstTime", 0);
        editor.apply();
    }

    private void getValues() {
        sliderPager2=   findViewById(R.id.sliderPager);
        mDotLinearLayout = findViewById(R.id.dotsLayout);
    }


    private void setSlidePager() {
        lstSlides = new ArrayList<>();

        lstSlides.add(new SlideWelcome(R.drawable.picture1,null));
        lstSlides.add(new SlideWelcome(R.drawable.picture2,null));
        lstSlides.add(new SlideWelcome(R.drawable.picture3,null));


        SliderPagerAdapter adapter = new SliderPagerAdapter(this,lstSlides);

        sliderPager2.setAdapter(adapter);
        addDotsIndicator(0);
        sliderPager2.addOnPageChangeListener(viewListener);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new WelcomeActivity.sliderTimer(),4000,6000);
        //indicator.setupWithViewPager(sliderPager2,true);



    }


    public void addDotsIndicator(int position){
        mDots = new TextView[3];
        mDotLinearLayout.removeAllViews();

        for(int i = 0; i < mDots.length; i++){
            mDots[i]= new TextView (this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.gray));
            mDotLinearLayout.addView(mDots[i]);
        }

        if(mDots.length>0){
            mDots[position].setTextColor(getResources().getColor(R.color.white));
        }


    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public void goToMainActivity(View view) {
        if(checkInternetConnection()){
            setSharedPreference();
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            this.finish();
        }else{
            Toast.makeText(this,"Es necesario tener una conexión a Internet para continuar",Toast.LENGTH_LONG).show();
        }

    }

    private boolean checkInternetConnection() {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

            connected = true;
        } else{
            connected = false;
        }

        return connected;

    }


    class sliderTimer extends TimerTask {

        @Override
        public void run() {
            WelcomeActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (sliderPager2.getCurrentItem() < lstSlides.size() - 1) {
                        sliderPager2.setCurrentItem(sliderPager2.getCurrentItem() + 1);
                    } else {
                        sliderPager2.setCurrentItem(0);
                    }


                }
            });
        }


    }
}