package tech.synframe.systemcontrol.utils;

import com.synload.eventsystem.EventPublisher;
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
            pa._delete();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void run(){
        while(true) {
            if (queue.size() > 0) {
                try {
                    List<Project> ps = queue.get(0)._related(Project.class).exec(Project.class);
                    if(ps.size()>0){
                        Project project = ps.get(0);
                        switch(ActionEnum.parse(queue.get(0).getAction())){
                            case START:
                                startProject(project);
                            break;
                            case STOP:
                                stopProject(project);
                            break;
                        }
                    }
                    remove(queue.get(0));
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(1L);
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
            }else{
                ExecuteShellSynFrame.instances.remove(project.getId()); // remove existing instance
                new Thread(new ExecuteShellSynFrame(project)).start(); // start instance of project
                raiseEvent(project, "start");
            }
        }else{
            // no existing instance found
            new Thread(new ExecuteShellSynFrame(project)).start();
            raiseEvent(project, "start");
        }
    }
    public void stopProject(Project project){
        if(ExecuteShellSynFrame.instances.containsKey(project.getId())){
            ExecuteShellSynFrame instance = ExecuteShellSynFrame.instances.get(project.getId());
            if(instance.isRunning()){
                instance.stop();
                raiseEvent(project, "stop");
            }else{
                // nothing to do, instance not running
            }
        }else{
            // nothing to do, no instance running
        }
    }
}
