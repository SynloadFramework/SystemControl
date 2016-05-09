package tech.synframe.systemcontrol.events;

import com.synload.eventsystem.EventClass;
import com.synload.eventsystem.Handler;
import com.synload.eventsystem.Type;
import com.synload.framework.handlers.Request;
import com.synload.framework.ws.WSHandler;
import tech.synframe.systemcontrol.models.User;

/**
 * Created by Nathaniel on 5/6/2016.
 */
public class UserLoggedIn extends EventClass{
    public User user;
    public WSHandler session;
    public Request request;
    public UserLoggedIn(WSHandler session, User u, Request request){
        this.user = u;
        this.session = session;
        this.request = request;
        this.setHandler(Handler.EVENT);
        this.setType(Type.OTHER);
    }

    public User getUser() {
        return user;
    }

    public WSHandler getSession() {
        return session;
    }

    public Request getRequest() {
        return request;
    }
}
