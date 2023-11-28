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

            if(num_threads == 1)
                try{threads[0].join();}catch(Exception e){}//If there is 1 student only -> finsihed task 1 ,then task 2

        }

        try{threads[num_threads-1].join();}catch(Exception e){} //In case last thread doesn't join
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
    Student getStudent(String studentID){
        return students.stream().filter(s->s.getStudentID().equals(studentID)).findFirst().orElse(null); //Generate by GPT(by copy the above statement)
    }

    Activity getActivity(String activityCode){
        return activities.stream().filter(a->a.getActivityID().equals(activityCode)).findFirst().orElse(null);//Generate by GPT(by copy the above statement)
    }

    /**
    * TODO: Part 3 Task 7: Implement following methods using lambda expression and/or functional programming
    * */
    public List<Student>  searchStudentByIntegerConditionLambda(QueryAction<Integer> query) {//Main5.1
        Integer condition = query.getCondition();
        QueryActionType actionType = query.getAction();

        switch (actionType){
            case YEAR_IS:
                return students.stream().filter(s->s.getYearOfStudy() == condition).toList();
            case REMAINING_DURATION_LARGER_THAN:
                return students.stream().filter(s->(s.getTotalDurationRequired()-s.getPassedDuration())>condition).toList();
            case REMAINING_DURATION_SMALLER_THAN:
                return students.stream().filter(s->(s.getTotalDurationRequired()-s.getPassedDuration())<condition).toList();
        }
        return null;// You can remove this line
    }

    /**
     * TODO: Part 3 Task 7
     * */
    public List<Activity> searchActivityByStringConditionLambda(QueryAction<String> query){

        String condition = query.getCondition();
        QueryActionType actionType = query.getAction();

        switch(actionType){
            case ID_CONTAINS:
                return activities.stream().filter(a->a.getActivityID().contains(condition)).toList();

            case SERVICE_UNIT_IS:
                return activities.stream().filter(a->a.getServiceUnit().equals(condition)).toList();

            case PREREQUISITE_CONTAINS:
                return activities.stream().filter(a->a.getTargetStudentDepartments().contains(condition)).toList();
        }

        return null; // You can remove this line
    }

    /**
     * TODO: Part 3 Task 7
     * */
    /*
        Condition:
        True :Ascending order
        False:Descending order

        Action:
        DURATION_CAPACITY
        CAPACITY_DURATION
     */
    public List<Activity> sortActivityByBooleanConditionByLambda(QueryAction<Boolean> query) {
        Boolean condition = query.getCondition();
        QueryActionType actionType = query.getAction();

        int order = condition?1:-1; //Check Order

        switch (actionType){
            case DURATION_CAPACITY:
                return activities.stream().sorted((activity1,activity2)->{
                    if(activity1.getDuration() == activity2.getDuration())
                        //if same duration -> check capacity
                        return Integer.compare(activity1.getCapacity(),activity2.getCapacity())*order;
                    else
                        //simply check duration
                        return Integer.compare(activity1.getDuration(),activity2.getDuration())*order;
                }).toList();

            case CAPACITY_DURATION:
                return activities.stream().sorted((activity1,activity2)->{
                    if(activity1.getCapacity() == activity2.getCapacity())
                        //if same capacity -> check duration
                        return Integer.compare(activity1.getDuration(),activity2.getDuration())*order;
                    else
                        //simply check capacity
                        return Integer.compare(activity1.getCapacity(),activity2.getCapacity())*order;
                }).toList();
        }
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
    public void processQuery(String fileName) throws IOException {//Main5(cont') jump from main
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(", ");
                QueryActionType actType = QueryActionType.valueOf(parts[0]);
                String rawCondition = parts[1];
                // TODO: Start from here

                switch (actType){
                    case YEAR_IS: case REMAINING_DURATION_SMALLER_THAN: case REMAINING_DURATION_LARGER_THAN:
                        Integer Integer_Condition = Integer.parseInt(rawCondition);

                        List<Student> Integer_result = searchStudentByIntegerConditionLambda(new QueryAction<Integer>(actType,Integer_Condition));
                        PrintResult_Student(Integer_result);


                        break;

                    case ID_CONTAINS: case SERVICE_UNIT_IS:case PREREQUISITE_CONTAINS:
                        String String_Condition = rawCondition;
                        List<Activity> String_result = searchActivityByStringConditionLambda(new QueryAction<String>(actType,String_Condition));
                        PrintResult_Activity(String_result);
                        break;

                    case DURATION_CAPACITY:case CAPACITY_DURATION:
                        Boolean Boolean_Condition = Boolean.parseBoolean(rawCondition);
                        List<Activity> Boolean_result = sortActivityByBooleanConditionByLambda(new QueryAction<Boolean>(actType,Boolean_Condition));
                        PrintResult_Activity(Boolean_result);
                        break;
                }

                // ...
            }
        }
    }

    void PrintResult_Student(List<Student> result){
        for(int i = 0; i< result.size();i++){
            if(i == result.size()-1)
                System.out.print(result.get(i).getStudentID() +"\n");
            else
                System.out.print(result.get(i).getStudentID() + ", ");

        }
    }

    void PrintResult_Activity(List<Activity> result){
        for(int i = 0; i <result.size(); i++){
            if(i== result.size()-1)
                System.out.print(result.get(i).getActivityID() +"\n");
            else
                System.out.print(result.get(i).getActivityID() + ", ");
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
        //system.Debugpart1();//Self-defined
        System.out.println("Part 1 Finished");
        // Part 2: Sequential Event Management
        System.out.println("=============== Part 2 ===============");
        system.processManagement("input/managementActions.txt"); //Main4: processManagement
        // Part 3: Functional Information Retrieval
        System.out.println("=============== Part 3 ===============");
        system.processQuery("input/queryActions.txt"); //Main5: process query
    }

    //Self defined debug
    void Debugpart1(){
        for(Activity activity: activities){
            System.out.println( activity.getActivityID()+", " + activity.getServiceUnit()+ ", " + activity.getDuration() + ", " + activity.getCapacity() +", " + activity.getTargetStudentDepartments() + ", " + activity.getRegisteredStudents());
        }
    }
}

