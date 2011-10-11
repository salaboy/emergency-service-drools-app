package com.wordpress.salaboy.emergencyservice.web.task.external;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.wordpress.salaboy.context.tracking.ContextTrackingProvider;
import com.wordpress.salaboy.model.serviceclient.PersistenceService;
import com.wordpress.salaboy.model.serviceclient.PersistenceServiceConfiguration;
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
		Map<String, Object> params = new HashMap<String, Object>();
        params.put("ContextTrackingImplementation", ContextTrackingProvider.ContextTrackingServiceType.IN_MEMORY);
        PersistenceServiceConfiguration conf = new PersistenceServiceConfiguration(params);
        try {
			distributedService = PersistenceServiceProvider.getPersistenceService(PersistenceServiceProvider.PersistenceServiceType.DISTRIBUTED_MAP, conf);
		} catch (IOException e) {
			throw new RuntimeException("There was an error getting persistence service", e);
		}
	}
	public PersistenceService getDistributedService() {
		return distributedService;
	}

}
