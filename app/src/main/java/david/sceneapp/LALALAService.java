package david.sceneapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


//TODO http://stackoverflow.com/questions/6896746/android-is-there-a-broadcast-action-for-volume-changes
public class LALALAService extends NotificationListenerService {
    static LALALAService currentInstance;
    static final String ACTION_GET_NOTIFICATION = "david.notification";
    static final String KEY_NOTIF_PKG_NAME = "notification_packagename";
    static final String ACTION_CLICK_NOTIFICATION = "com.gaowei.notif";
    static final String ACTION_SCENE_IMPLEMENTED = "com.david.sceneImplemented";
    static final String EXTRA_SCENE_INDEX = "scene_index";
    static final String EXTRA_FROM_TRIGGER = "from_trigger";
    static final String EXTRA_SCENE_ID = "com.david.extraSceneId";

    public static final int SCENE_CAPACITY = 4;
    private RemoteViews remoteViews;
    private boolean clicked = false;
    private static final int NOTIFICATION_ID = 100;
    NotificationManager mNotificationManager;
    public static boolean wifiEnabled = false;


    private  ArrayList<Scene> scenes = new ArrayList<Scene>();
    private Map<Integer, Scene> sceneMap = new HashMap<Integer, Scene>();

    public ArrayList<Scene> getScenes() {
        return scenes;
    }

    static Scene currentScene = null;
    private Map<Integer, SceneTrigger> triggers = new HashMap<Integer,SceneTrigger>();

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(ACTION_CLICK_NOTIFICATION)) {
                int index = intent.getIntExtra(EXTRA_SCENE_INDEX, -1);
                if(index > -1 && index < SCENE_CAPACITY) {
                    if(!intent.getBooleanExtra(EXTRA_FROM_TRIGGER,false))
                        LALALAService.this.implementScene(scenes.get(index));
                }
            } else if(action.equals(ACTION_SCENE_IMPLEMENTED)) {
                addRemoteView();
            }
        }
    };


    private String TAG = this.getClass().getSimpleName();
    @Override
    public void onCreate() {
        super.onCreate();
        currentInstance = this;



        SceneStorageManager ssm = new SceneStorageManager(this);
        ssm.dumpAll();
        if(ssm.getAllScene().isEmpty()) {
            ssm.saveScene(getDemoSilentMode());
            ssm.saveScene(getDemoSoundMode());
            ssm.saveScene(getDemoMiddleMode());
            ssm.saveScene(getDemoSilentMode2());
        }
        sceneMap = ssm.getAllScene();
        scenes.addAll(sceneMap.values());

//        WifiSceneTrigger trigger = new WifiSceneTrigger(scenes.get(0),this,"\"superluyouqi-5G\"");
//        SceneTriggerData triggerData = new SceneTriggerData();
//        triggerData.setTriggerType(SceneTriggerData.TYPE_WIFI_SWITCH);
//        triggerData.setSceneId(scenes.get(0).getId());
//        triggerData.setParameters(new String[]{"\"superluyouqi-5G\""});
//        triggerData.setId(0);
//        triggerData.setName(triggerData.getParameters()[0]);
//        //trigger.activate();
//
//        SceneTriggerData triggerData2 = new SceneTriggerData();
//        triggerData2.setTriggerType(SceneTriggerData.TYPE_NOTIFICATION);
//        triggerData2.setSceneId(scenes.get(1).getId());
//        triggerData2.setParameters(new String[]{"com.immomo.momo"});
//        triggerData.setId(1);
//        triggerData2.setName("lalala");
//        //trigger1.activate();
//
//        ssm.saveTrigger(triggerData);
//        ssm.saveTrigger(triggerData2);

        updateTriggers();

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        remoteViews = new RemoteViews(getPackageName(), R.layout.widget);
        addRemoteView();

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_CLICK_NOTIFICATION);
        filter.addAction(ACTION_SCENE_IMPLEMENTED);
        registerReceiver(receiver, filter);


    }

    void updateTriggers() {
        SceneStorageManager ssm = new SceneStorageManager(this);
        Map<Integer, SceneTriggerData> triggerDatas = ssm.getAllTrigger();
        for (SceneTriggerData st : triggerDatas.values()) {
            switch (st.getTriggerType()) {
                case SceneTriggerData.TYPE_WIFI_SWITCH:
                    triggers.put(st.getId(), new WifiSceneTrigger(sceneMap.get(st.getSceneId()), this, st.getParameters()[0]));
                    break;
                case SceneTriggerData.TYPE_NOTIFICATION:
                    Set<String> pkgs = new HashSet<String>();
                    pkgs.add(st.getParameters()[0]);
                    triggers.put(st.getId(), new NotificationSceneTrigger(this, sceneMap.get(st.getSceneId()), pkgs));
                    break;
            }
        }
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

    public void toggleTrigger(int triggerId, boolean activate) {
        if(activate)
            triggers.get(triggerId).activate();
        else
            triggers.get(triggerId).deactivate();
    }

    public void toggleAllWifiSwitch(boolean activate) {
        wifiEnabled = activate;
        for(SceneTrigger st : triggers.values()) {
            if(st instanceof WifiSceneTrigger) {
                if(activate) st.activate();
                else st.deactivate();
            }
        }

    }


    public void implementScene(Scene scene) {
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (am == null) {
            Log.e(TAG, "sound manager is null");
            return;
        }

        if (scene.getAlarmVolume() != -1)
            am.setStreamVolume(AudioManager.STREAM_ALARM, scene.getAlarmVolume(), AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        if (scene.getMusicVolume() != -1)
            am.setStreamVolume(AudioManager.STREAM_MUSIC, scene.getMusicVolume(), AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        if (scene.getSystemVolume() != -1)
            am.setStreamVolume(AudioManager.STREAM_SYSTEM, scene.getSystemVolume(), AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        if (scene.getVoiceCallVolume() != -1)
            am.setStreamVolume(AudioManager.STREAM_VOICE_CALL, scene.getVoiceCallVolume(), AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        if(scene.getRingerMode() != -1) am.setRingerMode(scene.getRingerMode());

        if (scene.isVibrate()) {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);
        }

        if(scene.isRing()) {
            try {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(this, notification);
                r.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        currentScene = scene;

        Intent intent = new Intent(ACTION_SCENE_IMPLEMENTED);
        intent.putExtra(EXTRA_SCENE_ID, scene.getId());
        sendBroadcast(intent);
    }

    public void implementScene(int sceneId) {
        for(Scene s : scenes) {
            if(sceneId == s.getId()) {
                implementScene(s);
                break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    Scene getDemoSilentMode() {
        Scene ss = new Scene();
        ss.setSystemVolume(0);
        ss.setVibrate(true);
        ss.setName("silent");
        ss.setId(1);
        ss.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        return ss;
    }

    Scene getDemoSoundMode() {
        Scene ss = new Scene();
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        ss.setSystemVolume(audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM));
        ss.setRing(true);
        ss.setName("sound");
        ss.setId(2);
        ss.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        return ss;
    }

    Scene getDemoMiddleMode() {
        Scene ss = new Scene();
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        ss.setSystemVolume(audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM) / 2);
        ss.setRing(true);
        ss.setName("mid");
        ss.setId(3);
        return ss;
    }

    Scene getDemoSilentMode2() {
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
