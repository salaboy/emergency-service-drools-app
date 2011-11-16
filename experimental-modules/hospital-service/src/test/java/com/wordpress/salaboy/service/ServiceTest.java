/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.wordpress.salaboy.service;

import com.wordpress.salaboy.hospital.HospitalService;
import com.wordpress.salaboy.hospital.HospitalServiceImpl;
import org.junit.*;

/**
 *
 * @author salaboy
 */
public class ServiceTest {
    
    public ServiceTest() {
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
    public void hello() throws InterruptedException {
        
        HospitalServiceImpl service = new HospitalServiceImpl();
        System.out.println("Available Bed = "+service.getAvailableBeds());
        Thread.sleep(2000);
        service.requestBed("myID");
        service.requestBed("myID2");
        
        Thread.sleep(2000);
        System.out.println("Available Bed = "+service.getAvailableBeds());
        System.out.println("Available Bed = "+service.getAvailableBeds());
        
        Thread.sleep(2000);
        service.requestBed("mySecondId");
        
        Thread.sleep(2000);
        System.out.println("Available Bed = "+service.getAvailableBeds());
        
       
        
        
        
        
    }
}
