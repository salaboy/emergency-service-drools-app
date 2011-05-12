/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.sensor.udp;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author esteban
 */
public class GenericUDPAccelerometerSensorParserTest {

    public GenericUDPAccelerometerSensorParserTest() {
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
    public void testUniqueData() {
        String data = "67";
        double expResult = 67.0;
        double result = doTest(data);
        assertEquals(expResult, result, 0.0);


        data = "145.62";
        expResult = 145.62;
        result = doTest(data);
        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testMultipleData() {
        String data = "ASD,67";
        double expResult = 67.0;
        double result = doTest(data);
        assertEquals(expResult, result, 0.0);


        data = "asd,fgd,swer,145.62";
        expResult = 145.62;
        result = doTest(data);
        assertEquals(expResult, result, 0.0);
    }

    @Test
    public void testInvalidPattern() {
        String data = null;
        try {
            data = "";
            doTest(data);
            fail("Exception expected!");
        } catch (NumberFormatException ex) {
            //OK
        }

        try {
            data = ",";
            doTest(data);
            fail("Exception expected!");
        } catch (ArrayIndexOutOfBoundsException ex) {
            //OK
        }

        try {
            data = "aasd";
            doTest(data);
            fail("Exception expected!");
        } catch (NumberFormatException ex) {
            //OK
        }

        try {
            data = "aasd,";
            doTest(data);
            fail("Exception expected!");
        } catch (NumberFormatException ex) {
            //OK
        }

    }

    private double doTest(String data) {
        GenericUDPAccelerometerSensorParser instance = new GenericUDPAccelerometerSensorParser();
        double result = instance.parseData(data);
        return result;
    }
}
