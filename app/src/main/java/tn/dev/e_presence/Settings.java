package tn.dev.e_presence;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.text.Html;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Settings extends AppCompatActivity {
    private FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    private FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
    private Switch darkModeSwitch;
    private TextView tv_change_password;
    private TextView tv_name;
    private TextView tv_edit;
    private CircleImageView cv_photo;
    private RelativeLayout rl_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        findViews();
        setDarkModeSwitch();
        ChangePassword();
        try {
            loadCurentUserInformations();
        }catch (Exception e)
        {

        }



    }
    private void setDarkModeSwitch(){

        darkModeSwitch.setChecked(new DarkModePrefManager(this).isNightMode());
        darkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DarkModePrefManager darkModePrefManager = new DarkModePrefManager(Settings.this);
                darkModePrefManager.setDarkMode(!darkModePrefManager.isNightMode());
                if(darkModePrefManager.isNightMode()){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                GV.loadCurentUserInformations();
                recreate();
            }
        });
    }
    public void onClick(View v) {
        Logout(v);
        EditProfile(v);

    }
    private void Logout(View v)
    {
        if (v.getId() == R.id.btn_logout) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            startActivity(new Intent(Settings.this, Welcome.class));
                            finish();
                        }
                    });
        }

    }
    private void EditProfile(View v)
    {
        if (v.getId() ==  R.id.btn_editprofile || v.getId() == R.id.rl_edit )
        {
            startActivity(new Intent(Settings.this,EditProfile.class));

        }

    }
    private void ChangePassword()
    {
        tv_change_password.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                //final EditText resetMail = new EditText(v.getContext());
                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password ?");
                passwordResetDialog.setMessage("Do you want to change your password ?");
                passwordResetDialog.setIcon(getDrawable(R.drawable.ic8_add_key));
                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // extract the email and send reset link
                       firebaseAuth.sendPasswordResetEmail(user.getEmail()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Settings.this, Html.fromHtml("<b>Reset Link Sent To Your Email </b>"), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Settings.this, Html.fromHtml("<b>Error ! Reset Link is Not Sent </b>") , Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // close the dialog
                    }
                });

                passwordResetDialog.create().show();

            }
        });
    }
    public void findViews()
    {
        tv_change_password =findViewById(R.id.tv_change_password);
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        tv_name=findViewById(R.id.tv_name);
        cv_photo=findViewById(R.id.cv_photo);
        rl_edit=findViewById(R.id.rl_edit);
        tv_edit=findViewById(R.id.tv_edit);
    }
    public void loadCurentUserInformations()
    {

           tv_name.setText(GV.currentUserName);
           if (!GV.currentUserPhotoPath.equals("0"))
               Picasso.get().load(GV.currentUserPhoto).into(cv_photo);

    }






}
