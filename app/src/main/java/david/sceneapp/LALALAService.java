package david.sceneapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.HashSet;

public class LALALAService extends NotificationListenerService {
    public static LALALAService currentInstance;
    public static final String ACTION_GET_NOTIFICATION = "david.notification";
    public static final String KEY_NOTIF_PKG_NAME = "notification_packagename";
    private RemoteViews remoteViews;
    private boolean clicked = false;
    private static final int NOTIFICATION_ID = 100;
    NotificationManager mNotificationManager;


    private String TAG = this.getClass().getSimpleName();
    @Override
    public void onCreate() {
        super.onCreate();
        currentInstance = this;
        WifiSceneTrigger trigger = new WifiSceneTrigger(getDemoSilentMode(),this,"\"nmagic2\"");
        trigger.activate();
        HashSet<String> pkgNames = new HashSet<String>();
        pkgNames.add("com.immomo.momo");
        NotificationSceneTrigger trigger1 = new NotificationSceneTrigger(this, getDemoSoundMode(),pkgNames);
        trigger1.activate();

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        remoteViews = new RemoteViews(getPackageName(), R.layout.widget);
        addRemoteView();
    }

    public void toggleButton() {
        remoteViews = new RemoteViews(getPackageName(), R.layout.widget);
        if(clicked) {
            remoteViews.setInt(R.id.button1, "setBackground", android.R.color.holo_red_dark);

        } else {
            remoteViews.setInt(R.id.button1, "setBackground", android.R.color.darker_gray);

        }
        clicked = !clicked;
        addRemoteView();
    }


    private void addRemoteView() {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.ic_launcher).setContent(
                remoteViews).setOngoing(true);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, NotificationHandlerActivity.class);
        resultIntent.putExtra("button",1);
        // The stack builder object will contain an artificial back stack for
        // the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(WifiActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.button1, resultPendingIntent);

        // mId allows you to update the notification later on.
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private Scene getDemoSilentMode() {
        Scene ss = new Scene();
        ss.setSystemVolume(0);
        ss.setVibrate(true);
        return ss;
    }

    private Scene getDemoSoundMode() {
        Scene ss = new Scene();
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        ss.setSystemVolume(audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM));
        ss.setRing(true);
        return ss;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        Log.i(TAG,"**********  onNotificationPosted");
        Log.i(TAG, "ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName());
        Intent i = new  Intent(ACTION_GET_NOTIFICATION);
        i.putExtra(KEY_NOTIF_PKG_NAME, sbn.getPackageName());
        sendBroadcast(i);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i(TAG,"********** onNOtificationRemoved");
        Log.i(TAG,"ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText +"\t" + sbn.getPackageName());

    }

}
