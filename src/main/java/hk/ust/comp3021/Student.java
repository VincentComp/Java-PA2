package hk.ust.comp3021;

import hk.ust.comp3021.constants.ActivityState;
import hk.ust.comp3021.constants.ManagementActionType;

import java.util.*;

public class Student implements EventHandler {
    private final String StudentID;
    private final int YearOfStudy;
    private final String department;
    private final int totalDurationRequired;
    private int passedDuration;
    private final List<Activity> registeredActivities;
    private final List<Activity> finishedActivities;

    public Student(String studentID, int yearOfStudy, String department, int totalDurationRequired) {
        this.StudentID = studentID;
        this.YearOfStudy = yearOfStudy;
        this.registeredActivities = new ArrayList<>();
        this.finishedActivities = new ArrayList<>();
        this.department = department;
        this.totalDurationRequired = totalDurationRequired;
        this.passedDuration = 0;
    }

    public String getStudentID() {
        return StudentID;
    }

    public int getYearOfStudy() {
        return YearOfStudy;
    }

    public String getDepartment() {
        return department;
    }

    public int getTotalDurationRequired() {
        return totalDurationRequired;
    }

    public int getPassedDuration() {
        return passedDuration;
    }

    /**The check of repeated registration would be in Activity Class, not this class
     * So this method only do simple registration
    * */
    public void registerActivity(Activity a){
        registeredActivities.add(a);
    }

    public void dropActivity(Activity a){
        registeredActivities.remove(a);
    }

    /*
        Check1: Only notified stydents enrolled in the activity

        C1:ACTIVITY_POSTONED
            T1:Print notification

        C2:ACTIVITY_FINISHED
            T1:Change the activity state to FINISHED
            T2:Move activity from registeredActivities to finished activities
            T3:Add Activity duration to passed Duration
            T4:Print notification

        C3:ACTIVITY_CANCELLED
            T1:Change the activity state to CANCELLED
            T2:Remove activity from registedActivites
            T3:Print notification
    */
    @Override
    public void onEvent(Event event) { //Main4.4
        if(event instanceof ActivityEvent){ //Note: C2/C3-T1/T1 already handle in MainSystem

            ActivityEvent activityEvent = (ActivityEvent) event; //Get info from event
            Activity activity = activityEvent.getEventActivity();
            ManagementActionType actionType = activityEvent.getAction();

            if(!registeredActivities.contains(activity)) {
                return; //Check1: Not yet enrolled -> Bye
            }

            if(actionType.equals(ManagementActionType.ACTIVITY_POSTPONED)){
                System.out.println(this.getStudentID() + " is notified that Activity " + activity.getActivityID() + " has postponed.");//T1

            } else if (actionType.equals(ManagementActionType.ACTIVITY_FINISHED)){

                this.registeredActivities.remove(activity); //T2:Move from registed to finished
                this.finishedActivities.add(activity);

                this.passedDuration += activity.getDuration(); //T3:Add Duration

                System.out.println(this.getStudentID() + " is notified that Activity " + activity.getActivityID() + " has finished.");//T4

            } else if (actionType.equals(ManagementActionType.ACTIVITY_CANCELLED)){

                this.registeredActivities.remove(activity); //T2: Remove activity
                System.out.println(this.getStudentID() + " is notified that Activity " + activity.getActivityID() + " has cancelled.");//T4
            }


        }
    }


}
