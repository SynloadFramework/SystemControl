package tech.synframe.systemcontrol.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nathaniel on 5/2/2016.
 */
public class ExecuteShellSynFrame implements Runnable{
    public String instance;
    public String directory;
    public String port;
    public Process p;
    public StringBuffer output = new StringBuffer();
    public static Map<String, ExecuteShellSynFrame> instances = new HashMap<String, ExecuteShellSynFrame>();
    public ExecuteShellSynFrame(String instance, String directory, String port){
        this.instance = instance;
        this.port = port;
        this.directory = directory;
    }
    boolean isRunning() {
        try {
            p.exitValue();
            return false;
        } catch (Exception e) {
            return true;
        }
    }
    public void run(){
        instances.put(this.instance, this);
        try {
            p = Runtime.getRuntime().exec("./bin/SynloadFramework -sitepath "+directory+" -port "+port);
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while(this.isRunning()) {
                String line = "";
                while ((line = reader.readLine()) != null) {
                    output.append(line + "\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        instances.remove(this.instance);
    }
}
