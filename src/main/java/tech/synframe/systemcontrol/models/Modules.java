package tech.synframe.systemcontrol.models;

import com.synload.framework.modules.annotations.sql.*;
import com.synload.framework.sql.Model;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nathaniel on 5/26/2016.
 */
@SQLTable(name = "Modules", version = 0.3, description = "Modules running in the instances")
public class Modules extends Model {


    public Modules(ResultSet rs) {
        super(rs);
    }

    public Modules(Object... data) {
        super(data);
    }

    @MediumIntegerColumn(length=20, AutoIncrement=true, Key=true)
    public long id;

    @StringColumn(length=150)
    public String name;

    @MediumIntegerColumn(length = 4)
    public int build;

    @StringColumn(length=300)
    public String version;

    @StringColumn(length=300)
    public String jenkinsUrl;

    @HasOne(of=Project.class, key="id")
    @MediumIntegerColumn(length = 20)
    public long project;

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

    public int getBuild() {
        return build;
    }

    public void setBuild(int build) {
        this.build = build;
    }

    public String getJenkinsUrl() {
        return jenkinsUrl;
    }

    public void setJenkinsUrl(String projectUrl) {
        this.jenkinsUrl = projectUrl;
    }

    public long getProject() {
        return project;
    }

    public void setProject(long project) {
        this.project = project;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
