package tech.synframe.systemcontrol.utils;

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
    public Thread logwriter;
    public Runtime runtime;
    public boolean stopThread = false;
    public LinkedList<ConsoleLine> output = new LinkedList<ConsoleLine>();
    public static Map<Long, ExecuteShellSynFrame> instances = new HashMap<Long, ExecuteShellSynFrame>();
    public Thread thread = null;
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
        stopThread=true;
        logwriter.interrupt();
        p.destroy();
    }
    public class LogWriter implements Runnable{
        public LinkedList<String> lines = new LinkedList<String>();
        public void run(){
            while(!stopThread) {
                if(lines.size()!=0) {
                    try {
                        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("./log/" + project.getId() + ".log", true)));
                        out.println(lines.getFirst());
                        out.close();
                        lines.removeFirst();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(1L);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    public void run(){
        Log.info("Started project "+this.project.getId(), ExecuteShellSynFrame.class);
        instances.put(this.project.getId(), this);
        //Log.info("started project", ExecuteShellSynFrame.class);
        try {
            runtime = Runtime.getRuntime();
            LogWriter writer = new LogWriter();
            logwriter = new Thread(writer);
            logwriter.start();
            String jars="";
            File folder = new File("./lib/");
            File[] listOfFiles = folder.listFiles();
            String directory = (new File("./")).getAbsolutePath();
            directory = directory.substring(0,directory.length()-2);
            for(int i=0;i<listOfFiles.length;i++){
                if(!jars.equals("")){
                    jars=jars+":";
                }
                jars=jars+directory+"/lib/"+listOfFiles[i].getName();
            }
            String command = "java " + project.getJava_arguments() + " -classpath \""+jars+"\" com.synload.framework.SynloadFramework" +
                " -sitepath " + this.project.getPath() +
                " -port " + this.project.getPort() +
                " -cb \"127.0.0.1:" + SynloadFramework.serverTalkPort + "&" + SynloadFramework.serverTalkKey + "\" " +
                " -id " + this.project.getId() +
                " -scb ";
            if(!(new File("./exec/")).exists()){
                (new File("./exec/")).mkdir();
            }
            new File("./exec/start-"+this.project.getId()+".sh").delete();
            PrintWriter writ = new PrintWriter("./exec/start-"+this.project.getId()+".sh", "UTF-8");
            writ.print(command);
            writ.close();
            Process p = Runtime.getRuntime().exec( "chmod 755 ./exec/start-"+this.project.getId()+".sh" );
            p.waitFor();
            p = runtime.exec(
                "./exec/start-"+this.project.getId()+".sh"
            );
            File logDirectory = new File("./log");
            if(!logDirectory.exists()){
                logDirectory.mkdir();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            int id = 0;
            while(!stopThread) {
                while ((line = reader.readLine()) != null) {
                    id++;
                    writer.lines.add(line);
                    output.addLast(new ConsoleLine(line, id));
                    if (output.size() > 50) {
                        output.removeFirst();
                    }
                }
            }
            //Log.info(output.toString(), ExecuteShellSynFrame.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.info("Stopped project "+this.project.getId(), ExecuteShellSynFrame.class);
        if(instances.containsKey(this.project.getId())){
            Log.info("Deleted instance of project "+this.project.getId(), ExecuteShellSynFrame.class);
            instances.remove(this.project.getId());
        }
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
