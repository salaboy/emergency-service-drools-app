package com.wordpress.salaboy;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.drools.SystemEventListenerFactory;
import org.drools.task.User;
import org.drools.task.service.TaskService;
import org.drools.task.service.TaskServiceSession;


import bitronix.tm.resource.jdbc.PoolingDataSource;
import org.drools.task.service.mina.MinaTaskServer;


public class TaskServerDaemon {

	private boolean running;
	private MinaTaskServer server;
	private EntityManagerFactory emf;
	private TaskService taskService;
	private TaskServiceSession taskSession;
	private PoolingDataSource ds1;

	public TaskServerDaemon() {
		this.running = false;
	}

	public void startServer() { 
		if(isRunning())
			throw new IllegalStateException("Server is already started");
		this.running = true;
		ds1 = new PoolingDataSource();
		ds1.setUniqueName("jdbc/testDS1");

		ds1.setClassName("com.mysql.jdbc.jdbc2.optional.MysqlXADataSource");
		ds1.setMaxPoolSize(5);
		ds1.setAllowLocalTransactions(true);
		ds1.getDriverProperties().put("user", "root");
		ds1.getDriverProperties().put("password", "atcroot");
		ds1.getDriverProperties().put("databaseName", "droolsflow");
		ds1.getDriverProperties().put("serverName", "localhost");

		ds1.init();
		// Use persistence.xml configuration
		emf = Persistence.createEntityManagerFactory("org.drools.task");

		taskService = new TaskService(emf, SystemEventListenerFactory.getSystemEventListener());
		taskSession = taskService.createSession();
		MockUserInfo userInfo = new MockUserInfo();
		taskService.setUserinfo(userInfo);

		User operator = new User("operator");
		User driver = new User("driver");
		User hospital = new User("hospital");
                User doctor = new User("doctor");
		User Administrator = new User("Administrator");
		taskSession.addUser(Administrator);
		taskSession.addUser(operator);
		taskSession.addUser(driver);
		taskSession.addUser(hospital);
                taskSession.addUser(doctor);

		server = new MinaTaskServer(taskService);
		Thread thread = new Thread(server);
		thread.start();
	}

	public void stopServer() {
		if(!isRunning())
			return;
		server.stop();
	}

	public boolean isRunning() {
		return running;
	}

}
