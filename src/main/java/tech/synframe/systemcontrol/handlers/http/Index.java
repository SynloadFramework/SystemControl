package tech.synframe.systemcontrol.handlers.http;

import java.io.IOException;

import com.synload.framework.Log;
import com.synload.framework.http.HttpRequest;
import com.synload.framework.http.annotations.Get;
import com.synload.framework.http.annotations.MimeType;
import com.synload.framework.modules.ModuleLoader;
import com.synload.framework.modules.ModuleResource;

/*
 * Class wrapping http response methods
 */
public class Index {
	@Get("/") // HTTP get annotation ( registers as get request http://yoursite.com:8080/template )
	@MimeType("text/html") // returns OK from server and the mimetype content type
	public void gHtml(HttpRequest hr){
		hr.getResponse().setContentLength(ModuleResource.get("systemcontrol","templates/main.html").length);
		try {
			hr.getResponse().getOutputStream().write(ModuleResource.get("systemcontrol","templates/main.html"));
		} catch (IOException e) {
			Log.info(e.getMessage(), Index.class);
		}
	}
}
