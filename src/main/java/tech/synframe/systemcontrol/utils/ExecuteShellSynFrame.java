package tech.synframe.systemcontrol.utils;

import com.google.common.io.Files;
import com.synload.framework.Log;
import com.synload.framework.SynloadFramework;
import tech.synframe.systemcontrol.models.Project;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Nathaniel on 5/2/2016.
 */
public class ExecuteShellSynFrame implements Runnable{
    public Project project;
    public Process p;
    public Runtime runtime;
    public LinkedList<ConsoleLine> output = new LinkedList<ConsoleLine>();
    public static Map<Long, ExecuteShellSynFrame> instances = new HashMap<Long, ExecuteShellSynFrame>();
    public ExecuteShellSynFrame(Project project){
        this.project = project;
    }
    public boolean isRunning() {
        try {
            p.exitValue();
            return false;
        } catch (Exception e) {
            return true;
        }
    }
    public void stop(){
        p.destroy();
    }
    public void run(){
        instances.put(this.project.getId(), this);
        //Log.info("started project", ExecuteShellSynFrame.class);
        try {
            runtime = Runtime.getRuntime();
            p = runtime.exec(
                "./bin/SynloadFramework"+
                " -sitepath "+this.project.getPath()+
                " -port "+this.project.getPort()+
                " -cb 127.0.0.1:"+ SynloadFramework.serverTalkPort+"&"+SynloadFramework.serverTalkKey+
                " -id "+this.project.getId()+
                " -scb"
            );
            File logDirectory = new File("./log");
            if(!logDirectory.exists()){
                logDirectory.mkdir();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while(this.isRunning()) {
                String line = "";
                int id = 0;
                while((line = reader.readLine()) != null) {
                    id++;
                    try {
                        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("./log/"+project.getId()+".log", true)));
                        out.println(line);
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    output.addLast(new ConsoleLine(line,id));
                    if(output.size()>25){
                        output.removeFirst();
                    }
                }
            }
            Log.info(output.toString(), ExecuteShellSynFrame.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        instances.remove(this.project.getId());
        //Log.info("stopped project", ExecuteShellSynFrame.class);
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Process getP() {
        return p;
    }

    public void setP(Process p) {
        this.p = p;
    }

    public Runtime getRuntime() {
        return runtime;
    }

    public void setRuntime(Runtime runtime) {
        this.runtime = runtime;
    }

    public static Map<Long, ExecuteShellSynFrame> getInstances() {
        return instances;
    }

    public static void setInstances(Map<Long, ExecuteShellSynFrame> instances) {
        ExecuteShellSynFrame.instances = instances;
    }

    public LinkedList<ConsoleLine> getOutput() {
        return output;
    }

    public void setOutput(LinkedList<ConsoleLine> output) {
        this.output = output;
    }
}
