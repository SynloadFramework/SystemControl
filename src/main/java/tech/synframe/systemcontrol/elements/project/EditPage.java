package tech.synframe.systemcontrol.elements.project;

import com.synload.framework.handlers.Response;
import com.synload.framework.modules.ModuleResource;
import com.synload.framework.ws.WSHandler;
import tech.synframe.systemcontrol.models.Project;
import tech.synframe.systemcontrol.models.User;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by Nathaniel on 5/22/2016.
 */
public class EditPage extends Response {
    public Project project = null;
    public String file;
    public String contents;
    public EditPage(WSHandler session, List<String> templateCache, Project p, String file){
        boolean foundAttempt = false;
        try {
            Pattern regex = Pattern.compile("/");
            Matcher regexMatcher = regex.matcher(file);
            foundAttempt = regexMatcher.find();
        } catch (PatternSyntaxException ex) {
            // Syntax error in the regular expression
        }
        if(foundAttempt){
            // hack attempt / send to future attempt log
        }else{
            File fileToOpen = new File(project.getPath()+file);
            this.setTemplateId("editpage-sc"); // set the template id (stored client side)
            if (!templateCache.contains(this.getTemplateId())) { // check to see if the client has this template
                try {
                    this.setTemplate(this.getTemplate(new String(ModuleResource.get("systemcontrol", "templates/project/edit.html"), "UTF-8"))); // retrieve template from memory
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            try {
                contents = new String(Files.readAllBytes(fileToOpen.toPath()),"UTF-8");
            }catch (Exception e){
                e.printStackTrace();
            }
            project = p;
            this.setParent(".projectAction-wrapper"); // parent element, css selector
            this.setParentTemplate("wrapper"); // if element is not found, which template contains it. client side sends (method="get",action=ParentTemplate)
            this.setAction("alone"); // how to transition the element to the new content
            this.setPageId("editProjectFile");
        }
    }
}
