package tech.synframe.systemcontrol.events;

import com.synload.eventsystem.EventClass;
import com.synload.eventsystem.Handler;
import com.synload.eventsystem.Type;
import tech.synframe.systemcontrol.models.Project;
import tech.synframe.systemcontrol.models.User;

/**
 * Created by Nathaniel on 5/7/2016.
 */
public class QueueAction extends EventClass {
    public Project project;
    public String action;
    public User user;
    public QueueAction(Project project, String action, User user){
        this.project = project;
        this.action = action;
        this.user = user;
        this.setHandler(Handler.EVENT);
        this.setType(Type.OTHER);
    }

    public Project getProject() {
        return project;
    }

    public String getAction() {
        return action;
    }

    public User getUser() {
        return user;
    }
}
