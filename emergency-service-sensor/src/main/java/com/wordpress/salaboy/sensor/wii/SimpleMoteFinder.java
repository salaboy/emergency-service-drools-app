package com.wordpress.salaboy.sensor.wii;


import motej.Mote;
import motej.MoteFinderListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleMoteFinder implements MoteFinderListener {

	private Logger log = LoggerFactory.getLogger(SimpleMoteFinder.class);
	private MyMoteFinder finder;
	private final Object lock = new Object();
	private Mote mote;

    @Override
	public void moteFound(Mote mote) {
		log.info("SimpleMoteFinder received notification of a found mote.");
		this.mote = mote;
		this.mote.rumble(2000l);
                boolean[] player = new boolean[4];
                player[0] = true;
                this.mote.setPlayerLeds(player);
		String blueId = this.mote.getBluetoothAddress();
		System.out.println("Bluethooth id = "+blueId);
		synchronized(lock) {
			lock.notifyAll();
		}
	}

	public Mote findMote(String wiiMoteId) {
		if (finder == null) {
			finder = MyMoteFinder.getMoteFinder(wiiMoteId);
			finder.addMoteFinderListener(this);
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
