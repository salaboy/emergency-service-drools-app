/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.wordpress.salaboy.grid;

import com.wordpress.salaboy.MockUserInfo;
import com.wordpress.salaboy.services.DroolsServices;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.drools.SystemEventListenerFactory;
import org.drools.io.impl.ClassPathResource;
import org.jbpm.task.Group;
import org.jbpm.task.User;
import org.jbpm.task.service.SendIcal;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.TaskServiceSession;
import org.jbpm.task.service.mina.MinaTaskClientConnector;
import org.jbpm.task.service.mina.MinaTaskClientHandler;
import org.jbpm.task.service.mina.MinaTaskServer;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.compiler.ExpressionCompiler;

/**
 *
 * @author salaboy
 */
public class HumanTaskHelper {
    
    private static HumanTaskHelper instance;
    private EntityManagerFactory emf;
    private TaskService taskService;
    private TaskServiceSession taskSession;
    private MinaTaskServer server;
    
    private HumanTaskHelper() {
    }
    
    
    public static HumanTaskHelper getInstance(){
        if(instance == null){
            instance = new HumanTaskHelper();
        }
        return instance;
    }
    
    public void taskServerStart() throws Exception {
//        Properties conf = new Properties();
//        conf.setProperty("mail.smtp.host", "localhost");
//        conf.setProperty("mail.smtp.port", "2345");
//        conf.setProperty("from", "from@domain.com");
//        conf.setProperty("replyTo", "replyTo@domain.com");
//        conf.setProperty("defaultLanguage", "en-UK");
//        SendIcal.initInstance(conf);

        // Use persistence.xml configuration
        emf = Persistence.createEntityManagerFactory("org.jbpm.task");

        taskService = new TaskService(emf, SystemEventListenerFactory.getSystemEventListener());
        taskSession = taskService.createSession();
        MockUserInfo userInfo = new MockUserInfo();
        taskService.setUserinfo(userInfo);
        Map<String, Object> vars = new HashMap();

        Reader reader = new InputStreamReader(new ClassPathResource("LoadUsers.mvel").getInputStream());
        Map<String, User> users = (Map<String, User>) eval(reader, vars);
        for (User user : users.values()) {
            taskSession.addUser(user);
        }

        reader = new InputStreamReader(new ClassPathResource("LoadGroups.mvel").getInputStream());
        Map<String, Group> groups = (Map<String, Group>) eval(reader, vars);
        for (Group group : groups.values()) {
            taskSession.addGroup(group);
        }

        server = new MinaTaskServer(taskService);
        Thread thread = new Thread(server);
        thread.start();
        Thread.sleep(500);
        System.out.println("Server started ...");
    }

    public void taskServerStop() throws Exception {
        server.stop();
        taskSession.dispose();
        emf.close();
    }

    private Object eval(Reader reader, Map<String, Object> vars) {
        try {
            return eval(toString(reader), vars);
        } catch (IOException e) {
            throw new RuntimeException("Exception Thrown", e);
        }
    }

    private String toString(Reader reader) throws IOException {
        int charValue = 0;
        StringBuffer sb = new StringBuffer(1024);
        while ((charValue = reader.read()) != -1) {
            // result = result + (char) charValue;
            sb.append((char) charValue);
        }
        return sb.toString();
    }

    private Object eval(String str, Map<String, Object> vars) {
        ExpressionCompiler compiler = new ExpressionCompiler(str.trim());

        ParserContext context = new ParserContext();
        context.addPackageImport("org.jbpm.task");
        context.addPackageImport("org.jbpm.task.service");
        context.addPackageImport("org.jbpm.task.query");
        context.addPackageImport("java.util");

        vars.put("now", new Date());
        return MVEL.executeExpression(compiler.compile(context), vars);
    }
    
    public  TaskClient initTaskClient() {
        TaskClient client = new TaskClient(new MinaTaskClientConnector("client 1",
                new MinaTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
        boolean connected = client.connect("127.0.0.1", 9123);

        int retry = 0;
        while (!connected) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                java.util.logging.Logger.getLogger(HumanTaskHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
            connected = client.connect("127.0.0.1", 9123);
            if (!connected) {
                retry++;
            }
        }
        System.out.println("Client Connected after " + retry + " retries");
        return client;
    }
}
