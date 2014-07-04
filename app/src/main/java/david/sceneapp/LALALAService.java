package david.sceneapp;

import android.app.Notification;
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
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


//TODO http://stackoverflow.com/questions/6896746/android-is-there-a-broadcast-action-for-volume-changes
public class LALALAService extends NotificationListenerService {
    public static LALALAService currentInstance;
    public static final String ACTION_GET_NOTIFICATION = "david.notification";
    public static final String KEY_NOTIF_PKG_NAME = "notification_packagename";
    public static final String ACTION_CLICK_NOTIFICATION = "com.gaowei.notif";
    public static final String EXTRA_SCENE_INDEX = "scene_index";
    public static final String EXTRA_FROM_TRIGGER = "from_trigger";
    public static final int SCENE_CAPACITY = 4;
    private RemoteViews remoteViews;
    private boolean clicked = false;
    private static final int NOTIFICATION_ID = 100;
    NotificationManager mNotificationManager;


    private ArrayList<Scene> scenes = new ArrayList<Scene>();
    private Scene currentScene = null;

    private Set<SceneTrigger> triggers = new HashSet<SceneTrigger>();

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(ACTION_CLICK_NOTIFICATION)) {
                Toast.makeText(LALALAService.this, "hahaha", Toast.LENGTH_SHORT).show();
                int index = intent.getIntExtra(EXTRA_SCENE_INDEX, -1);
                if(index > -1 && index < SCENE_CAPACITY) {
                    currentScene = scenes.get(index);
                    if(!intent.getBooleanExtra(EXTRA_FROM_TRIGGER,false))
                        currentScene.implement(LALALAService.this);
                    addRemoteView();
                }
            }
        }
    };


    private String TAG = this.getClass().getSimpleName();
    @Override
    public void onCreate() {
        super.onCreate();
        currentInstance = this;

        scenes.add(getDemoSilentMode());
        scenes.add(getDemoSoundMode());
        scenes.add(getDemoMiddleMode());
        scenes.add(getDemoSilentMode2());

        WifiSceneTrigger trigger = new WifiSceneTrigger(scenes.get(0),this,"\"nmagic2\"");
        //trigger.activate();
        HashSet<String> pkgNames = new HashSet<String>();
        pkgNames.add("com.immomo.momo");
        NotificationSceneTrigger trigger1 = new NotificationSceneTrigger(this, scenes.get(1),pkgNames);
        //trigger1.activate();

        triggers.add(trigger);
        triggers.add(trigger1);

        for(final SceneTrigger t : triggers) {
            t.activate();
            if(scenes.contains(t.getScene())) {
                t.setExtraAction(new SceneTrigger.SceneTriggerExtraAction() {
                    @Override
                    public void action() {
                        Intent intent = new Intent();
                        intent.setAction(ACTION_CLICK_NOTIFICATION);
                        intent.putExtra(EXTRA_SCENE_INDEX, scenes.indexOf(t.getScene()));
                        intent.putExtra(EXTRA_FROM_TRIGGER, true);
                        LALALAService.this.sendBroadcast(intent);
                    }
                });
            }
        }



        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        remoteViews = new RemoteViews(getPackageName(), R.layout.widget);
        addRemoteView();

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_CLICK_NOTIFICATION);
        registerReceiver(receiver, filter);


    }

    private void addRemoteView() {
        if(scenes.isEmpty()) return;
//        remoteViews.removeAllViews(R.id.buttonContainer);
        for(int i = 0; i < Math.min(SCENE_CAPACITY, scenes.size()); i++) {
            Scene s = scenes.get(i);
            int buttonId = getIdBySceneIndex(i);
            remoteViews.setCharSequence(buttonId, "setText", s.getName());
            if(s.equals(currentScene)) {
                remoteViews.setInt(buttonId, "setBackgroundResource", android.R.color.holo_red_dark);
            } else {
                remoteViews.setInt(buttonId, "setBackgroundResource", android.R.color.holo_blue_bright);
            }
            Intent resultIntent = new Intent();
            resultIntent.setAction(ACTION_CLICK_NOTIFICATION);
            resultIntent.putExtra(EXTRA_SCENE_INDEX, i);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, s.getId(), resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(buttonId, pendingIntent);
        }
        Notification.Builder mBuilder = new Notification.Builder(
                this).setSmallIcon(R.drawable.ic_launcher).setContent(
                remoteViews).setOngoing(true);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private int getIdBySceneIndex(int index) {
        switch (index) {
            case 0: return R.id.sceneButton1;
            case 1: return R.id.sceneButton2;
            case 2: return R.id.sceneButton3;
            case 3: return R.id.sceneButton4;
            default: return 0;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private Scene getDemoSilentMode() {
        Scene ss = new Scene();
        ss.setSystemVolume(0);
        ss.setVibrate(true);
        ss.setName("silent");
        ss.setId(1);
        return ss;
    }

    private Scene getDemoSoundMode() {
        Scene ss = new Scene();
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        ss.setSystemVolume(audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM));
        ss.setRing(true);
        ss.setName("sound");
        ss.setId(2);
        return ss;
    }

    private Scene getDemoMiddleMode() {
        Scene ss = new Scene();
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        ss.setSystemVolume(audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM) / 2);
        ss.setRing(true);
        ss.setName("mid");
        ss.setId(3);
        return ss;
    }

    private Scene getDemoSilentMode2() {
        Scene ss = new Scene();
        ss.setSystemVolume(0);
        ss.setVibrate(true);
        ss.setName("silent");
        ss.setId(4);
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
