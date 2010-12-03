package com.wordpress.salaboy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.client.ClientConsumer;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.api.core.client.HornetQClient;
import org.hornetq.core.config.Configuration;
import org.hornetq.core.config.impl.ConfigurationImpl;
import org.hornetq.core.server.HornetQServer;
import org.hornetq.core.server.HornetQServers;
import org.hornetq.integration.transports.netty.NettyAcceptorFactory;
import org.hornetq.integration.transports.netty.TransportConstants;


public class EmbebbedHornetQServer {

	private HornetQServer server;
	private ClientSession session;
	private ClientConsumer consumer;

	public EmbebbedHornetQServer() {
		Configuration configuration = new ConfigurationImpl();
		configuration.setPersistenceEnabled(false);
		configuration.setSecurityEnabled(false);

		Map<String, Object> connectionParams = new HashMap<String, Object>();
		connectionParams.put(TransportConstants.PORT_PROP_NAME, 5446);

		TransportConfiguration transpConf = new TransportConfiguration(NettyAcceptorFactory.class.getName(), connectionParams);

		HashSet<TransportConfiguration> setTransp = new HashSet<TransportConfiguration>();
		setTransp.add(transpConf);

		configuration.setAcceptorConfigurations(setTransp);

		server = HornetQServers.newHornetQServer(configuration);

	}

	public void start() throws Exception {
		server.start();
		System.out.println("STARTED::");
		Map<String, Object> connectionParams = new HashMap<String, Object>();
		connectionParams.put(TransportConstants.PORT_PROP_NAME, 5446);
		TransportConfiguration transportConfiguration = new TransportConfiguration("org.hornetq.integration.transports.netty.NettyConnectorFactory", connectionParams);
		ClientSessionFactory factory = HornetQClient.createClientSessionFactory(transportConfiguration);
		session = factory.createSession();
		session.createQueue("alerts", "alerts", true);
		consumer = session.createConsumer("alerts");
		session.start();
		System.out.println("QUEUE 'alerts' CREATED::");
	}

	public void stop() throws Exception {
		session.close();
		server.stop();
	}

	public String receive() throws HornetQException {
		ClientMessage msgReceived = consumer.receive();
		return msgReceived.getBodyBuffer().readString();
	}

	public static void main(String[] args) throws Exception {
		EmbebbedHornetQServer server = new EmbebbedHornetQServer();
		server.start();
		while (true) {
			System.out.println("waiting...");
			server.receive();
		}
	}

}
