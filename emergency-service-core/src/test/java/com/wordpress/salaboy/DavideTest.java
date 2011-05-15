/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy;

import java.util.LinkedList;
import java.util.List;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBaseConfiguration;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.io.impl.ByteArrayResource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author salaboy
 */
public class DavideTest {
    
    public DavideTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
     @Test
     public void hello() {
          String src = "\n" +
                 "package org.drools;\n" +
                 "\n" +
                 "global java.util.List list;\n" +
                 "\n" +
                 "\n" +
                 "declare A\n" +
                 "@role(event)\n" +
                 "end\n" +
                 "\n" +
                 "declare B\n" +
                 "@role(event)\n" +
                 "end\n" +
                 "\n" +
                 "declare C\n" +
                 "@role(event)\n" +
                 "end\n" +
                 "\n" +
                 "rule \"init\"\n" +
                 "when\n" +
                 "then\n" +
                 "    insert( new A() );\n" +
                 "    insert( new B() );\n" +
                 "    insert( new C() );\n" +
                 "end\n" +
                 "\n" +
                 "rule \"SUpp\"\n" +
                 "when\n" +
                 "    $a : A()\n" +
                 "    not  B( this after[0,100ms] $a )\n" +
                 "    not  C( this after[0,100ms] $a )\n" +
                 "then\n" +
                 "    insertLogical(\"zz\");\n" +
                 "end";


        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
            kbuilder.add(new ByteArrayResource(src.getBytes()),ResourceType.DRL);
        RuleBaseConfiguration rbconf = new RuleBaseConfiguration();
        rbconf.setEventProcessingMode(EventProcessingOption.STREAM);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(rbconf);

        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

        StatefulKnowledgeSession kSession = kbase.newStatefulKnowledgeSession( );

        List<Object> list = new LinkedList<Object>();
        kSession.setGlobal("list",list);


         kSession.fireAllRules();
     }
}
