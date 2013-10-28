package org.hive2hive.core.process.register;

import java.io.IOException;

import net.tomp2p.futures.FutureDHT;

import org.hive2hive.core.H2HConstants;
import org.hive2hive.core.log.H2HLogger;
import org.hive2hive.core.log.H2HLoggerFactory;
import org.hive2hive.core.model.Locations;
import org.hive2hive.core.model.UserProfile;
import org.hive2hive.core.network.messages.direct.response.ResponseMessage;
import org.hive2hive.core.process.ProcessStep;
import org.hive2hive.core.process.common.PutLocationStep;
import org.hive2hive.core.process.common.PutUserProfileStep;

public class CheckIfProfileExistsStep extends ProcessStep {

	private static final H2HLogger logger = H2HLoggerFactory.getLogger(CheckIfProfileExistsStep.class);
	private final String userId;

	public CheckIfProfileExistsStep(String userId) {
		this.userId = userId;
	}

	@Override
	public void start() {
		logger.debug(String.format("Checking if a user profile already exists. user id = '%s'", userId));
		get(userId, H2HConstants.USER_PROFILE);
	}

	@Override
	public void rollBack() {
		// only a get call which has no effect
	}

	@Override
	protected void handleMessageReply(ResponseMessage asyncReturnMessage) {
		// not used
	}

	@Override
	protected void handlePutResult(FutureDHT future) {
		// not used
	}

	@Override
	protected void handleGetResult(FutureDHT future) {
		if (future.getData() == null) {
			logger.debug(String.format("No user profile exists. user id = '%s'", userId));
			continueWithNextStep();
		} else {
			try {
				if (!(future.getData().getObject() instanceof UserProfile)) {
					logger.warn(String.format("Instance of UserProfile expected. key = '%s'", userId));
				}
			} catch (ClassNotFoundException | IOException e) {
				logger.warn(String.format("future.getData().getObject() failed. reason = '%s'",
						e.getMessage()));
			}
			getProcess().rollBack("User profile already exists.");
		}
	}

	private void continueWithNextStep() {
		RegisterProcess process = (RegisterProcess) super.getProcess();

		// create the next steps:
		// first, put the new user profile
		// second, put the empty locations map
		// third, put the public key of the user
		// next step: Put the public key of the user into the DHT
		PutPublicKeyStep third = new PutPublicKeyStep(process.getUserProfile());
		PutLocationStep second = new PutLocationStep(new Locations(process.getUserProfile().getUserId()),
				null, third);
		PutUserProfileStep first = new PutUserProfileStep(process.getUserProfile(), null,
				process.getUserPassword(), second);
		getProcess().nextStep(first);
	}

	@Override
	protected void handleRemovalResult(FutureDHT future) {
		// not used
	}
}
