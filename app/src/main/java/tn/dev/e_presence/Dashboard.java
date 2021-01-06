package tn.dev.e_presence;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.github.jhonnyx2012.horizontalpicker.DatePickerListener;
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.joda.time.DateTime;

public class Dashboard extends AppCompatActivity implements DatePickerListener {
    private ListView lv_Session;
    private SessionAdapter Adapter;
    private ListOfSessions SessionList;
    private HorizontalPicker picker;
    private BottomAppBar bottomAppBar;
    private BottomNavigationView bottomNavigationView;
    private int REQUEST_CAMERA = 1;
    private int Cur_pos;
    private String Scane_res = "chaine";


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        UpdateListofSessions();
        SetDatePicker();
        SetUpBottomAppBarMenu();
        FloatingActionButton fab =(FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this::AddSession);


    }

    private void UpdateListofSessions() {
        lv_Session = findViewById(R.id.lv_session);
        SessionList = new ListOfSessions();
        Adapter = new SessionAdapter(Dashboard.this, SessionList);
        lv_Session.setAdapter(Adapter);
        lv_Session.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(Dashboard.this, "testclick", Toast.LENGTH_SHORT).show();
                Cur_pos = position;
                if (ActivityCompat.checkSelfPermission(Dashboard.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Dashboard.this, ScanActivity.class);

                    Adapter.notifyDataSetChanged();
                    startActivityForResult(intent, 0);
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                        Toast.makeText(Dashboard.this,
                                "Camera permission is needed",
                                Toast.LENGTH_SHORT).show();
                    }
                    ActivityCompat.requestPermissions(Dashboard.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                }

            }
        });
        Log.v("qr_res", Scane_res);
        //listen for incoming messages
        Bundle incommingMessages = getIntent().getExtras();
        if (incommingMessages != null) {
            // capture incomming data
            String mes_start = incommingMessages.getString("start");
            String mes_end = incommingMessages.getString("end");
            String mes_classroom = incommingMessages.getString("classroom");
            String mes_teacher = incommingMessages.getString("teacher");
            String mes_subject = incommingMessages.getString("subject");
            String mes_group = incommingMessages.getString("group");
            String mes_date = incommingMessages.getString("date");
            String mes_qrcode = incommingMessages.getString("qrcode");
            boolean mes_presential = incommingMessages.getBoolean("presential");
            //create new session object
            Session S = new Session(1, mes_date, mes_start, mes_end, mes_classroom, mes_group, mes_teacher, mes_subject, mes_presential,mes_qrcode);
            //add session to the list
            SessionList.getSessionList().add(S);
            // update adapter
            Adapter.notifyDataSetChanged();

        }
        ;
    }

    private void SetDatePicker() {
        picker = (HorizontalPicker) findViewById(R.id.datePicker);
        picker.setListener(this)
                .setDays(120)
                .setOffset(7)
                .setDateSelectedColor(Color.BLACK)
                .setDateSelectedTextColor(Color.WHITE)
                .setMonthAndYearTextColor(Color.BLACK)
                .setTodayButtonTextColor(Color.WHITE)
                .setTodayDateTextColor(Color.WHITE)
                .setTodayDateBackgroundColor(Color.GRAY)
                .setUnselectedDayTextColor(Color.BLACK)
                .setDayOfWeekTextColor(Color.BLACK)
                .setUnselectedDayTextColor(getResources().getColor(R.color.primaryTextColor))
                .showTodayButton(false)
                .init();
        picker.setBackgroundColor(getResources().getColor(R.color.contentBodyColor));
        picker.setDate(new DateTime());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void SetUpBottomAppBarMenu() {
        //find id
        bottomAppBar = findViewById(R.id.bnb);
        //bottomAppBar.getMenu().getItem(1).setIconTintList(getColorStateList(R.color.c2));
        //set bottom bar to Action bar as it is similar like Toolbar
        //bottomAppBar.replaceMenu(R.menu.bottom_app_bar_secondary_menu);
        //click event over Bottom bar menu item
        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.miHome:
                        startActivity(new Intent(getApplicationContext(), Home.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.miProfile:
                        startActivity(new Intent(getApplicationContext(), Profile.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.miSettings:
                        startActivity(new Intent(getApplicationContext(), Settings.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return true;

            }


        });
    }

    //Scan Fonction
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 0) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra("barcode");
                    //res.setText(barcode.displayValue);
                    Scane_res = barcode.displayValue;
                    Log.v("qr_res", Scane_res);
                    if (Scane_res.equals(Adapter.getItem(Cur_pos).getQrstring())) {
                        Adapter.getItem(Cur_pos).setFlag(true);
                        Adapter.notifyDataSetChanged();
                    }

                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //Camera Permission Function
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Intent intent = new Intent(this, ScanActivity.class);

                startActivityForResult(intent, 0);
            } else {
                Toast.makeText(this, "Permission was not granted", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }
    }

    @Override
    public void onDateSelected(DateTime dateSelected) {
        Toast.makeText(this, dateSelected.toString("dd MMMM yyyy"), Toast.LENGTH_SHORT).show();


    }

    private  void AddSession(View v)
    {

        Intent intent =new Intent(Dashboard.this,CreateSession.class);
        startActivity(intent);
        finish();
    }
   /* protected void onListItemClick(ListView l, View v, int position, long id) {
        startActivity(new Intent(getApplicationContext(),ScanActivity.class));

    }*/
}
