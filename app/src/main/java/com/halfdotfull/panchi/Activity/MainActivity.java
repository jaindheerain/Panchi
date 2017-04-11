package com.halfdotfull.panchi.Activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.halfdotfull.panchi.FakeCallReceiver;
import com.halfdotfull.panchi.R;
import com.halfdotfull.panchi.Service.MessageService;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    CardView message,contacts,fakecall,police;
    SharedPreferences mSharedPreferences;
    String number,serial;
    //SmsManager smsManager = SmsManager.getDefault();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        message= (CardView) findViewById(R.id.cardView2);

        contacts= (CardView) findViewById(R.id.cardView1);

        fakecall= (CardView) findViewById(R.id.cardView3);

        police= (CardView) findViewById(R.id.cardView4);

        fakecall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.add(Calendar.SECOND, 10);
                long currentFakeTime =  calendar.getTimeInMillis();

                String fakeNameEntered = "Papa";
                String fakeNumberEntered = "9412168792";
                setUpAlarm(currentFakeTime, fakeNameEntered, fakeNumberEntered);
            }
        });
        contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,Contact.class);
                startActivity(i);
            }
        });
        police.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+"1091"));
                startActivity(intent);
            }
        });
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog.Builder builder = new MaterialDialog.Builder(MainActivity.this)
                        .title(R.string.input)
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input("Enter Here", null, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                mSharedPreferences=getSharedPreferences("panchi", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor=mSharedPreferences.edit();
                                editor.putString("Message",input.toString());
                                editor.apply();
                            }
                        });
                MaterialDialog dialog = builder.build();
                dialog.show();
            }
        });
    }
    public void setUpAlarm(long selectedTimeInMilliseconds, String fakeName, String fakeNumber){

        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, FakeCallReceiver.class);

        intent.putExtra("FAKENAME", fakeName);
        intent.putExtra("FAKENUMBER", fakeNumber);

        PendingIntent fakePendingIntent = PendingIntent.getBroadcast(this, 0,  intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, selectedTimeInMilliseconds, fakePendingIntent);
        Toast.makeText(getApplicationContext(), "Your fake call time has been set", Toast.LENGTH_SHORT).show();
    }
    private void startService() {
        Intent intent=new Intent(MainActivity.this,MessageService.class);
        intent.putExtra("number",number);
        intent.putExtra("serial",serial);
        startService(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startService();
    }

}
