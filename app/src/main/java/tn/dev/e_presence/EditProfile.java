package tn.dev.e_presence;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EditProfile extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner gender;
    private TextView btn_ok;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        btn_ok=findViewById(R.id.save_btn);
        gender=findViewById(R.id.s_gender);
        List<String> groupestatique= new ArrayList<String>();groupestatique.add("");
        groupestatique.add("Female");
        groupestatique.add("Male");
        ArrayAdapter<String> group_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,groupestatique);
        group_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(group_adapter);
        gender.setOnItemSelectedListener(this);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(EditProfile.this,Home.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}