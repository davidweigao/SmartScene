package david.sceneapp.Model;

import david.sceneapp.Model.Scene;

/**
 * Created by david on 7/2/14.
 */
public interface SceneTrigger {

    public Scene getScene();

    public boolean isQualified();

    public void activate();

    public void deactivate();

    public SceneTriggerExtraAction getExtraAction();

    public void setExtraAction(SceneTriggerExtraAction extraAction);

    public interface SceneTriggerExtraAction {
        public void action();
    }

}
