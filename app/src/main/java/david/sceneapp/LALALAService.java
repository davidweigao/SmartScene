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

import java.util.ArrayList;
import java.util.HashSet;

public class LALALAService extends NotificationListenerService {
    public static LALALAService currentInstance;
    public static final String ACTION_GET_NOTIFICATION = "david.notification";
    public static final String KEY_NOTIF_PKG_NAME = "notification_packagename";
    public static final String ACTION_CLICK_NOTIFICATION = "com.gaowei.notif";
    private RemoteViews remoteViews;
    private boolean clicked = false;
    private static final int NOTIFICATION_ID = 100;
    NotificationManager mNotificationManager;

    private ArrayList<Scene> scenes = new ArrayList<Scene>();

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(ACTION_CLICK_NOTIFICATION)) {
                Toast.makeText(LALALAService.this, "hahaha", Toast.LENGTH_SHORT).show();
                toggleButton();
            }
        }
    };


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

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_CLICK_NOTIFICATION);
        registerReceiver(receiver, filter);
    }

    public void toggleButton() {
        remoteViews = new RemoteViews(getPackageName(), R.layout.widget);
        if(clicked) {
            remoteViews.setInt(R.id.button1, "setBackgroundResource", android.R.color.holo_red_dark);

        } else {
            remoteViews.setInt(R.id.button1, "setBackgroundResource", android.R.color.holo_blue_bright);

        }
        clicked = !clicked;
        addRemoteView();
    }


    private void addRemoteView() {
//        remoteViews.setInt(R.id.button1, "setBackground", android.R.color.holo_red_dark);
        remoteViews.
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.ic_launcher).setContent(
                remoteViews).setOngoing(true);
        Intent resultIntent = new Intent();
        resultIntent.setAction(ACTION_CLICK_NOTIFICATION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 123, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.button1, pendingIntent);
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
