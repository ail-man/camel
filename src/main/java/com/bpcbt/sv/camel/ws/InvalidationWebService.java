package com.bpcbt.sv.camel.ws;

import com.bpcbt.sv.camel.Router;
import com.bpcbt.sv.sv_sync.SvsyncAsync;
import com.bpcbt.sv.sv_sync.SyncRequestHeadType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class InvalidationWebService implements SvsyncAsync {
	private static final Logger logger = LogManager.getLogger(ConfigWebService.class);
	@Autowired
	private Router router;

	@Override
	public void cancel(SyncRequestHeadType parameters) {
		String sessionId = parameters.getSessionId();
		logger.info("Received invalidation request for sessionId" + sessionId);
		router.invalidateSession(sessionId);
	}
}
