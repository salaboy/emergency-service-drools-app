/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wordpress.salaboy.sensor.udp;

import com.wordpress.salaboy.sensor.SensorDataParser;
import com.wordpress.salaboy.sensor.SensorMessageProducer;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author esteban
 */
public class UDPSensorServer implements Runnable {

    private final static int DEFAULT_BUFFER_SIZE = 75;
    private boolean keepRunning;
    private DatagramSocket socket;
    private int bufferSize;
    private SensorDataParser sensorDataParser;
    private SensorMessageProducer sensorMessageProducer;
    
    private final Object PARSER_LOCK = new Object(); 
    private final Object PRODUCER_LOCK = new Object(); 

    public UDPSensorServer(SensorDataParser sensorDataParser, SensorMessageProducer sensorMessageProducer) {
        this.sensorDataParser = sensorDataParser;
        this.sensorMessageProducer = sensorMessageProducer;
    }

    public void startService(String address, int port) throws SocketException, UnknownHostException {
        this.startService(address, port, DEFAULT_BUFFER_SIZE);
    }

    public void startService(String address, int port, int bufferSize) throws SocketException, UnknownHostException {
        System.out.println("Starting UDP Sensor Server");
        this.bufferSize = bufferSize;

        System.out.println("Starting UDP Server on " + port + " port");
        
        if (address != null){
            socket = new DatagramSocket(port, InetAddress.getByName(address));
        } else{
            socket = new DatagramSocket(port);
        }

        keepRunning = true;

        new Thread(this).start();
    }

    public void stopService() {
        System.out.println("Stopping UDP Sensor Server");
        keepRunning = false;
        socket.close();
    }

    @Override
    public void run() {
        System.out.println("UDP Sensor Server ready to get data");
        while (keepRunning) {
            DatagramPacket datagramPacket = new DatagramPacket(new byte[bufferSize], bufferSize);
            try {
                socket.receive(datagramPacket);
                try {
                    String data = new String(datagramPacket.getData());
                    
                    Logger.getLogger(UDPSensorServer.class.getName()).log(Level.FINEST, "UDP Data Received= {0}", data);
                    
                    double message = 0D;
                    
                    boolean validData = false;
                    
                    synchronized(PARSER_LOCK){
                        validData = sensorDataParser.isValidData(data);
                        Logger.getLogger(UDPSensorServer.class.getName()).log(Level.FINEST, "\tis valid? {0}", validData);
                        if (validData){
                            message = sensorDataParser.parseData(data);
                        }
                        Logger.getLogger(UDPSensorServer.class.getName()).log(Level.FINEST, "\tvalue? {0}", message);
                    }
                    
                    synchronized(PRODUCER_LOCK){
                        if (validData){
                            sensorMessageProducer.informMessage(message);
                        }
                    }
                } catch (Exception ex) {
                    Logger.getLogger(UDPSensorServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (IOException ex) {
                Logger.getLogger(UDPSensorServer.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        System.out.println("UDP Sensor Server stopped");
    }
    
    public boolean isRunning(){
        return this.keepRunning;
    }

    public void setSensorDataParser(SensorDataParser sensorDataParser) {
        synchronized (PARSER_LOCK){
            this.sensorDataParser = sensorDataParser;
        }
    }

    public SensorDataParser getSensorDataParser() {
        synchronized (PARSER_LOCK){
            return sensorDataParser;
        }
    }

    public SensorMessageProducer getSensorMessageProducer() {
        synchronized(PRODUCER_LOCK){
            return sensorMessageProducer;
        }
    }

    public void setSensorMessageProducer(SensorMessageProducer sensorMessageProducer) {
        synchronized(PRODUCER_LOCK){
            this.sensorMessageProducer = sensorMessageProducer;
        }
    }
    
    
}
