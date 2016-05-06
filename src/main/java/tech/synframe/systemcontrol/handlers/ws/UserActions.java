package tech.synframe.systemcontrol.handlers.ws;

import com.synload.eventsystem.EventPublisher;
import com.synload.eventsystem.events.RequestEvent;
import com.synload.framework.ws.annotations.WSEvent;
import tech.synframe.systemcontrol.elements.user.LoginForm;
import tech.synframe.systemcontrol.events.UserLoggedIn;
import tech.synframe.systemcontrol.models.User;

import java.util.List;

/**
 * Created by Nathaniel on 5/6/2016.
 */
public class UserActions {
    @WSEvent(method = "get", action = "login", description = "Display login form", enabled = true, name = "DisplayLoginForm")
    public void loginForm(RequestEvent e){
        e.getSession().send(new LoginForm(e.getSession(), e.getRequest().getTemplateCache()));
    }
    @WSEvent(method = "post", action = "login", description = "initiate login", enabled = true, name = "InitiateLogin")
    public void login(RequestEvent e){
        if(e.getRequest().getData().containsKey("email") && e.getRequest().getData().containsKey("password")){
            String email = e.getRequest().getData().get("email");
            String password = e.getRequest().getData().get("password");
            try {
                List<User> users = User._find(User.class, "email=?", email).exec(User.class);
                if(users.size()>0){
                    User u = users.get(0);
                    if(User.hash(password).equals(u.getPassword())){
                        e.getSession().getSessionData().put("user", u);
                        EventPublisher.raiseEvent(new UserLoggedIn(e.getSession(), u), true, null);
                    }else{
                        // failed to login, password incorrect
                    }
                }else{
                    // failed to login user not found
                }
            }catch(Exception err){

            }
        }
    }
}
