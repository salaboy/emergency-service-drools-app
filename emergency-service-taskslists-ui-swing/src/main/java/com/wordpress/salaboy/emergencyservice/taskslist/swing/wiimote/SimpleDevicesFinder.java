package com.wordpress.salaboy.emergencyservice.taskslist.swing.wiimote;


import motej.Mote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleDevicesFinder implements DeviceFinderListener {

	private Logger log = LoggerFactory.getLogger(SimpleDevicesFinder.class);
	private MyDeviceFinder finder;
	private Object lock = new Object();
	private Mote mote;

	public void deviceFound(Device device) {
		log.info("New Device Was Found");
		
	}

	public Mote findDevices() {
		if (finder == null) {
			finder = MyDeviceFinder.getDeviceFinder();
			finder.addDeviceFinderListener(this);
		}
		finder.startDiscovery();
		try {
			synchronized(lock) {
				lock.wait();
			}
		} catch (InterruptedException ex) {
                    System.out.println(">>>> ex:"+ex.getMessage());
			return null;
		}
                finder.stopDiscovery();
		return mote;
	}
        
        public void stopFind(){
            if(mote != null){
                mote.disconnect();
            }
            
            finder.stopDiscovery();
        }

}
