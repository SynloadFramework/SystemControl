package tech.synframe.template.handlers.ws;

import com.synload.eventsystem.events.RequestEvent;
import com.synload.framework.ws.annotations.WSEvent;
import tech.synframe.template.elements.TestElement;

public class IndexPage {
	@WSEvent(method = "get", action = "index", description = "default page that loads", enabled = true, name = "Template Index")
	public void gIndex(RequestEvent e){
		e.getSession().send(
			new TestElement(
				e.getSession(),
				e.getRequest().getTemplateCache()
			)
		);
	}
}
