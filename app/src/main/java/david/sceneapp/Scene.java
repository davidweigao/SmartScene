package david.sceneapp;

import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;

/**
 * Created by david on 7/1/14.
 */
public class Scene {
    private int id;
    private String name;

    private int alarmVolume = -1;
    private int musicVolume = -1;
    private int voiceCallVolume = -1;
    private int systemVolume = -1;
    private int ringerMode = -1;
    private boolean vibrate;
    private boolean ring;

    public void setVibrate(boolean vibrate) {
        this.vibrate = vibrate;
    }

    public void setRing(boolean ring) {
        this.ring = ring;
    }

    public void setSystemVolume(int systemVolume) {
        this.systemVolume = systemVolume;
    }

    public void setAlarmVolume(int alarmVolume) {
        this.alarmVolume = alarmVolume;
    }

    public void setMusicVolume(int musicVolume) {
        this.musicVolume = musicVolume;
    }

    public void setVoiceCallVolume(int voiceCallVolume) {
        this.voiceCallVolume = voiceCallVolume;
    }

    public int getAlarmVolume() {
        return alarmVolume;
    }

    public int getMusicVolume() {
        return musicVolume;
    }

    public int getVoiceCallVolume() {
        return voiceCallVolume;
    }

    public int getSystemVolume() {
        return systemVolume;
    }

    public boolean isVibrate() {
        return vibrate;
    }

    public boolean isRing() {
        return ring;
    }

    public int getRingerMode() {
        return ringerMode;
    }

    public void setRingerMode(int ringerMode) {
        this.ringerMode = ringerMode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void implement(Context context) {

        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        if (am == null) {
            Log.e("SoundScene", "manager is null");
            return;
        }

        if (alarmVolume != -1) am.setStreamVolume(AudioManager.STREAM_ALARM, alarmVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        if (musicVolume != -1) am.setStreamVolume(AudioManager.STREAM_MUSIC, musicVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        if (systemVolume != -1) am.setStreamVolume(AudioManager.STREAM_SYSTEM, systemVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        if (voiceCallVolume != -1)
            am.setStreamVolume(AudioManager.STREAM_VOICE_CALL, voiceCallVolume, 0);
        if(ringerMode != -1) am.setRingerMode(ringerMode);

        if (vibrate) {
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);
        }

        if(ring) {
            try {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(context, notification);
                r.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
