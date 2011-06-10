package com.wordpress.salaboy.emergencyservice.web.task.external;

import com.wordpress.salaboy.model.serviceclient.DistributedPeristenceServerService;

/**
 * Represents the distributed service. This adapts the
 * {@link DistributedPeristenceServerService}.
 * interface.
 * 
 * @author calcacuervo
 * 
 */
public class DistributedService {

	private DistributedPeristenceServerService distributedService = DistributedPeristenceServerService
			.getInstance();

	public DistributedPeristenceServerService getDistributedService() {
		return distributedService;
	}

	public void setDistributedService(
			DistributedPeristenceServerService distributedService) {
		this.distributedService = distributedService;
	}

}
