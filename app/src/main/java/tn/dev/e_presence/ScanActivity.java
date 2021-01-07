package tn.dev.e_presence;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;

public class ScanActivity extends AppCompatActivity {
    private SurfaceView cameraPreview;
    private String SchoolId,GroupId,SessionId,DataBaseQRcode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listenForIncommingMessages();
        setContentView(R.layout.activity_scan);
        cameraPreview = (SurfaceView) findViewById(R.id.camera_preview);
        createCameraSource();
    }

    private void createCameraSource() {
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this).build();
        final CameraSource cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1600, 1024)
                .build();
        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {

                if (ActivityCompat.checkSelfPermission(ScanActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    //ActivityCompat.requestPermissions(ScanActivity.this,new String[]{Manifest.permission.CAMERA},REQUEST_CAMERA);
                    return;
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.

                }


                try {
                    CameraSource start = cameraSource.start(cameraPreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                cameraSource.stop();
            }
        });
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(@NonNull Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size()>0){
                    String Sc_QR_code=barcodes.valueAt(0).displayValue;
                    Intent intent = new Intent()
                            .putExtra("barcode",barcodes.valueAt(0))
                            .putExtra("DataBaseQRcode",DataBaseQRcode)
                            .putExtra("SchoolID",SchoolId)
                            .putExtra("GroupID",GroupId)
                            .putExtra("SessionID",SessionId);
                    setResult(CommonStatusCodes.SUCCESS,intent);
                    finish();
                }
            }
        });
    }

    void listenForIncommingMessages()
    {
        //listen for incoming messages
        Bundle incommingMessages =getIntent().getExtras();
        SchoolId =incommingMessages.getString("SchoolID","0");
        GroupId=incommingMessages.getString("GroupID","0");
        SessionId=incommingMessages.getString("SessionID","0");
        DataBaseQRcode=incommingMessages.getString("DataBaseQRcode","0");
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent =new Intent(ScanActivity.this,Dashboard.class)
                .putExtra("SchoolID",SchoolId)
                .putExtra("GroupID",GroupId);
        startActivity(intent);
        finish();

    }
    public void updateDocumentArray() {
        //SchoolId //SessionId
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String UserId = user.getUid();
        DocumentReference RF = db.collection("School")
                .document(SchoolId)
                .collection("Session")
                .document(SessionId);


        RF.update("listOfPresence", FieldValue.arrayUnion(UserId));


    }
}