package david.sceneapp.Model;

import android.app.Application;
import android.content.Intent;

import david.sceneapp.LALALAService;

/**
 * Created by David on 7/10/2014.
 */
public class SmartSceneApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        startService(new Intent(getApplicationContext(), LALALAService.class));
    }
}
