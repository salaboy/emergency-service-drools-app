package com.wordpress.salaboy;

import com.wordpress.salaboy.services.EmergencyService;
import bitronix.tm.TransactionManagerServices;
import com.wordpress.salaboy.log.ProcessEventLogger;
import com.wordpress.salaboy.services.TaskServerDaemon;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.event.DebugProcessEventListener;
import org.drools.event.rule.DebugAgendaEventListener;
import org.drools.event.rule.DebugWorkingMemoryEventListener;
import org.drools.io.impl.ClassPathResource;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.StatefulKnowledgeSession;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Created by IntelliJ IDEA.
 * User: salaboy
 * Date: 2/11/11
 * Time: 4:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class NewMyDroolsUtilities {
    public static final TaskServerDaemon taskServerDaemon = new TaskServerDaemon();

       public static StatefulKnowledgeSession createSession() {


           KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

           kbuilder.add(new ClassPathResource("EmergencyServiceV2.bpmn"), ResourceType.BPMN2);



           kbuilder.add(new ClassPathResource("newcallsHandling.drl"), ResourceType.DRL);


           if (kbuilder.hasErrors()) {
               for (KnowledgeBuilderError error : kbuilder.getErrors()) {
                   System.out.println(error);
               }
               throw new IllegalStateException("Error building kbase!");
           }

           KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

           kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());


           EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("org.drools.persistence.jpa");
                Environment env = KnowledgeBaseFactory.newEnvironment();
                   env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, entityManagerFactory);
                   env.set(EnvironmentName.TRANSACTION_MANAGER, TransactionManagerServices.getTransactionManager());

           StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);



           ksession.addEventListener(new ProcessEventLogger(EmergencyService.logger));
           ksession.addEventListener(new DebugAgendaEventListener());
           ksession.addEventListener(new DebugProcessEventListener());
           ksession.addEventListener(new DebugWorkingMemoryEventListener());

           return ksession;
       }











}
