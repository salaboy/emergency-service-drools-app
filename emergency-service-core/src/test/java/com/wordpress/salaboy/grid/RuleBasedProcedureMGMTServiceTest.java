/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.grid;

import com.wordpress.salaboy.model.Emergency;
import com.wordpress.salaboy.model.events.EmergencyEndsEvent;
import com.wordpress.salaboy.model.persistence.PersistenceServiceProvider;
import com.wordpress.salaboy.services.ProceduresMGMTServiceImpl;
import java.io.IOException;
import java.util.Date;
import org.junit.*;

/**
 *
 * @author salaboy
 */
public class RuleBasedProcedureMGMTServiceTest {

    public RuleBasedProcedureMGMTServiceTest() {
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
    public void initialTest() throws IOException, InterruptedException {

        Emergency emergency = new Emergency();
        PersistenceServiceProvider.getPersistenceService().storeEmergency(emergency);



        ProceduresMGMTServiceImpl.getInstance().newRequestedProcedure(emergency.getId(), "DumbProcedure", null);



        Thread.sleep(3000);

        ProceduresMGMTServiceImpl.getInstance().notifyProcedures(new EmergencyEndsEvent(emergency.getId(), new Date()));

        Thread.sleep(3000);
        
    }
}
