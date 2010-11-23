package com.wordpress.salaboy.msgs;

import java.util.HashMap;
import java.util.Map;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.api.core.client.HornetQClient;
import org.hornetq.integration.transports.netty.TransportConstants;

public class HornetQClientService {
	
	private static HornetQClientService instance;

	private ClientSession session;
	private ClientProducer producer;

	public HornetQClientService() throws HornetQException {
		Map<String, Object> connectionParams = new HashMap<String, Object>();
		connectionParams.put(TransportConstants.PORT_PROP_NAME, 5446);
		TransportConfiguration transportConfiguration = new TransportConfiguration("org.hornetq.integration.transports.netty.NettyConnectorFactory", connectionParams);
		ClientSessionFactory factory = HornetQClient.createClientSessionFactory(transportConfiguration);
		session = factory.createSession();
		producer = session.createProducer("alerts");
		session.start();
	}

	public void sendMessage(String message) throws Exception {
		ClientMessage clientMessage = session.createMessage(true);
		clientMessage.getBodyBuffer().writeString(message);
		producer.send(clientMessage);
		System.out.println("Sending message: " + message);
	}

	public static void main(String[] args) throws Exception {
		HornetQClientService client = new HornetQClientService();
		client.sendMessage("testing rule message");
	}

	public static void setInstance(HornetQClientService instance) {
		HornetQClientService.instance = instance;
	}

	public static HornetQClientService getInstance() throws HornetQException {
		if (instance==null) {
			instance = new HornetQClientService();
		}
		return instance;
	}

}
