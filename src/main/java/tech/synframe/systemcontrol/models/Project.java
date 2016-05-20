package tech.synframe.systemcontrol.models;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.synload.framework.modules.annotations.sql.*;
import com.synload.framework.sql.Model;
import tech.synframe.systemcontrol.utils.ExecuteShellSynFrame;

import java.io.File;
import java.sql.ResultSet;

/**
 * Created by Nathaniel on 5/5/2016.
 */

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class")
@SQLTable(name = "Project Model", version = 0.1, description = "Instances of synframe project")
public class Project extends Model{
    public Project(ResultSet rs) {
        super(rs);
        checkStatus();
    }

    public Project(Object... data) {
        super(data);
        checkStatus();
    }

    public long freeMemory = -1;
    public long totalMemory = -1;
    public long maxMemory = -1;
    public long totalSpace = -1;
    public long freeSpace = -1;
    public long usableSpace = -1;
    public String running = "u";

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

    @HasMany(of=PendingAction.class, key="id")
    @LongBlobColumn()
    public String pendingActions;

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
                freeMemory = this.instance().getRuntime().freeMemory();
                totalMemory = this.instance().getRuntime().totalMemory();
                maxMemory = this.instance().getRuntime().maxMemory();
                running="y";
            }else{
                running="n";
            }
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
}
