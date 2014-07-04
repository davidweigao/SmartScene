package david.sceneapp;

import android.content.Context;

/**
 * Created by david on 7/2/14.
 */
public class SceneActor {
    private Scene scene;
    private SceneTrigger sceneTrigger;
    private Context context;

    public void implement() {
        sceneTrigger.activate();
    }

}
