package com.wordpress.salaboy.emergencyservice.web.task.external;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Unit tests for {@link ModelDataConnector.
 * @author calcacuervo
 *
 */
public class ModelDataConnectorTest {

	@Test
	public void getAllEmergencyTypes() {
		ModelDataConnector connector = new ModelDataConnector();
		List<String> types = connector.emergencyTypes();
		Assert.assertEquals(4, types.size());
		Assert.assertTrue(types.contains("ROBBERY"));
		Assert.assertTrue(types.contains("FIRE"));
		Assert.assertTrue(types.contains("CAR_CRASH"));
		Assert.assertTrue(types.contains("HEART_ATTACK"));
	}
}
