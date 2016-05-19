package tech.synframe.systemcontrol.handlers.ws;

import com.synload.eventsystem.EventPublisher;
import com.synload.eventsystem.events.RequestEvent;
import com.synload.framework.SynloadFramework;
import com.synload.framework.handlers.Data;
import com.synload.framework.ws.annotations.WSEvent;
import tech.synframe.systemcontrol.elements.PageWrapper;
import tech.synframe.systemcontrol.elements.user.LoginForm;
import tech.synframe.systemcontrol.events.UserLoggedIn;
import tech.synframe.systemcontrol.models.User;
import tech.synframe.systemcontrol.response.CallBackData;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Nathaniel on 5/6/2016.
 */
public class UserActions {
    @WSEvent(method = "get", action = "login", description = "Display login form", enabled = true, name = "DisplayLoginForm")
    public void loginForm(RequestEvent e){
        e.respond(
            new LoginForm(
                e.getSession(),
                e.getRequest().getTemplateCache()
            )
        );
    }
    @WSEvent(method = "post", action = "login", description = "initiate login", enabled = true, name = "InitiateLogin")
    public void login(RequestEvent e){
        HashMap<String, Object> feedback = new HashMap<String, Object>();
        if(e.getRequest().getData().containsKey("email") && e.getRequest().getData().containsKey("password")){
            String email = e.getRequest().getData().get("email");
            String password = e.getRequest().getData().get("password");
            try {
                List<User> users = User._find(User.class, "email=?", email).exec(User.class);
                if(users.size()>0){
                    final User u = users.get(0);
                    if(User.hash(password).equals(u.getPassword())){
                        e.getSession().getSessionData().put("user", u);
                        feedback.put("message", "Welcome back, <br/><center>"+u.getEmail()+"</center>");
                        feedback.put("status", "success");
                        EventPublisher.raiseEvent(new UserLoggedIn(e.getSession(), u, e.getRequest()), true, null);
                    }else{
                        feedback.put("message", "Password Incorrect");
                        feedback.put("status", "error");
                    }
                }else{
                    feedback.put("message", "User does not exist");
                    feedback.put("status", "error");
                }
            }catch(Exception err){
                err.printStackTrace();
                feedback.put("message", "System failure, exception thrown");
                feedback.put("status", "error");
            }
        }else{
            feedback.put("message", "Make sure to fill out the form");
            feedback.put("status", "error");
        }
        try {
            e.respond(
                new CallBackData(
                    feedback,
                    "login_feedback"
                )
            );
        }catch (Exception err){
            err.printStackTrace();
        }
    }
    @WSEvent(method = "post", action = "register", description = "create new user", enabled = true, name = "InitiateRegister")
    public void register(RequestEvent e){
        HashMap<String, Object> feedback = new HashMap<String, Object>();
        if(e.getRequest().getData().containsKey("email") && e.getRequest().getData().containsKey("password") && e.getRequest().getData().containsKey("country")){
            String email = e.getRequest().getData().get("email");
            String password = e.getRequest().getData().get("password");
            String country = e.getRequest().getData().get("country");
            try{
                List<User> users = User._find(User.class, "email=?", email).exec(User.class);
                if(users.size()==0) {
                    try {
                        User user = new User();
                        user.setEmail(email);
                        user.setPassword(User.hash(password));
                        user.setCountry(country);
                        user.setProjects("");
                        user._insert();
                        if (user.getId() > 0) {
                            feedback.put("message", "Account created, redirecting to login.");
                            feedback.put("status", "success");
                            e.getSession().send(
                                new LoginForm(
                                    e.getSession(),
                                    e.getRequest().getTemplateCache()
                                )
                            );
                        } else {
                            feedback.put("message", "System failure");
                            feedback.put("status", "error");
                        }
                    } catch (Exception err) {
                        err.printStackTrace();
                    }
                }else{
                    feedback.put("message", "Email exists");
                    feedback.put("status", "error");
                }
            } catch (Exception err) {
                err.printStackTrace();
                feedback.put("message", "System failure, exception thrown");
                feedback.put("status", "error");
            }
        }else{
            feedback.put("message", "Fill out the form!");
            feedback.put("status", "error");
        }
        try {
            e.respond(
                new CallBackData(
                    feedback,
                    "register_feedback"
                )
            );
        }catch (Exception err){
            err.printStackTrace();
        }
    }
    @WSEvent(method = "post", action = "logout", description = "logout of a session", enabled = true, name = "InitiateLogout")
    public void logout(RequestEvent e){
        if(e.getSession().getSessionData().containsKey("user")){ // logged in?
            e.getSession().getSessionData().remove("user");
            e.respond(
                new LoginForm(
                    e.getSession(),
                    e.getRequest().getTemplateCache()
                )
            );
        }
    }
}
