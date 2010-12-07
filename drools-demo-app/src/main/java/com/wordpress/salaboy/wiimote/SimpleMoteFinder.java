package com.wordpress.salaboy.wiimote;


import motej.Mote;
import motej.MoteFinderListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleMoteFinder implements MoteFinderListener {

	private Logger log = LoggerFactory.getLogger(SimpleMoteFinder.class);
	private MyMoteFinder finder;
	private Object lock = new Object();
	private Mote mote;

	public void moteFound(Mote mote) {
		//log.info("SimpleMoteFinder received notification of a found mote.");
		this.mote = mote;
		this.mote.rumble(2000l);
		String blueId = this.mote.getBluetoothAddress();
		System.out.println("Your Bluethooth id is = "+blueId);
		synchronized(lock) {
			lock.notifyAll();
		}
	}

	public Mote findMote() {
		if (finder == null) {
			finder = MyMoteFinder.getMoteFinder();
			//finder.bluetoothAddressCache.add("0023CC8AD195"); //0023CC8AD195	
			finder.addMoteFinderListener(this);
		}
		finder.startDiscovery();
		try {
			synchronized(lock) {
				lock.wait();
			}
		} catch (InterruptedException ex) {
			return null;
		}
		return mote;
	}

}
