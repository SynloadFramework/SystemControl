package tech.synframe.systemcontrol.response;

import com.synload.framework.handlers.CallEvent;

import java.util.HashMap;

/**
 * Created by Nathaniel on 5/8/2016.
 */
public class CallBackData extends CallEvent {
    HashMap<String, Object> feedback;
    public CallBackData(HashMap<String, Object> feedback, String event){
        this.event = event;
        this.feedback = new HashMap<String, Object>(feedback);
    }

    public HashMap<String, Object> getFeedback() {
        return feedback;
    }
}
