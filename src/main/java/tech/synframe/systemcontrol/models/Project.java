package tech.synframe.systemcontrol.models;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.synload.framework.modules.annotations.sql.*;
import com.synload.framework.sql.Model;

/**
 * Created by Nathaniel on 5/5/2016.
 */

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class")
@SQLTable(name = "Project Model", version = 0.1, description = "Instances of synframe projects")
public class Project extends Model{
    @MediumIntegerColumn(length=20, AutoIncrement=true, Key=true)
    public long id;

    @StringColumn(length=60)
    public String name;

    @StringColumn(length=255)
    public String path;

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
}
