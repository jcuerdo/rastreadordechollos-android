package com.jca.rastreadordechollos;

import android.content.Context;
import android.content.ServiceConnection;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        System.out.println(s);
    }



    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Context context = getApplicationContext();
        CharSequence text = "Hello toast!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
