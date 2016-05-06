package tech.synframe.systemcontrol.models;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.synload.framework.modules.annotations.sql.HasOne;
import com.synload.framework.modules.annotations.sql.MediumIntegerColumn;
import com.synload.framework.modules.annotations.sql.SQLTable;
import com.synload.framework.modules.annotations.sql.StringColumn;
import com.synload.framework.sql.Model;

import java.sql.ResultSet;

/**
 * Created by Nathaniel on 5/5/2016.
 */

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class")
@SQLTable(name = "pending Action", version = 0.1, description = "pending actions for projects")
public class PendingAction extends Model{
    public PendingAction(ResultSet rs) {
        super(rs);
    }

    public PendingAction(Object... data) {
        super(data);
    }
    @MediumIntegerColumn(length = 20, Key = true, AutoIncrement = true)
    public long id;

    @StringColumn(length = 60)
    public String action;

    @HasOne(of=Project.class, key = "id")
    @MediumIntegerColumn(length = 20)
    public long project;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public long getProject() {
        return project;
    }

    public void setProject(long project) {
        this.project = project;
    }
}
