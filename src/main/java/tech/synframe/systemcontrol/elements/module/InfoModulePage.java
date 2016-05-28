package tech.synframe.systemcontrol.elements.module;

import com.synload.framework.handlers.Response;
import com.synload.framework.modules.ModuleResource;
import com.synload.framework.ws.WSHandler;
import tech.synframe.systemcontrol.models.Modules;
import tech.synframe.systemcontrol.models.Project;
import tech.synframe.systemcontrol.models.User;
import tech.synframe.systemcontrol.utils.NewVersionCheck;
import java.io.*;
import java.security.MessageDigest;
import java.util.Formatter;
import java.util.List;

/**
 * Created by Nathaniel on 5/28/2016.
 */
public class InfoModulePage extends Response {
    public User user;
    public Project project = null;
    public Modules module = null;
    public String hash = "";
    public String info = null;
    public InfoModulePage(WSHandler session, List<String> templateCache, User u, Project p, Modules m){
        boolean foundAttempt = false;
        this.setTemplateId("modinfo-sc"); // set the template id (stored client side)
        if (!templateCache.contains(this.getTemplateId())) { // check to see if the client has this template
            try {
                this.setTemplate(this.getTemplate(new String(ModuleResource.get("systemcontrol", "templates/module/info.html"), "UTF-8"))); // retrieve template from memory
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        user = u;
        project = p;
        module = m;
        info = NewVersionCheck.getBuildInfoString(module, module.getBuild());
        if(!module.getFile().equals("")) {
            hash = sha1(new File(project.modulePath + module.getFile()));
        }
        this.setParent(".projectAction-wrapper"); // parent element, css selector
        this.setParentTemplate("wrapper"); // if element is not found, which template contains it. client side sends (method="get",action=ParentTemplate)
        this.setAction("alone"); // how to transition the element to the new content
        this.setPageId("modinfo-sc");
    }
    public String sha1(final File file){
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
                final byte[] buffer = new byte[1024];
                for (int read = 0; (read = is.read(buffer)) != -1; ) {
                    messageDigest.update(buffer, 0, read);
                }
            }
            // Convert the byte to hex format
            try (Formatter formatter = new Formatter()) {
                for (final byte b : messageDigest.digest()) {
                    formatter.format("%02x", b);
                }
                return formatter.toString();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
}
