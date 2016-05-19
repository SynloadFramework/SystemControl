package tech.synframe.systemcontrol.response;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.synload.framework.handlers.CallEvent;
import com.synload.framework.handlers.Data;

import java.util.HashMap;

/**
 * Created by Nathaniel on 5/8/2016.
 */

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class")
public class CallBackData extends Data{
    public String callEvent;
    public HashMap<String, Object> feedback;
    public CallBackData(HashMap<String, Object> feedback, String event){
        super(null, null);
        this.callEvent = event;
        this.feedback = new HashMap<String, Object>(feedback);
    }

    public HashMap<String, Object> getFeedback() {
        return feedback;
    }

    public String getCallEvent() {
        return callEvent;
    }
}
