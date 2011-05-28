package com.wordpress.salaboy.sensor.wii;

/*
 * Copyright 2007-2008 Volker Fritzsch
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.swing.event.EventListenerList;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.bluetooth.BlueCoveConfigProperties;
import com.intel.bluetooth.BlueCoveImpl;
import com.intel.bluetooth.DebugLog;

/**
 * 
 * <p>
 * @author Volker Fritzsch
 * @author Christoph Krichenbauer
 */
public class MyDeviceFinder {
    
    // initialization on demand holder idiom
    private static class SingletonHolder {

        private static final MyDeviceFinder INSTANCE = new MyDeviceFinder();
    }

    /**
     * Returns the <code>WiimoteFinder</code> instance.
     * 
     * @return WiimoteFinder
     */
    public static MyDeviceFinder getDeviceFinder() {
        try {
            // disable PSM minimum flag because the wiimote has a PSM below 0x1001
            //System.setProperty(BlueCoveConfigProperties.PROPERTY_JSR_82_PSM_MINIMUM_OFF, "true");
            BlueCoveImpl.setConfigProperty(BlueCoveConfigProperties.PROPERTY_DEBUG, "false" );
            BlueCoveImpl.setConfigProperty(BlueCoveConfigProperties.PROPERTY_DEBUG_STDOUT, "false" );
            BlueCoveImpl.setConfigProperty(BlueCoveConfigProperties.PROPERTY_INQUIRY_DURATION, "5000" );
            BlueCoveImpl.setConfigProperty(BlueCoveConfigProperties.PROPERTY_CONNECT_TIMEOUT, "10000" );
            
            
            
            DebugLog.setDebugEnabled(false);
            
            SingletonHolder.INSTANCE.localDevice = LocalDevice.getLocalDevice();
            SingletonHolder.INSTANCE.discoveryAgent = SingletonHolder.INSTANCE.localDevice.getDiscoveryAgent();
            
            
            return SingletonHolder.INSTANCE;
        } catch (BluetoothStateException ex) {
            throw new RuntimeException(ex);
        }
    }
    private Logger log = LoggerFactory.getLogger(MyDeviceFinder.class);
    private EventListenerList listenerList = new EventListenerList();
    private DiscoveryAgent discoveryAgent;
    public Set<String> bluetoothAddressCache = new HashSet<String>();
    private Set<DiscoveryListener> discoveryListeners;

    public void addDiscoveryListener(DiscoveryListener listener) {
        if (discoveryListeners == null) {
            discoveryListeners = new HashSet<DiscoveryListener>();
        }

        discoveryListeners.add(listener);
    }

    public void removeDiscoveryListener(DiscoveryListener listener) {
        if (discoveryListeners != null) {
            discoveryListeners.remove(listener);
        }
    }
    protected final DiscoveryListener listener = new DiscoveryListener() {

        @Override
        public void deviceDiscovered(final RemoteDevice device, DeviceClass clazz) {
            if (log.isInfoEnabled()) {
                try {
                    log.info("found device: " + device.getFriendlyName(false)
                            + " - " + device.getBluetoothAddress() + " - "
                            + clazz.getMajorDeviceClass() + ":"
                            + clazz.getMinorDeviceClass() + " - "
                            + clazz.getServiceClasses());
                } catch (IOException ex) {
                    log.error(ex.getMessage(), ex);
                    throw new RuntimeException(ex);
                }
            }


          
            
        }

        @Override
        public void inquiryCompleted(int discType) {
            if (discType == DiscoveryListener.INQUIRY_COMPLETED) {
                if (log.isInfoEnabled()) {
                    log.info("inquiry completed");
                }
            }

            if (discType == DiscoveryListener.INQUIRY_TERMINATED) {
                if (log.isInfoEnabled()) {
                    log.info("inquiry terminated");
                }
            }

            if (discType == DiscoveryListener.INQUIRY_ERROR) {
                if (log.isInfoEnabled()) {
                    log.info("inquiry error");
                }
            }


        }

        @Override
        public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
            // internally unused

            for (DiscoveryListener delegate : discoveryListeners) {
                delegate.servicesDiscovered(transID, servRecord);
            }
        }

        @Override
        public void serviceSearchCompleted(int transID, int respCode) {
            // internally unused

            for (DiscoveryListener delegate : discoveryListeners) {
                delegate.serviceSearchCompleted(transID, respCode);
            }
        }
    };
    private LocalDevice localDevice;

    private MyDeviceFinder() {
    }

    public void addDeviceFinderListener(DeviceFinderListener listener) {
        listenerList.add(DeviceFinderListener.class, listener);
    }

    protected void fireDeviceFound(Device device) {
        DeviceFinderListener[] listeners = listenerList.getListeners(DeviceFinderListener.class);
        for (DeviceFinderListener l : listeners) {
            l.deviceFound(device);
        }
    }

    public void removeDeviceFinderListener(DeviceFinderListener listener) {
        listenerList.remove(DeviceFinderListener.class, listener);
    }

    public void startDiscovery() {
        
        try {
            discoveryAgent.startInquiry(DiscoveryAgent.LIAC, listener);
        } catch (BluetoothStateException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void stopDiscovery() {
        discoveryAgent.cancelInquiry(listener);
    }
}
