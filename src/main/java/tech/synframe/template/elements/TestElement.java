package tech.synframe.template.elements;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.synload.framework.handlers.Response;
import com.synload.framework.modules.ModuleLoader;
import com.synload.framework.ws.WSHandler;

public class TestElement extends Response {
	public TestElement(WSHandler user, List<String> templateCache){
		this.setTemplateId("template"); // set the template id (stored client side)
		if(!templateCache.contains(this.getTemplateId())){ // check to see if the client has this template
			try {
				this.setTemplate(this.getTemplate(new String(ModuleLoader.resources.get("template").get("index.html"),"UTF-8"))); // retrieve template from memory
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		this.setParent(".content"); // parent element, css selector
		this.setParentTemplate("wrapper"); // if element is not found, which template contains it. client side sends (method="get",action=ParentTemplate)
		this.setAction("alone"); // how to transition the element to the new content
		this.setPageId("templateIndex");
		this.setPageTitle("Template Index");
	}
}
