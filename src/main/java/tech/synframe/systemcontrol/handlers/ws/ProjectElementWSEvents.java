package tech.synframe.systemcontrol.handlers.ws;

import com.synload.eventsystem.events.RequestEvent;
import com.synload.framework.handlers.Data;
import com.synload.framework.handlers.Request;
import com.synload.framework.ws.annotations.WSEvent;
import tech.synframe.systemcontrol.elements.project.CreateProjectPage;
import tech.synframe.systemcontrol.models.User;

import java.util.HashMap;

/**
 * Created by Nathaniel on 5/15/2016.
 */
public class ProjectElementWSEvents {
    @WSEvent(name="CreateProject", description="", enabled = true, method = "get", action = "createproject")
    public void createProject(RequestEvent e){
        if(e.getSession().getSessionData().containsKey("user")){
            e.getSession().send(
                    new CreateProjectPage(
                            e.getSession(),
                            e.getRequest().getTemplateCache(),
                            (User) e.getSession().getSessionData().get("user")
                    )
            );
        }
    }
}
