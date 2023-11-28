package hk.ust.comp3021;

import hk.ust.comp3021.constants.ActivityState;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Activity {
    private final String activityID;
    private final String ServiceUnit;
    private final List<String> targetStudentDepartments;
    private final int duration;
    private final int capacity;
    private final ArrayList<Student> registeredStudents;
    private ActivityState state;

    private final Queue<Student> waitlist = new LinkedList<>();//self-defined

    public Activity(String activityID, String ServiceUnit, List<String> targetStudentDepartments,
                    int duration, int capacity) {
        this.activityID = activityID;
        this.ServiceUnit = ServiceUnit;
        this.targetStudentDepartments = targetStudentDepartments;
        this.duration = duration;
        this.capacity = capacity;
        this.registeredStudents = new ArrayList<>();
        this.state = ActivityState.OPEN;
    }

    /*
        Note:

        [Enroll]
        Check1: Fulfill requirement
        Check2: Activity::registeredStudents doesn't has this_student
        Check3: Activity has not yet full

        T1a:If full-> add to FIFO waitlist
        T1b:Add this_student to Activity::registeredStudents
        T2:Add this_activity by Student::registerActivity

        Return True : Success
        Return False: Fail

        [Drop]
        Check1: check already enrolled
        Check2: check waitlist

        TA1:Remove this_student from Activity::registeredStudents
        TA2:Remove this_activity by Student::dropActivity

        TB1: Remove from waitlist
        TB2: Enroll

        */


    /**
     * TODO: Part 1 Task 1: Implement this method to enroll a student to this activity
     * You should also enroll the activity to the student
     * Make sure no duplicate student is enrolled
     * You need to check whether the student is eligible to enroll this activity
     * You should also check whether the activity is full
     * */
    public synchronized boolean enroll(Student s){


        if((targetStudentDepartments.size() != 0) && (!targetStudentDepartments.contains(s.getDepartment())))
            return false;//Check1: Not fulfil the requirement

        if(registeredStudents.contains(s))
            return false;//Check2: Student exist already

        if(isFull()){
            waitlist.add(s); //T1a: Add to waitlist
            return false; //Check3: If full -> waitlist then bye
        }

        registeredStudents.add(s); //T1b: Add student to activity
        s.registerActivity(this);//T2: Add activitiy to students
        return true;
    }

    /**
    * TODO: Part 1 Task 1: Implement this method to drop a student from this activity
    * You should also drop the activity from the student
    * */
    public synchronized void drop(Student s){
        if(registeredStudents.contains(s)){

            registeredStudents.remove(s); //TA1: remove student from activity
            s.dropActivity(this); //TA2: remove activity in student

            if(!waitlist.isEmpty()){
                enroll( waitlist.poll()); //TB2: After someone drop -> add head to course
            }

            return; //Check1: Already enrolled
        }

        if(waitlist.contains(s)){
            waitlist.remove(s); //Check2 + TB1: In waitlist
        }


    }

    public String getActivityID() {
        return activityID;
    }

    public String getServiceUnit() {
        return ServiceUnit;
    }

    public List<String> getTargetStudentDepartments() {
        return targetStudentDepartments;
    }

    public int getDuration() {
        return duration;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isFull(){
        return registeredStudents.size() == capacity;
    }

    public ActivityState getState(){
        return this.state;
    }

    public void changeState(ActivityState s){
        // Only changeable from OPEN to CLOSED
        if (this.state == ActivityState.OPEN){
            this.state = s;
        }
    }

    //debug usage
    public String getRegisteredStudents() {
        String student_list= "[";
        for(int i = 0; i < registeredStudents.size(); i++) {

            if (i == registeredStudents.size() - 1)
                student_list += registeredStudents.get(i).getStudentID();
            else
                student_list += (registeredStudents.get(i).getStudentID() + " ");

        }

        student_list+= "]";
        return student_list;

    }
}
