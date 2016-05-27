package tech.synframe.systemcontrol.utils;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.synload.framework.Log;
import org.apache.commons.io.FileUtils;
import tech.synframe.systemcontrol.models.Modules;
import tech.synframe.systemcontrol.models.PendingAction;
import tech.synframe.systemcontrol.models.Project;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Nathaniel on 5/26/2016.
 */
public class NewVersionCheck implements Runnable {
    public static List<Project> updated = new ArrayList<Project>();
    public void run(){
        while(true){
            HashMap<Long, Project> projects = new HashMap<Long, Project>();
            try {
                List<Modules> modules = Modules._find(Modules.class, "").exec(Modules.class);
                for(Modules m: modules) {
                    if(!m.getName().equalsIgnoreCase("synloadframework")) {
                        Project project = null;
                        if (projects.containsKey(m.getProject())) {
                            project = projects.get(m.getProject());
                        } else {
                            List<Project> prs = m._related(Project.class).exec(Project.class);
                            if (prs.size() > 0) {
                                project = m._related(Project.class).exec(Project.class).get(0);
                                projects.put(m.getProject(), project);
                            }
                        }
                        if (project.getAutoUpdate() == 1) {
                            int updated = checkUpdateModule(m, project);
                            if (updated != -1) {
                                updateProject(m, project, updated);
                            }
                        }
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            for(Project p : updated){
                try {
                    PendingAction pStop = new PendingAction();
                    pStop.setAction("stop");
                    pStop.setProject(p.getId());
                    pStop._insert();
                    Queue.add(pStop);
                    PendingAction pStart = new PendingAction();
                    pStart.setAction("start");
                    pStart.setProject(p.getId());
                    pStart._insert();
                    Queue.add(pStart);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(1000);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    public int checkUpdateModule(Modules m, Project project){
        try {
            String apiRequest = m.getJenkinsUrl();
            if(!apiRequest.endsWith("/")){
                apiRequest = apiRequest + "/";
            }
            apiRequest=apiRequest+"api/json?tree=builds[number]";
            //Log.info(Unirest.post(apiRequest).asString().getBody(),NewVersionCheck.class);
            HttpResponse<JsonNode> response = Unirest.post(apiRequest).asJson();
            if(response.getBody().getObject().has("builds")){
                if(response.getBody().getObject().getJSONArray("builds").length()>0){
                    if(response.getBody().getObject().getJSONArray("builds").getJSONObject(0).has("number")){
                        if(response.getBody().getObject().getJSONArray("builds").getJSONObject(0).getInt("number")>m.getBuild()){
                            return response.getBody().getObject().getJSONArray("builds").getJSONObject(0).getInt("number");
                        }
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return -1;
    }
    public JsonNode getBuildInfo(Modules m, int build){
        String apiRequest = m.getJenkinsUrl();
        if(!apiRequest.endsWith("/")){
            apiRequest = apiRequest + "/";
        }
        apiRequest=apiRequest+build+"/api/json";
        try {
            //Log.info(Unirest.post(apiRequest).asString().getBody(),NewVersionCheck.class);
            return Unirest.post(apiRequest).asJson().getBody();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public void deleteBuild(Modules m, Project p, JsonNode jn){
        if(jn.getObject().has("artifacts")){
            if(jn.getObject().getJSONArray("artifacts").getJSONObject(0).length()>0) {
                String filename = jn.getObject().getJSONArray("artifacts").getJSONObject(0).getString("fileName");
                try {
                    String mPath = p.getModulePath();
                    if(!mPath.endsWith("/")){
                        mPath=mPath+"/";
                    }
                    new File(mPath+filename).delete();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    public void downloadModule(Modules m, Project p, JsonNode jn){
        if(jn.getObject().has("artifacts")){
            if(jn.getObject().getJSONArray("artifacts").getJSONObject(0).length()>0) {
                String jenkins = m.getJenkinsUrl();
                if(!jenkins.endsWith("/")){
                    jenkins=jenkins+"/";
                }
                try {
                    String mPath = p.getModulePath();
                    if(!mPath.endsWith("/")){
                        mPath=mPath+"/";
                    }
                    int number = jn.getObject().getInt("number");
                    String downloadUrl = jenkins+number+"/artifact/"+jn.getObject().getJSONArray("artifacts").getJSONObject(0).getString("relativePath");
                    String filename = jn.getObject().getJSONArray("artifacts").getJSONObject(0).getString("fileName");
                    if(!(new File("./artifactCache/")).exists()){
                        (new File("./artifactCache/")).mkdir();
                    }
                    if(!(new File("./artifactCache/"+number+"-"+filename)).exists()){
                        FileUtils.copyURLToFile(new URL(downloadUrl), new File("./artifactCache/"+number+"-"+filename));
                    }
                    FileUtils.copyFile(new File("./artifactCache/"+number+"-"+filename), new File(mPath+filename));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    public void updateProject(Modules m, Project project, int latestBuild){
        try {
            JsonNode latest = getBuildInfo(m, latestBuild);
            if(latest.getObject().getString("result").equalsIgnoreCase("success")) {
                JsonNode previous = getBuildInfo(m, m.getBuild());
                if(previous!=null && latest!=null) {

                    deleteBuild(m, project, previous); // delete old version

                    downloadModule(m, project, latest);

                    m.setBuild(latestBuild);

                    m._save("build",latestBuild);

                    if(updated.contains(project))
                        updated.add(project);

                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
