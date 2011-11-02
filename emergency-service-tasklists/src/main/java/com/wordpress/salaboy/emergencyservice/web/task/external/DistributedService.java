package com.wordpress.salaboy.emergencyservice.web.task.external;


import com.wordpress.salaboy.model.serviceclient.PersistenceService;
import com.wordpress.salaboy.model.serviceclient.PersistenceServiceProvider;

/**
 * Represents the distributed service. This adapts the
 * {@link DistributedPeristenceServerService}.
 * interface.
 * 
 * @author calcacuervo
 * 
 */
public class DistributedService {

	private PersistenceService distributedService;

	public DistributedService() {
            distributedService = PersistenceServiceProvider.getPersistenceService();
	}
        
	public PersistenceService getDistributedService() {
            return distributedService;
	}

}
