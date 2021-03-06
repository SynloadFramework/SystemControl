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
    public Process p=null;
    public Thread logwriter;
    public LogWriter writer = new LogWriter();
    public Runtime runtime;
    public int logPosition = 0;
    public boolean stopThread = false;
    public LinkedList<ConsoleLine> output = new LinkedList<ConsoleLine>();
    public static Map<Long, ExecuteShellSynFrame> instances = new HashMap<Long, ExecuteShellSynFrame>();
    public Thread thread = null;
    public ExecuteShellSynFrame(Project project){
        this.project = project;
    }
    public boolean isRunning() {
        if(p!=null) {
            try {
                p.exitValue();
                return false;
            } catch (Exception e) {
                return true;
            }
        }else{
            return false;
        }
    }
    public boolean stop(){
        if(p!=null) {
            p.destroy();
            stopThread = true;
            logwriter.interrupt();
            return true;
        }
        return false;
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
    public class ErrorLogs implements Runnable{
        public LinkedList<String> lines = new LinkedList<String>();
        private InputStreamReader isr = null;
        public ExecuteShellSynFrame shell;
        public ErrorLogs(InputStreamReader isr, ExecuteShellSynFrame shell){
            this.shell = shell;
            this.isr = isr;
        }
        public void run(){
            BufferedReader reader = new BufferedReader(isr);
            String line = "";
            try {
                while (!stopThread) {
                    while (!stopThread && (line = reader.readLine()) != null) {
                        shell.setLogPosition(shell.getLogPosition()+1);
                        shell.getWriter().lines.add(line);
                        System.out.println(line);
                        shell.getOutput().addLast(new ConsoleLine(line, shell.getLogPosition()));
                        if (shell.getOutput().size() > 50) {
                            shell.getOutput().removeFirst();
                        }

                        Thread.sleep(1L);
                    }
                    Thread.sleep(1L);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public void run(){
        Log.info("Started project "+this.project.getId(), ExecuteShellSynFrame.class);
        instances.put(this.project.getId(), this);
        //Log.info("started project", ExecuteShellSynFrame.class);
        try {
            runtime = Runtime.getRuntime();
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
            String command = "exec java " + project.getJava_arguments() + " -classpath \""+jars+"\" com.synload.framework.SynloadFramework" +
                " -sitepath " + this.project.getPath() +
                " -port " + this.project.getPort() +
                " -cb \"127.0.0.1:" + SynloadFramework.serverTalkPort + "&" + SynloadFramework.serverTalkKey + "\" " +
                " -id " + this.project.getId() +
                " -scb ";
            if(!(new File("./exec/")).exists()){
                (new File("./exec/")).mkdir();
            }
            new File("./exec/start-"+this.project.getId()).delete();
            PrintWriter writ = new PrintWriter("./exec/start-"+this.project.getId(), "UTF-8");
            writ.print(command);
            writ.close();
            Process proc = Runtime.getRuntime().exec( "chmod 755 ./exec/start-"+this.project.getId() );
            proc.waitFor();
            p = runtime.exec(
                "./exec/start-"+this.project.getId()
            );
            File logDirectory = new File("./log");
            if(!logDirectory.exists()){
                logDirectory.mkdir();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            new Thread(new ErrorLogs(new InputStreamReader(p.getErrorStream()), this)).start();
            String line = "";
            while(!stopThread) {
                while (!stopThread && (line = reader.readLine()) != null) {
                    logPosition++;
                    writer.lines.add(line);
                    output.addLast(new ConsoleLine(line, logPosition));
                    if (output.size() > 50) {
                        output.removeFirst();
                    }
                    Thread.sleep(1L);
                }
                Thread.sleep(1L);
            }
            //Log.info(output.toString(), ExecuteShellSynFrame.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.info("Stopped project "+this.project.getId(), ExecuteShellSynFrame.class);
        if(instances.containsKey(this.project.getId())){
            Log.info("Deleted instance of project "+this.project.getId(), ExecuteShellSynFrame.class);
            instances.remove(this.project.getId());
            p=null;
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

    public Thread getLogwriter() {
        return logwriter;
    }

    public void setLogwriter(Thread logwriter) {
        this.logwriter = logwriter;
    }

    public LogWriter getWriter() {
        return writer;
    }

    public void setWriter(LogWriter writer) {
        this.writer = writer;
    }

    public int getLogPosition() {
        return logPosition;
    }

    public void setLogPosition(int logPosition) {
        this.logPosition = logPosition;
    }

    public boolean isStopThread() {
        return stopThread;
    }

    public void setStopThread(boolean stopThread) {
        this.stopThread = stopThread;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }
}
