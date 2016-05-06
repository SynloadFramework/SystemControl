package tech.synframe.systemcontrol.handlers.ws;

import com.synload.eventsystem.events.RequestEvent;
import com.synload.framework.Log;
import com.synload.framework.ws.annotations.WSEvent;
import tech.synframe.systemcontrol.elements.GetPendingActions;
import tech.synframe.systemcontrol.models.User;

/**
 * Created by Nathaniel on 5/5/2016.
 */
public class PendingActions {

    @WSEvent(method = "get", action = "pendingActions", description = "Displays all pending actions for the user", enabled = true, name = "DisplayPendingActions")
    public void display(RequestEvent e){
        if(e.getSession().getSessionData().containsKey("user")){
            User u = (User)e.getSession().getSessionData().get("user");
            e.getSession().send(
                new GetPendingActions(
                    e.getSession(),
                    e.getRequest().getTemplateCache(),
                    u
                )
            );
        }else{
            Log.info("Not logged in", PendingActions.class);
        }
    }

}
