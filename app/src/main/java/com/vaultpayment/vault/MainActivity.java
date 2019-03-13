package com.vaultpayment.vault;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ImageView signal;
    private TextView cardNumber;
    private Button btnTestMain;


    private TextView mTextMessage;
    private TextView paymentdesc;
    private FloatingActionButton btnAdd;
    int MY_PERMISSIONS_REQUEST_NFC;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    btnAdd.hide();
                    signal.setVisibility(View.VISIBLE);
                    paymentdesc.setText(R.string.pay_desc);
                    paymentdesc.setVisibility(View.VISIBLE);
                    cardNumber.setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.payment_methods_title);
                    btnAdd.show();
                    signal.setVisibility(View.INVISIBLE);
                    paymentdesc.setText("");
                    paymentdesc.setVisibility(View.INVISIBLE);
                    cardNumber.setVisibility(View.INVISIBLE);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_history);
                    btnAdd.hide();
                    signal.setVisibility(View.INVISIBLE);
                    paymentdesc.setText("");
                    paymentdesc.setVisibility(View.INVISIBLE);
                    cardNumber.setVisibility(View.INVISIBLE);
                    return true;
            }
            return false;
        }
    };

    public void SignalActive(boolean isActive){
        if(isActive) {
            signal.setColorFilter(R.color.neon_blue);
        }
        else{
            signal.setColorFilter(R.color.light_grey_n_f_c_near);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().setTitle("");
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setLogo(R.mipmap.vault_logo);
        //getSupportActionBar().setDisplayUseLogoEnabled(true);
        setContentView(R.layout.activity_main);
        AppCompatActivity thisActivity = this;
        if (ContextCompat.checkSelfPermission(thisActivity, Manifest.permission.NFC)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(thisActivity, new String[]{ Manifest.permission.NFC},
                    MY_PERMISSIONS_REQUEST_NFC);
        }
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        btnAdd = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        btnAdd.hide();
        paymentdesc = (TextView) findViewById(R.id.payscreendescription);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        signal = (ImageView) findViewById(R.id.signal);
        cardNumber = (TextView) findViewById(R.id.card_number);

    }

}
