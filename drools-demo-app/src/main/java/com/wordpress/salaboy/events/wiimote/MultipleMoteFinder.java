package com.wordpress.salaboy.events.wiimote;


import motej.MoteFinderListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultipleMoteFinder {

	private Logger log = LoggerFactory.getLogger(MultipleMoteFinder.class);
	private MyMoteFinder finder;


	public void findMotes(MoteFinderListener listener) {
		if (finder == null) {
			finder = MyMoteFinder.getMoteFinder();
			//finder.bluetoothAddressCache.add("0023CC8AD195"); //0023CC8AD195	
			finder.addMoteFinderListener(listener);
		}		
                finder.startDiscovery();
	}

}
