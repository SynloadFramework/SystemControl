package tech.synframe.systemcontrol.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.synload.framework.Log;
import com.synload.framework.sql.annotations.*;
import com.synload.framework.sql.Model;
import tech.synframe.systemcontrol.utils.*;
import tech.synframe.systemcontrol.utils.Queue;

import java.io.File;
import java.sql.ResultSet;
import java.util.*;

/**
 * Created by Nathaniel on 5/5/2016.
 */

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class")
@SQLTable(name = "Project Model", version = 0.4, description = "Instances of synframe project")
public class Project extends Model{

    public static Map<Long, Map<String, Object>> projectStatistics = new HashMap<Long, Map<String, Object>>();

    public class StatusThread implements Runnable{
        public void run(){
            while(true) {
                checkStatus();
                try {
                    Thread.sleep(100);
                }catch (Exception e){

                }
            }
        }
    }
    public Project(ResultSet rs) {
        super(rs);
        new Thread(new StatusThread()).start();
    }

    public Project(Object... data) {
        super(data);
        new Thread(new StatusThread()).start();
    }

    public long freeMemory = -1;
    public long totalMemory = -1;
    public long maxMemory = -1;
    public long totalSpace = -1;
    public long freeSpace = -1;
    public int clients = 0;
    public long wsSent = 0;
    public long wsReceive = 0;
    public String defaultPath = "";
    public String configPath = "";
    public String modulePath = "";
    public List<Object> modules = new ArrayList<Object>();
    public long usableSpace = -1;
    public String running = "u";

    @JsonIgnore
    public HashMap<String, Properties> moduleProperties = null;

    @JsonIgnore
    public Properties instanceProperties;

    @MediumIntegerColumn(length=20, AutoIncrement=true, Key=true)
    public long id;

    @StringColumn(length=60)
    public String name;

    @StringColumn(length=255)
    public String path;

    @MediumIntegerColumn(length = 4)
    public int port;

    @HasOne(of=User.class, key="id")
    @MediumIntegerColumn(length = 20)
    public long user;

    @StringColumn(length=300)
    public String java_arguments;

    @MediumIntegerColumn(length = 1)
    public int autostart;

    @HasMany(of=PendingAction.class, key="id")
    @LongBlobColumn()
    public String pendingActions;

    @MediumIntegerColumn(length = 1)
    public int autoUpdate;

    @HasMany(of=Modules.class, key="id")
    @LongBlobColumn()
    public String installedModules;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getUser() {
        return user;
    }

    public void setUser(long user) {
        this.user = user;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void checkStatus(){
        if(this.instance()!=null){
            if(this.instance().isRunning()){
                if(projectStatistics.containsKey(this.getId())){
                    if(projectStatistics.get(this.getId()).containsKey("free")){
                        freeMemory = (Long) projectStatistics.get(this.getId()).get("free");
                    }
                    if(projectStatistics.get(this.getId()).containsKey("total")){
                        totalMemory = (Long) projectStatistics.get(this.getId()).get("total");
                    }
                    if(projectStatistics.get(this.getId()).containsKey("max")){
                        maxMemory = (Long) projectStatistics.get(this.getId()).get("max");
                    }
                    if(projectStatistics.get(this.getId()).containsKey("clients")){
                        clients = (Integer) projectStatistics.get(this.getId()).get("clients");
                    }
                    if(projectStatistics.get(this.getId()).containsKey("wsSent")){
                        wsSent = (Long) projectStatistics.get(this.getId()).get("wsSent");
                    }
                    if(projectStatistics.get(this.getId()).containsKey("wsReceive")){
                        wsReceive = (Long) projectStatistics.get(this.getId()).get("wsReceive");
                    }
                    if(projectStatistics.get(this.getId()).containsKey("defaultPath")) {
                        defaultPath = (String) projectStatistics.get(this.getId()).get("defaultPath");
                    }
                    if(projectStatistics.get(this.getId()).containsKey("modulePath")) {
                        modulePath = (String) projectStatistics.get(this.getId()).get("modulePath");
                    }
                    if(projectStatistics.get(this.getId()).containsKey("configPath")) {
                        configPath = (String) projectStatistics.get(this.getId()).get("configPath");
                    }
                    if(projectStatistics.get(this.getId()).containsKey("moduleProperties")) {
                        moduleProperties = new HashMap<String, Properties>((HashMap<String, Properties>)projectStatistics.get(this.getId()).get("moduleProperties"));
                        for(Map.Entry<String, Properties> mod : moduleProperties.entrySet()){
                            //Log.info("d="+mod.getValue().toString(), Project.class);
                            if(mod.getValue()==null){
                                Log.info("error, property data for "+mod.getKey(), Project.class);
                            }else {
                                Object[] newModData = new Object[]{mod.getKey(), mod.getValue().getProperty("author"), mod.getValue().getProperty("build"), mod.getValue().getProperty("version"), mod.getValue().getProperty("jenkins")};
                                boolean exists = false;
                                for(Object modData : modules){
                                   if(mod.getKey().equals((String)((Object[])modData)[0])){
                                       if(((Object[])modData).equals(newModData)) {
                                           exists = true;
                                       }else{
                                           modules.remove(modData);
                                       }
                                       break;
                                   }
                                }
                                if(!exists) {
                                    modules.add(newModData);
                                }
                            }
                        }
                    }
                    if(projectStatistics.get(this.getId()).containsKey("instanceProperties")) {
                        instanceProperties = (Properties) projectStatistics.get(this.getId()).get("instanceProperties");
                    }
                }
                running="y";
            }else{
                freeMemory=0;
                totalMemory=0;
                maxMemory=0;
                wsReceive=0;
                wsSent=0;
                clients=0;
                modules = new ArrayList<Object>();
                running="n";
            }
        }else{
            freeMemory=0;
            totalMemory=0;
            maxMemory=0;
            wsReceive=0;
            wsSent=0;
            clients=0;
            modules = new ArrayList<Object>();
            running="n";
        }
        try {
            File directory = new File(this.getPath());
            totalSpace = directory.getTotalSpace();
            freeSpace = directory.getFreeSpace();
            usableSpace = directory.getUsableSpace();
        }catch(Exception e){}
    }
    public ExecuteShellSynFrame instance(){
        if(ExecuteShellSynFrame.instances.containsKey(getId())){
            return ExecuteShellSynFrame.instances.get(getId());
        }
        return null;
    }
    public void start(){
        try {
            PendingAction pa = new PendingAction();
            pa.setAction("start");
            pa.setProject(this.getId());
            pa._insert();
            Queue.add(pa);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void stop(){
        try {
            PendingAction pa = new PendingAction();
            pa.setAction("stop");
            pa.setProject(this.getId());
            pa._insert();
            Queue.add(pa);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Map<Long, Map<String, Object>> getProjectStatistics() {
        return projectStatistics;
    }

    public static void setProjectStatistics(Map<Long, Map<String, Object>> projectStatistics) {
        Project.projectStatistics = projectStatistics;
    }

    public long getFreeMemory() {
        return freeMemory;
    }

    public void setFreeMemory(long freeMemory) {
        this.freeMemory = freeMemory;
    }

    public long getTotalMemory() {
        return totalMemory;
    }

    public void setTotalMemory(long totalMemory) {
        this.totalMemory = totalMemory;
    }

    public long getMaxMemory() {
        return maxMemory;
    }

    public void setMaxMemory(long maxMemory) {
        this.maxMemory = maxMemory;
    }

    public long getTotalSpace() {
        return totalSpace;
    }

    public void setTotalSpace(long totalSpace) {
        this.totalSpace = totalSpace;
    }

    public long getFreeSpace() {
        return freeSpace;
    }

    public void setFreeSpace(long freeSpace) {
        this.freeSpace = freeSpace;
    }

    public int getClients() {
        return clients;
    }

    public void setClients(int clients) {
        this.clients = clients;
    }

    public String getDefaultPath() {
        return defaultPath;
    }

    public void setDefaultPath(String defaultPath) {
        this.defaultPath = defaultPath;
    }

    public String getConfigPath() {
        return configPath;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    public String getModulePath() {
        return modulePath;
    }

    public void setModulePath(String modulePath) {
        this.modulePath = modulePath;
    }

    public HashMap<String, Properties> getModuleProperties() {
        return moduleProperties;
    }

    public void setModuleProperties(HashMap<String, Properties> moduleProperties) {
        this.moduleProperties = moduleProperties;
    }

    public Properties getInstanceProperties() {
        return instanceProperties;
    }

    public void setInstanceProperties(Properties instanceProperties) {
        this.instanceProperties = instanceProperties;
    }

    public long getUsableSpace() {
        return usableSpace;
    }

    public void setUsableSpace(long usableSpace) {
        this.usableSpace = usableSpace;
    }

    public String getRunning() {
        return running;
    }

    public void setRunning(String running) {
        this.running = running;
    }

    public String getPendingActions() {
        return pendingActions;
    }

    public void setPendingActions(String pendingActions) {
        this.pendingActions = pendingActions;
    }

    public List<Object> getModules() {
        return modules;
    }

    public void setModules(List<Object> modules) {
        this.modules = modules;
    }

    public String getInstalledModules() {
        return installedModules;
    }

    public void setInstalledModules(String installedModules) {
        this.installedModules = installedModules;
    }

    public int getAutoUpdate() {
        return autoUpdate;
    }

    public void setAutoUpdate(int autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    public int getAutostart() {
        return autostart;
    }

    public void setAutostart(int autostart) {
        this.autostart = autostart;
    }

    public String getJava_arguments() {
        return java_arguments;
    }

    public void setJava_arguments(String java_arguments) {
        this.java_arguments = java_arguments;
    }

    public long getWsSent() {
        return wsSent;
    }

    public void setWsSent(long wsSent) {
        this.wsSent = wsSent;
    }

    public long getWsReceive() {
        return wsReceive;
    }

    public void setWsReceive(long wsReceive) {
        this.wsReceive = wsReceive;
    }
}
