package tn.dev.e_presence;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;
public class NotificationHandler {
    public static String token;
    private static final FirebaseFirestore db=FirebaseFirestore.getInstance();
    private static final FirebaseMessaging mes=FirebaseMessaging.getInstance();
    public static final ApiCloudMessaging apiCloudMessaging = Client
            .getClient("https://fcm.googleapis.com/")
            .create(ApiCloudMessaging.class);


// get current token
    public static void getNewToken()
    {
        mes.getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("token", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                         token = task.getResult();
                    }
                });
    }
    // update token in Database
    public static void updateToken()
    {
        Map<String,Object> tokenMap=new HashMap<String, Object>();
        tokenMap.put("token",token);
        db.collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(tokenMap);
    }
/*-------------------------Notification Class------------------*/
    public static class Notification
    {
            /*------------------Data Class-----------------*/
            public class Data
            {
             // Data Attributes
                private String Title;
                private String Message;
             // Data Constructors
                public Data(String title, String message) {Title = title;Message = message; }
                public Data() { }
            //Data setters and getters
                public String getTitle() { return Title; }
                public void setTitle(String title) {Title = title; }
                public String getMessage() { return Message;}
                public void setMessage(String message) { Message = message; }
            }
        // Notification Attributes
            private Data data;// data to send
            private String token;//receiver token
        // Notification Constructors
            public Notification() { }
            public Notification(String title,String message, String token) { this.data = new Data(title,message); this.token = token;}
        //Notification setters and getters
            public Data getData() { return data; }
            public void setData(Data data) { this.data = data; }
            public String getToken() { return token;}
            public void setToken(String token) { this.token = token; }
        //send Notification
            public void sendNotifications() {
                apiCloudMessaging.sendNotifcation(NotificationHandler.Notification.this).enqueue(new Callback<Integer>() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                        if (response.code() == 200) {
                            if (response.body() != 1) {
                                Toast.makeText(getApplicationContext(), "Failed ", Toast.LENGTH_LONG);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Integer> call, Throwable t) {

                    }
                });
            }
    }

/*---------ApiCloudMessaging Interface-----------*/
    public interface ApiCloudMessaging
{
        @Headers(
                    {
                        "Content-Type:application/json",
                        "Authorization:key=AAAAuCPIeMI:APA91bHcuqIMPNAR7japDIOni0FxzFsVeY_m3bz3FzAwpl6kSS5UfjmRZEvbh0n_hLl0j_7Zzkk9ACwi0kfdfOEdPEA9ndxwWf-Rzfs49tC71hMb6Xg_1QzzX-WjPF49LZz0l-XY-HMb"
                    }
                )

        @POST("fcm/send")
        Call<Integer> sendNotifcation(@Body NotificationHandler.Notification body);

    }
/*-------------Client Class-------------------*/
    public static class Client {
        private static Retrofit retrofit=null;

        public static Retrofit getClient(String url)
        {
            if(retrofit==null)
            {
                retrofit=new Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create()).build();
            }
            return  retrofit;
        }
    }
/*----------------------------------------------*/
public class fireBaseMessagingService extends FirebaseMessagingService {
    String title,message;
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        title=remoteMessage.getData().get("title");
        message=remoteMessage.getData().get("message");
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_logo)
                        .setContentTitle(title)
                        .setContentText(message);
        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

}

}

