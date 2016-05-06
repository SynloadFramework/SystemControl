package tech.synframe.systemcontrol.events;

import com.synload.eventsystem.EventClass;
import com.synload.framework.ws.WSHandler;
import tech.synframe.systemcontrol.models.User;

/**
 * Created by Nathaniel on 5/6/2016.
 */
public class UserLoggedIn extends EventClass{
    public User user;
    public WSHandler session;
    public UserLoggedIn(WSHandler session, User u){
        this.user = u;
        this.session = session;
    }

    public User getUser() {
        return user;
    }

    public WSHandler getSession() {
        return session;
    }
}
