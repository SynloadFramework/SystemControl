package tech.synframe.systemcontrol.utils;

/**
 * Created by Nathaniel on 5/7/2016.
 */
public class ActionEnum {
    public enum Action {
        START(1),
        STOP(2);
        private int value;
        Action(int actionValue) {
            this.value = actionValue;
        }
    }
    public static Action parse(String action){
        for(Action a: Action.values()){
            if(a.name().toLowerCase().equals(action.toLowerCase())){
                return a;
            }
        }
        return null;
    }
}