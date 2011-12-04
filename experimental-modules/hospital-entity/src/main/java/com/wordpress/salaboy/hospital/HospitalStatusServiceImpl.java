/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.hospital;

import com.wordpress.salaboy.model.roles.Role;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.SystemEventListenerFactory;
import org.drools.builder.*;
import org.drools.io.impl.ClassPathResource;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.LiveQuery;
import org.drools.runtime.rule.QueryResultsRow;
import org.drools.runtime.rule.Row;
import org.drools.runtime.rule.ViewChangedEventListener;
import org.jbpm.process.workitem.wsht.SyncWSHumanTaskHandler;
import org.jbpm.task.Group;
import org.jbpm.task.User;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.TaskServiceSession;
import org.jbpm.task.service.local.LocalTaskService;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.compiler.ExpressionCompiler;

/**
 *
 * @author salaboy
 */
public class HospitalStatusServiceImpl implements HospitalStatusService {

    private StatefulKnowledgeSession ksession;
    public static final List updated = new ArrayList();
    public static final List removed = new ArrayList();
    public static final List added = new ArrayList();
    protected EntityManagerFactory emf;
    protected Map<String, User> users;
    protected Map<String, Role> roles;
    protected Map<String, Group> groups;
    protected TaskService taskService;
    protected TaskServiceSession taskSession;

    protected EntityManagerFactory createEntityManagerFactory() {
        return Persistence.createEntityManagerFactory("org.jbpm.task");
    }

    public HospitalStatusServiceImpl() {
        System.out.println("Creating new Hospital Service! " + System.currentTimeMillis());
        createKnowledgeBasedHospital();
    }

    public List<String> getSpecialities() {
        return null;
    }

    public String requestBed(String id) {
        System.out.println("Requesting a Bed " + System.currentTimeMillis());
        ksession.insert(new BedRequest(id));
        return UUID.randomUUID().toString();
    }

    private void createKnowledgeBasedHospital() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add(new ClassPathResource("hospital.drl"), ResourceType.DRL);
        kbuilder.add(new ClassPathResource("patientCheckIn.bpmn"), ResourceType.BPMN2);
        if (kbuilder.hasErrors()) {
            KnowledgeBuilderErrors errors = kbuilder.getErrors();

            for (KnowledgeBuilderError error : errors) {
                System.out.println(">>> Error:" + error.getMessage());

            }
            throw new IllegalStateException(">>> Knowledge couldn't be parsed! ");
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());


        ksession = kbase.newStatefulKnowledgeSession();

        initTaskService();

        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new SyncWSHumanTaskHandler(
                new LocalTaskService(taskSession), ksession));
        KnowledgeRuntimeLoggerFactory.newConsoleLogger(ksession);

        initRoles();

        new Thread() {

            @Override
            public void run() {
                ksession.fireUntilHalt();
            }
        }.start();


        Hospital hospital = new Hospital("Hospital 1", 1, 1);
        hospital.setAvailableBeds(30);
        ksession.insert(hospital);


        ViewChangedEventListener listener = new ViewChangedEventListener() {

            public void rowUpdated(Row row) {

                updated.add(row.get("$availableBeds"));
                System.out.println("Updating the Available Beds = " + row.get("$availableBeds"));
            }

            public void rowRemoved(Row row) {
                removed.add(row.get("$availableBeds"));
            }

            public void rowAdded(Row row) {
                added.add(row.get("$availableBeds"));
                System.out.println("Adding the Available Beds = " + row.get("$availableBeds"));
            }
        };

        // Open the LiveQuery
        LiveQuery query = ksession.openLiveQuery("getAvailableBeds", new Object[]{}, listener);




    }

    private void initTaskService() {
        emf = createEntityManagerFactory();

        taskService = new TaskService(emf, SystemEventListenerFactory.getSystemEventListener());
        taskSession = taskService.createSession();

        loadUsersAndGroups(taskService);
    }

    public int getAvailableBeds() {
        System.out.println("Get Available Beds! " + System.currentTimeMillis());
        org.drools.runtime.rule.QueryResults queryResults = ksession.getQueryResults("getAvailableBeds");
        for (QueryResultsRow row : queryResults) {
            return (Integer) row.get("$availableBeds");

        }

        return 0;

    }

    private void loadUsersAndGroups(TaskService taskService) {
        Map vars = new HashMap();

        Reader reader = null;

        try {
            reader = new InputStreamReader(new ClassPathResource("org/jbpm/task/LoadUsers.mvel").getInputStream());
            users = (Map<String, User>) eval(reader, vars);
            for (User user : users.values()) {

                taskSession.addUser(user);
            }
        } catch (IOException ex) {
            Logger.getLogger(HospitalStatusServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                reader = null;
            } catch (IOException ex) {
                Logger.getLogger(HospitalStatusServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        try {
            reader = new InputStreamReader(new ClassPathResource("org/jbpm/task/LoadGroups.mvel").getInputStream());
            groups = (Map<String, Group>) eval(reader, vars);
            for (Group group : groups.values()) {
                taskSession.addGroup(group);
            }
        } catch (IOException ex) {
            Logger.getLogger(HospitalStatusServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    Logger.getLogger(HospitalStatusServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private Object eval(Reader reader,
            Map vars) {
        try {
            return eval(toString(reader),
                    vars);
        } catch (IOException e) {
            throw new RuntimeException("Exception Thrown",
                    e);
        }
    }

    private String toString(Reader reader) throws IOException {
        StringBuilder sb = new StringBuilder(1024);
        int charValue;

        while ((charValue = reader.read()) != -1) {
            sb.append((char) charValue);
        }
        return sb.toString();
    }

    private Object eval(String str, Map vars) {
        ExpressionCompiler compiler = new ExpressionCompiler(str.trim());

        ParserContext context = new ParserContext();
        context.addPackageImport("org.jbpm.task");
        context.addPackageImport("org.jbpm.task.service");
        context.addPackageImport("org.jbpm.task.query");
        context.addPackageImport("java.util");
        context.addPackageImport("com.wordpress.salaboy.model.roles");

        vars.put("now", new Date());
        return MVEL.executeExpression(compiler.compile(context), vars);
    }

    private void initRoles() {
        try {
            Map vars = new HashMap();
            Reader reader = null;

            reader = new InputStreamReader(new ClassPathResource("org/jbpm/task/LoadRoles.mvel").getInputStream());
            roles = (Map<String, Role>) eval(reader, vars);
            
            for(Role role : roles.values()){
                ksession.insert(role);
            }
        } catch (IOException ex) {
            Logger.getLogger(HospitalStatusServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
