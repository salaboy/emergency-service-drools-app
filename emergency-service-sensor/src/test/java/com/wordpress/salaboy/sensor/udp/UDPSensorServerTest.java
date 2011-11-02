/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.sensor.udp;

import com.wordpress.salaboy.sensor.SensorDataParser;
import com.wordpress.salaboy.sensor.SensorMessageProducer;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
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
public class UDPSensorServerTest {
    
    public final static int SERVER_PORT = 1555; 
    
    public UDPSensorServerTest() {
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

    /**
     * Test UDPSensorServer's life cycle.
     */
    @Test
    public void testServer() throws Exception {
        
        String message = "FAKE_DATA";
        
        MockSensorDataParser sensorDataParser = new MockSensorDataParser();
        MockSensorMessageProducer sensorMessageProducer = new MockSensorMessageProducer();
        
        //Creates a UDPSensorServer
        UDPSensorServer server = new UDPSensorServer(sensorDataParser, sensorMessageProducer);
        
        //server shouldn't be running
        assertFalse(server.isRunning());
        
        //starts the server
        server.startService(InetAddress.getLocalHost().getHostAddress(), SERVER_PORT,message.getBytes().length);
        
        //Waits until the server is up
        Thread.sleep(500);
        
        //server should be running now
        assertTrue(server.isRunning());
        
        //Test that the server is really running
        byte[] data = message.getBytes();
        DatagramSocket datagramSocket = new DatagramSocket();
        datagramSocket.send(new DatagramPacket(data, 0, data.length, InetAddress.getLocalHost(), SERVER_PORT));
        datagramSocket.close();

        Thread.sleep(1000);
        
        assertEquals(1, sensorDataParser.getParsedData().size());
        assertEquals(message, sensorDataParser.getParsedData().get(0));
        assertEquals(1, sensorMessageProducer.getInformedMessages().size());
        assertEquals("1.0", sensorMessageProducer.getInformedMessages().get(0).toString());
        
        //stops the server
        server.stopService();
        
        //server shouldn't be running anymore
        assertFalse(server.isRunning());
        
    }

}

class MockSensorDataParser implements SensorDataParser{

    private List<String> parsedData = new ArrayList<String>();
    
    @Override
    public double parseData(String data) {
        parsedData.add(data);
        return parsedData.size();
    }

    public List<String> getParsedData() {
        return parsedData;
    }

    @Override
    public boolean isValidData(String data) {
        return true;
    }
    
}

class MockSensorMessageProducer extends SensorMessageProducer{
    
    private List<Double> informedMessages = new ArrayList<Double>();

    public MockSensorMessageProducer() {
        super(null);
    }

    @Override
    public void informMessage(double heartBeat) throws Exception {
        informedMessages.add(heartBeat);
    }

    public List<Double> getInformedMessages() {
        return informedMessages;
    }
    
}
