package tech.synframe.systemcontrol.handlers.ws;

import com.synload.eventsystem.events.RequestEvent;
import com.synload.framework.handlers.Request;
import com.synload.framework.ws.annotations.WSEvent;

/**
 * Created by Nathaniel on 5/15/2016.
 */
public class ProjectElementWSEvents {
    @WSEvent(name="CreateProject", description="", enabled = true, method = "get", action = "createproject")
    public void createProject(RequestEvent e){
        
    }
}
