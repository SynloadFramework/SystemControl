package tech.synframe.systemcontrol.events;

import com.synload.eventsystem.EventClass;
import com.synload.framework.ws.WSHandler;
import tech.synframe.systemcontrol.models.Project;
import tech.synframe.systemcontrol.models.User;

/**
 * Created by Nathaniel on 5/6/2016.
 */
public class ProjectCreated extends EventClass{
    public Project project;
    public User user;
    public WSHandler session;
    public ProjectCreated(WSHandler session, User u, Project p){
        this.session = session;
        this.user = u;
        this.project = p;
    }

    public Project getProject() {
        return project;
    }

    public User getUser() {
        return user;
    }

    public WSHandler getSession() {
        return session;
    }
}
