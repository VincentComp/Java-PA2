package hk.ust.comp3021;

import hk.ust.comp3021.constants.*;
import hk.ust.comp3021.action.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainSystem {
    private final List<Student> students;
    private final List<Activity> activities;
    private final List<EventHandler> listeners;

    public MainSystem() {
        this.students = new ArrayList<>();
        this.activities = new ArrayList<>();
        this.listeners = new ArrayList<>();
    }


    /**
     * TODO Part 1 Task 1: Implement enroll() and drop() in Activity.java
     */
    /**
     * TODO Part 1 Task 2: Implement this method
     */


    //Solved by GPT (with copy and paste the statement from guidlines)
    public void concurrentRegistration(List<RegistrationAction> actions) {//Main3(cont'): jump from main


        int num_threads = actions.size();
        Thread[] threads = new Thread[num_threads]; //*****Mulitprogramming Method******

        for(int i = 0; i < num_threads;i++){
            if(i !=0)
                try {threads[i-1].join();}catch (Exception e){} //Wait previous action to complete


            RegistrationAction action = actions.get(i);//get current action
            threads[i] = new Thread(()->{
                Student student = action.getStudent(); //Get All data
                Activity activity = action.getActivity();
                RegistrationActionType actionType = action.getAction();

                if(actionType.equals(RegistrationActionType.ENROLL)) //If action == ENROLL
                    activity.enroll(student);
                else if(actionType.equals(RegistrationActionType.DROP)) //If action == Drop
                    activity.drop(student);

                action.setCompleted(true); //Set the status = true
            });

            threads[i].start();
        }

            /*
        ExecutorService executor = Executors.newSingleThreadExecutor(); //[Concurrent Method]
        for(RegistrationAction action: actions){


            executor.submit(()->{
                Student student = action.getStudent(); //Get All data
                Activity activity = action.getActivity();
                RegistrationActionType actionType = action.getAction();

                if(actionType.equals(RegistrationActionType.ENROLL)) //If action == ENROLL
                    activity.enroll(student);
                else if(actionType.equals(RegistrationActionType.DROP)) //If action == Drop
                    activity.drop(student);

                action.setCompleted(true); //Set the status = true
            });

        }

             */
    }



    /**
     * TODO: Part 2 Task 3: Define interface EventHandler and make correct class implement it
     * */
    /**
     * TODO: Part 2 Task 4: Define and implement class ActivityEvent that implements Event
     * */
    public void addListener(EventHandler listener) { //Main4.1: add students to the this.listners
        listeners.add(listener);
    }

    public void dispatchEvent(Event event) {//Main4.3
        listeners.forEach(listener -> listener.onEvent(event));//Invoke listener with the Activityevent
    }

    /**
     * TODO: Part 2 Task 5: Implement the following method for event handling.
     * You need to call dispatchEvent() in this method.
     * */
    public void studentGetUpdate(ManagementAction action) { //Main4.2
        Activity activity = action.getActivity();
        ManagementActionType actionType = action.getAction();


        if(actionType.equals(ManagementActionType.ACTIVITY_FINISHED)) //Student::C2/C3-T1/T1 -> Change state
            activity.changeState(ActivityState.FINISHED);
        else if(actionType.equals(ManagementActionType.ACTIVITY_CANCELLED))
            activity.changeState(ActivityState.CANCELLED);

        dispatchEvent(new ActivityEvent(activity,actionType)); //send action to the listeners
    }

    /**
     * TODO: Part 3 Task 6: Implement getStudent(String studentID) and getActivity(String activityID) use lambda expression and/or functional programming
    * */
    // Start from here

    /**
    * TODO: Part 3 Task 7: Implement following methods using lambda expression and/or functional programming
    * */
    public List<Student>  searchStudentByIntegerConditionLambda(QueryAction<Integer> query) {
        return null; // You can remove this line
    }

    /**
     * TODO: Part 3 Task 7
     * */
    public List<Activity> searchActivityByStringConditionLambda(QueryAction<String> query){
        return null; // You can remove this line
    }

    /**
     * TODO: Part 3 Task 7
     * */
    public List<Activity> sortActivityByBooleanConditionByLambda(QueryAction<Boolean> query) {
        return null; // You can remove this line
    }

    public void parseStudents(String fileName) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(", ");
                String studentID = parts[0];
                int yearOfStudy = Integer.parseInt(parts[1]);
                String department = parts[2];
                int totalDurationRequired = Integer.parseInt(parts[3]);
                Student student = new Student(studentID, yearOfStudy, department, totalDurationRequired);
                students.add(student);
            }
        }
    }

    public void parseActivities(String fileName) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(", ");
                String activityID = parts[0];
                String serviceUnit = parts[1];
                int duration = Integer.parseInt(parts[2]);
                int capacity = Integer.parseInt(parts[3]);
                List<String> preferences;
                if (parts[4].length() > 2){
                    preferences = Arrays.asList(parts[4].substring(1, parts[4].length() - 1).split(" "));
                }
                else {
                    preferences = new ArrayList<>();
                }
                Activity activity = new Activity(activityID, serviceUnit, preferences, duration, capacity);
                activities.add(activity);
            }
        }
    }

    public void processRegistration(String fileName) throws IOException {
        List<RegistrationAction> actions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(", ");
                String studentID = parts[0];
                Student student = getStudent(studentID);
                String activityCode = parts[1];
                Activity activity = getActivity(activityCode);
                RegistrationActionType actType = RegistrationActionType.valueOf(parts[2]);
                actions.add(new RegistrationAction(student, activity, actType));
            }
        }
        concurrentRegistration(actions); //Main3 con't: prase Registration -> pass to concurrent
    }

    public void processManagement(String fileName) throws IOException {//Main4(cont') jump from main
        List<ManagementAction> actions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(", ");
                String activityCode = parts[0];
                Activity activity = getActivity(activityCode);
                ManagementActionType actType = ManagementActionType.valueOf(parts[1]);
                actions.add(new ManagementAction(activity, actType)); //create an Action list
            }
        }
        // You need to have your implementation done to allow the following code to work
        this.students.forEach(this::addListener); //Add all students to the listener list
        actions.forEach(this::studentGetUpdate); //Call students GetUpdate with different actions
    }

    /**
     * TODO: Part 3 Task 8: Finish this method
     */
    public void processQuery(String fileName) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(", ");
                QueryActionType actType = QueryActionType.valueOf(parts[0]);
                String rawCondition = parts[1];
                // TODO: Start from here
                // ...
            }
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("=============== System Start ===============");
        MainSystem system = new MainSystem();
        system.parseStudents("input/student.txt"); //Main 1: parse the Students
        system.parseActivities("input/activity.txt"); //Main 2: parse the Activities
        // Part 1: Parallel Registration
        System.out.println("=============== Part 1 ===============");
        system.processRegistration("input/registrationActions.txt"); //Main3: process processRegistration
        System.out.println("Part 1 Finished");
        // Part 2: Sequential Event Management
        System.out.println("=============== Part 2 ===============");
        system.processManagement("input/managementActions.txt"); //Main4: processManagement
        // Part 3: Functional Information Retrieval
        System.out.println("=============== Part 3 ===============");
        system.processQuery("input/queryActions.txt");
    }
}
