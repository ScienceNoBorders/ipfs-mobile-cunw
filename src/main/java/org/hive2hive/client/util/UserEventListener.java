package org.hive2hive.client.util;

import net.engio.mbassy.listener.Listener;
import net.engio.mbassy.listener.References;
import org.hive2hive.core.events.framework.interfaces.IUserEventListener;
import org.hive2hive.core.events.framework.interfaces.user.IUserLoginEvent;
import org.hive2hive.core.events.framework.interfaces.user.IUserLogoutEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author XiaoTiJun
 * @date 1/22/22 2:23 PM
 */
@Listener(references = References.Strong)
public class UserEventListener implements IUserEventListener {

    public UserEventListener(){}

    private Logger logger = LoggerFactory.getLogger(UserEventListener.class);

    @Override
    public void onClientLogin(IUserLoginEvent iUserLoginEvent) {
        logger.info("user ${} success logging and IP is ${}!", iUserLoginEvent.getCurrentUser(), iUserLoginEvent.getClientAddress());
    }

    @Override
    public void onClientLogout(IUserLogoutEvent iUserLogoutEvent) {
        logger.info("user ${} is logger out and IP is ${}!", iUserLogoutEvent.getCurrentUser(), iUserLogoutEvent.getClientAddress());
    }
}
