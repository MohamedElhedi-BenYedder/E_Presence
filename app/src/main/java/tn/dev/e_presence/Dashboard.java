package tn.dev.e_presence;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.github.jhonnyx2012.horizontalpicker.DatePickerListener;
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.joda.time.DateTime;

public class Dashboard extends AppCompatActivity implements DatePickerListener{
    private ListView lv_Session;
    private SessionAdapter Adapter;
    private ListOfSessions SessionList;
    private HorizontalPicker picker;
    private BottomAppBar bottomAppBar;
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        UpdateListofSessions();
        SetDatePicker();
        SetUpBottomAppBarMenu();








    }
    private void UpdateListofSessions()
    {
        lv_Session =findViewById(R.id.lv_session);
        SessionList = new ListOfSessions();
        Adapter = new SessionAdapter(Dashboard.this,SessionList);
        lv_Session.setAdapter(Adapter);
        //listen for incoming messages
        Bundle incommingMessages =getIntent().getExtras();
        if(incommingMessages != null)
        {
            // capture incomming data
            String mes_start=incommingMessages.getString("start");
            String mes_end=incommingMessages.getString("end");
            String mes_classroom=incommingMessages.getString("classroom");
            String mes_teacher=incommingMessages.getString("teacher");
            String mes_subject=incommingMessages.getString("subject");
            String mes_group=incommingMessages.getString("group");
            String mes_date=incommingMessages.getString("date");
            boolean mes_presential=incommingMessages.getBoolean("presential");
            //create new session object
            Session S= new Session(1,mes_date,mes_start,mes_end,mes_classroom,mes_group,mes_teacher,mes_subject,mes_presential);
            //add session to the list
            SessionList.getSessionList().add(S);
            // update adapter
            Adapter.notifyDataSetChanged();
        };
    }

        private void SetDatePicker()
        {
            picker= (HorizontalPicker) findViewById(R.id.datePicker);
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
    private void SetUpBottomAppBarMenu( )
    {
        //find id
        bottomAppBar=findViewById(R.id.bnb);
        //set bottom bar to Action bar as it is similar like Toolbar
        //bottomAppBar.replaceMenu(R.menu.bottom_app_bar_secondary_menu);
        //click event over Bottom bar menu item
        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.miHome:
                        startActivity(new Intent(getApplicationContext(),Home.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.miProfile:
                        startActivity(new Intent(getApplicationContext(),Profile.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.miSettings:
                        startActivity(new Intent(getApplicationContext(),Settings.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return true;

            }


        });
    }

    @Override
    public  void onDateSelected(DateTime dateSelected){
        Toast.makeText(this,dateSelected.toString(),Toast.LENGTH_SHORT).show();


    }
}