package tech.synframe.systemcontrol.utils;

import com.synload.eventsystem.EventPublisher;
import com.synload.framework.Log;
import tech.synframe.systemcontrol.events.QueueAction;
import tech.synframe.systemcontrol.models.PendingAction;
import tech.synframe.systemcontrol.models.Project;
import tech.synframe.systemcontrol.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nathaniel on 5/7/2016.
 */
public class Queue implements Runnable {
    public static List<PendingAction> queue = new ArrayList<PendingAction>();
    public static void add(PendingAction pa){
        queue.add(pa);
    }
    public static void remove(PendingAction pa){
        queue.remove(pa);
        try {
            List<Project> ps = pa._related(Project.class).exec(Project.class);
            if(ps.size()>0) {
                ps.get(0)._unset(pa); // remove relation
            }
            pa._delete();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public int actionToInt(String act){
        if(act.equalsIgnoreCase("start")){
            return 1;
        }else if(act.equalsIgnoreCase("stop")){
            return 2;
        }
        return -1;
    }
    public void run(){
        while(true) {
            if (queue.size() > 0) {
                try {
                    List<Project> ps = queue.get(0)._related(Project.class).exec(Project.class);
                    if(ps.size()>0){
                        //Log.error("project found!", Queue.class);
                        Project project = ps.get(0);
                        switch(actionToInt(queue.get(0).getAction())){
                            case 1:
                                startProject(project);
                            break;
                            case 2:
                                stopProject(project);
                            break;
                            case -1:
                                //Log.error("action not found!", Queue.class);
                            break;
                        }
                    }else{
                        //Log.error("project not found!", Queue.class);
                    }
                    remove(queue.get(0));
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            //Log.info("test", Queue.class);
            try {
                Thread.sleep(1000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public void raiseEvent(Project project, String action){
        User user = null;
        try {
            List<User> users = project._related(User.class).exec(User.class);
            if (users.size() > 0) {
                user = users.get(0);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        EventPublisher.raiseEvent(new QueueAction(project, action, user), true, null);
    }
    public void startProject(Project project){
        if(ExecuteShellSynFrame.instances.containsKey(project.getId())){
            ExecuteShellSynFrame instance = ExecuteShellSynFrame.instances.get(project.getId());
            if(instance.isRunning()){
                // Already running, no action
                //Log.info("Already running",Queue.class);
            }else{
                //ExecuteShellSynFrame.instances.remove(project.getId()); // remove existing instance
                new Thread(new ExecuteShellSynFrame(project)).start(); // start instance of project
                raiseEvent(project, "start");
                //Log.info("Start project",Queue.class);
            }
        }else{
            // no existing instance found
            new Thread(new ExecuteShellSynFrame(project)).start();
            raiseEvent(project, "start");
            //Log.info("Start project",Queue.class);
        }
        //Log.info("done",Queue.class);
    }
    public void stopProject(Project project){
        if(ExecuteShellSynFrame.instances.containsKey(project.getId())){
            ExecuteShellSynFrame instance = ExecuteShellSynFrame.instances.get(project.getId());
            if(instance.isRunning()){
                instance.stop();
                raiseEvent(project, "stop");
                //Log.info("Stop project",Queue.class);
            }else{
                // nothing to do, instance not running
                //Log.info("nothing...",Queue.class);
            }
        }else{
            // nothing to do, no instance running
            //Log.info("nothing...",Queue.class);
        }
        //Log.info("done",Queue.class);
    }
}
