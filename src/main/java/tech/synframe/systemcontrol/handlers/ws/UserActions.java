package tech.synframe.systemcontrol.handlers.ws;

import com.synload.eventsystem.EventPublisher;
import com.synload.eventsystem.events.RequestEvent;
import com.synload.framework.handlers.Data;
import com.synload.framework.ws.annotations.WSEvent;
import tech.synframe.systemcontrol.elements.PageWrapper;
import tech.synframe.systemcontrol.elements.user.LoginForm;
import tech.synframe.systemcontrol.events.UserLoggedIn;
import tech.synframe.systemcontrol.models.User;

import java.util.HashMap;
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
                    final User u = users.get(0);
                    if(User.hash(password).equals(u.getPassword())){
                        e.getSession().getSessionData().put("user", u);
                        e.getSession().send(new Data(new HashMap<String, Object>() {{
                            put("message", "Welcome back, <br/><center>"+u.getEmail()+"</center>");
                            put("status", "success");
                        }}, "login_feedback"));
                        EventPublisher.raiseEvent(new UserLoggedIn(e.getSession(), u, e.getRequest()), true, null);
                    }else{
                        e.getSession().send(new Data(new HashMap<String, Object>() {{
                            put("message", "Password Incorrect");
                            put("status", "error");
                        }}, "login_feedback"));
                    }
                }else{
                    e.getSession().send(new Data(new HashMap<String, Object>() {{
                        put("message", "User does not exist");
                        put("status", "error");
                    }}, "login_feedback"));
                }
            }catch(Exception err){
                e.getSession().send(new Data(new HashMap<String, Object>() {{
                    put("message", "System failure, exception thrown");
                    put("status", "error");
                }}, "login_feedback"));
            }
        }else{
            e.getSession().send(new Data(new HashMap<String, Object>() {{
                put("message", "Make sure to fill out the form");
                put("status", "error");
            }}, "login_feedback"));
        }
    }
    @WSEvent(method = "post", action = "register", description = "create new user", enabled = true, name = "InitiateRegister")
    public void register(RequestEvent e){
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
                            e.getSession().send(new Data(new HashMap<String, Object>() {{
                                put("message", "Account created, redirecting to login.");
                                put("status", "success");
                            }}, "register_feedback"));
                            e.getSession().send(
                                new LoginForm(
                                    e.getSession(),
                                    e.getRequest().getTemplateCache()
                                )
                            );
                        } else {
                            e.getSession().send(new Data(new HashMap<String, Object>() {{
                                put("message", "System failure");
                                put("status", "error");
                            }}, "register_feedback"));
                        }
                    } catch (Exception err) {

                    }
                }else{
                    e.getSession().send(new Data(new HashMap<String, Object>() {{
                        put("message", "Email exists");
                        put("status", "error");
                    }}, "register_feedback"));
                }
            } catch (Exception err) {
                e.getSession().send(new Data(new HashMap<String, Object>() {{
                    put("message", "System failure, exception thrown");
                    put("status", "error");
                }}, "register_feedback"));
            }
        }else{
            e.getSession().send(new Data(new HashMap<String, Object>() {{
                put("message", "Fill out the form!");
                put("status", "error");
            }}, "register_feedback"));
        }
    }
    @WSEvent(method = "post", action = "logout", description = "logout of a session", enabled = true, name = "InitiateLogout")
    public void logout(RequestEvent e){
        if(e.getSession().getSessionData().containsKey("user")){ // logged in?
            e.getSession().getSessionData().remove("user");
            e.getSession().send(
                new LoginForm(
                    e.getSession(),
                    e.getRequest().getTemplateCache()
                )
            );
        }
    }
}
