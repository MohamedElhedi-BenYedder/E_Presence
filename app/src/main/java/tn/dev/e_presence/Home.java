package tn.dev.e_presence;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Home extends AppCompatActivity {
    private BottomAppBar bottomAppBar;
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        SetUpBottomAppBarMenu();

    }
        private void SetUpBottomAppBarMenu( )
        {
            //find id
            bottomAppBar=findViewById(R.id.bnb);
            //click event over Bottom bar menu item
            bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.miDashboard:
                            startActivity(new Intent(getApplicationContext(),Dashboard.class));
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


}