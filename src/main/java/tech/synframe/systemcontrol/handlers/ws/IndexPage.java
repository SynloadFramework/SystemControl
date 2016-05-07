package tech.synframe.systemcontrol.handlers.ws;

import com.synload.eventsystem.events.RequestEvent;
import com.synload.framework.ws.annotations.WSEvent;
import tech.synframe.systemcontrol.elements.PageWrapper;
import tech.synframe.systemcontrol.elements.user.LoginForm;
import tech.synframe.systemcontrol.models.User;

public class IndexPage {
	@WSEvent(method = "get", action = "index", description = "default page that loads", enabled = true, name = "Template Index")
	public void gIndex(RequestEvent e){
		if(e.getSession().getSessionData().containsKey("user")){ // logged in?
			User user = (User) e.getSession().getSessionData().get("user");
			e.getSession().send(
				new PageWrapper(
					e.getSession(),
					e.getRequest().getTemplateCache(),
					user
				)
			);
		}else{ // not logged in
			e.getSession().send(
				new LoginForm(
					e.getSession(),
					e.getRequest().getTemplateCache()
				)
			);
		}
	}
}
