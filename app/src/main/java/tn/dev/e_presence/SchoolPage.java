package tn.dev.e_presence;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import static tn.dev.e_presence.GV.getSchool;
import static tn.dev.e_presence.GV.getUser;

public class SchoolPage extends AppCompatActivity {
    private BottomAppBar bottomAppBar;
    private final String UserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String SchoolId;
    private TextView tv_display_name;
    private TextView tv_description;
    private TextView tv_full_name;
    private TextView tv_welcome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_page);
        findViews();
        welcomeUser();
        listenForIncommingMessages();
        displaySchoolInformations();
        setUpBottomAppBarMenu();

    }
    private void setUpBottomAppBarMenu()
    {
        //find id
        bottomAppBar=findViewById(R.id.bnb);
        // bottomAppBar.getMenu().getItem(0).setIconTintList(getColorStateList(R.color.c2));

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
                    case R.id.miHome:
                        startActivity(new Intent(getApplicationContext(),Home.class));
                        overridePendingTransition(0,0);
                        return true;

                }
                return true;
            }


        });
    }
    void welcomeUser()
    {
        getUser(UserId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                tv_welcome.setText("Welcome "+documentSnapshot.getString("displayName"));
            }
        });
    }
    void onStudentClick(View V)
    {
        Intent intent=new Intent(SchoolPage.this,MemberList.class);
        intent.putExtra("key","studentIN");
        intent.putExtra("path","School/schoolName");
        startActivity(intent);
        finish();
    }
    void onTeacherClick(View V)
    {
        Intent intent=new Intent(SchoolPage.this,MemberList.class);
        intent.putExtra("key","studentIN");
        intent.putExtra("path","School/schoolName");
        startActivity(intent);
        finish();
    }
    void listenForIncommingMessages()
    {
        //listen for incoming messages
        Bundle incommingMessages =getIntent().getExtras();
       SchoolId =incommingMessages.getString("ID","0");
    }
    void displaySchoolInformations()
    {
        getSchool(SchoolId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                tv_description.setText(documentSnapshot.getString("Description"));
                tv_display_name.setText(documentSnapshot.getString("DisplayName"));
                tv_full_name.setText(documentSnapshot.getString("FullName"));
            }
        });
    }
    void findViews()
    {
        tv_description=findViewById(R.id.tv_description);
        tv_display_name=findViewById(R.id.tv_display_name);
        tv_full_name=findViewById(R.id.tv_full_name);
        tv_welcome=findViewById(R.id.tv_welcome_user);
    }
}