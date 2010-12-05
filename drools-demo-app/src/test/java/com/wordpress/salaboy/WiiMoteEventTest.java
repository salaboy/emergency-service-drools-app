package com.wordpress.salaboy;

import com.wordpress.salaboy.wiimote.EmbebbedHornetQServer;
import com.wordpress.salaboy.wiimote.SimpleMoteFinder;
import com.wordpress.salaboy.wiimote.WiiMoteEvent;
import junit.framework.Assert;
import motej.Mote;
import motej.event.AccelerometerEvent;
import motej.event.AccelerometerListener;
import motej.request.ReportModeRequest;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.impl.ClassPathResource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.hornetq.api.core.HornetQException;
import org.junit.Test;

import com.intel.bluetooth.BlueCoveConfigProperties;

/**
 *
 * @author salaboy
 */
public class WiiMoteEventTest {

	private KnowledgeBase kbase;
	private WorkingMemoryEntryPoint patientHeartbeatsEntryPoint;
	private StatefulKnowledgeSession ksession;

	@Test
	public void helloWiiMote() throws Exception {
		
		final EmbebbedHornetQServer server = new EmbebbedHornetQServer();
		server.start();

		ksession = createSession();

		System.setProperty(BlueCoveConfigProperties.PROPERTY_JSR_82_PSM_MINIMUM_OFF, "true");
		SimpleMoteFinder simpleMoteFinder = new SimpleMoteFinder();
		Mote mote = simpleMoteFinder.findMote();

		System.out.println("founded wiimote" + mote);

		Assert.assertNotNull(mote);

		AccelerometerListener<Mote> listener = new AccelerometerListener<Mote>() {
			public void accelerometerChanged(AccelerometerEvent<Mote> evt) {
				int y = evt.getY();
				if ( y > 225) {
					System.out.println("sended " + y + " heartbeat");
					patientHeartbeatsEntryPoint.insert(new WiiMoteEvent(evt, "acc"));
					ksession.fireAllRules();
				}
			}
		};
		mote.setReportMode(ReportModeRequest.DATA_REPORT_0x31);
		mote.addAccelerometerListener(listener);

		new Thread(new Runnable() {

			public void run() {
				try {
					while(true){
						Thread.sleep(2500);
						//System.out.println("sended 240 regular heartbeat");
						AccelerometerEvent<Mote> accelerometerEvent = new AccelerometerEvent<Mote>(null, 240, 240, 240);
						patientHeartbeatsEntryPoint.insert(new WiiMoteEvent(accelerometerEvent, "acc"));
						ksession.fireAllRules();
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}).start();
		
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						String message = server.receive();
						System.out.println("HornetQ server message received: " + message);
					} catch (HornetQException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();

		try {
			Thread.sleep(100000l);
		} catch (InterruptedException ex) {
			System.out.println("ERROR!!!");
		} finally {
			mote.setReportMode(ReportModeRequest.DATA_REPORT_0x30);
			mote.disconnect();
		}
	}

	private StatefulKnowledgeSession createSession() {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(new ClassPathResource("rules/patient.drl"), ResourceType.DRL);

		if (kbuilder.hasErrors()) {
			for (KnowledgeBuilderError error : kbuilder.getErrors()) {
				System.out.println(error);
			}
			throw new IllegalStateException("Error building kbase!");
		}

		kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
		patientHeartbeatsEntryPoint = ksession.getWorkingMemoryEntryPoint("patientHeartbeats");
		return ksession;
	}

}