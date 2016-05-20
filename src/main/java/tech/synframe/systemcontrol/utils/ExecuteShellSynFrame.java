package tech.synframe.systemcontrol.utils;

import tech.synframe.systemcontrol.models.Project;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nathaniel on 5/2/2016.
 */
public class ExecuteShellSynFrame implements Runnable{
    public Project project;
    public Process p;
    public Runtime runtime;
    public StringBuffer output = new StringBuffer();
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
        try {
            runtime = Runtime.getRuntime();
            p = runtime.exec("./bin/SynloadFramework -sitepath "+this.project.getPath()+" -port "+this.project.getPort());
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
        instances.remove(this.project.getId());
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

    public StringBuffer getOutput() {
        return output;
    }

    public void setOutput(StringBuffer output) {
        this.output = output;
    }

    public static Map<Long, ExecuteShellSynFrame> getInstances() {
        return instances;
    }

    public static void setInstances(Map<Long, ExecuteShellSynFrame> instances) {
        ExecuteShellSynFrame.instances = instances;
    }
}
