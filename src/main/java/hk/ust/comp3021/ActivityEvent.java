package hk.ust.comp3021;


import hk.ust.comp3021.constants.ManagementActionType;

public class ActivityEvent implements Event{
    private final Activity activity;
    private final ManagementActionType action;

    ActivityEvent(Activity activity, ManagementActionType action){
        this.activity = activity;
        this.action = action;
    }

    public ManagementActionType getAction() {
        return action;
    }

    public Activity getEventActivity() {
        return activity;
    }
}
