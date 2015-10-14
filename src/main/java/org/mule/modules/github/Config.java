/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */
package org.mule.modules.github;

import java.io.IOException;

import org.mule.api.ConnectionException;
import org.mule.api.ConnectionExceptionCode;
import org.mule.api.annotations.Connect;
import org.mule.api.annotations.ConnectionIdentifier;
import org.mule.api.annotations.Disconnect;
import org.mule.api.annotations.TestConnectivity;
import org.mule.api.annotations.ValidateConnection;
import org.mule.api.annotations.components.ConnectionManagement;
import org.mule.api.annotations.display.Password;
import org.mule.api.annotations.param.ConnectionKey;
import org.mule.api.annotations.param.Default;
import org.mule.modules.github.service.ServiceFactory;
import org.mule.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ConnectionManagement(friendlyName = "Configuration")
public class Config {

    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    private ServiceFactory serviceFactory;

    public ServiceFactory getServiceFactory() throws IOException {
        return serviceFactory;
    }

    public void setServiceFactory(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    /**
     * Constructs a new {@link Config} Instance with default configuration values set to false.
     */
    public Config() {

    }

    @TestConnectivity
    public void testConnectivity(@ConnectionKey String connectionUser, @Password String connectionPassword, @Default("repo") String scope) throws ConnectionException {

        logger.debug("Testing connectivity...");

        try {
            this.connect(connectionUser, connectionPassword, scope);
        } finally {
            disconnect();
            logger.debug("Session disconnected");
        }

    }

    /**
     * Creates a connection to GitHub by making a login call with the given credentials to the specified address. The login call, if successful, returns a token which will be used
     * in the subsequent calls to JIRA.
     *
     * @param user
     *            the login user credential
     * @param password
     *            the login password credential
     * @param scope
     *            the repository to connect
     * @see <a href="https://developer.github.com/v3/oauth/#scopes">list of supported scopes</a>
     */
    @Connect
    public void connect(@ConnectionKey String connectionUser, @Password String connectionPassword, @Default("repo") String scope) throws ConnectionException {

        // Validate username
        if (StringUtils.isBlank(connectionUser)) {
            logger.error("Invalid username");
            throw new ConnectionException(ConnectionExceptionCode.INCORRECT_CREDENTIALS, "", "Attribute 'user' is required. Provide a valid user to access your GitHub account.");
        }

        // Validate password
        if (StringUtils.isBlank(connectionPassword)) {
            logger.error("Invalid password");
            throw new ConnectionException(ConnectionExceptionCode.INCORRECT_CREDENTIALS, "",
                    "Attribute 'password' is required. Provide a valid password to access your GitHub account.");
        }

        // Validate scope
        if (StringUtils.isBlank(scope)) {
            logger.warn("Empty scope");
        }

        try {
            setServiceFactory(new ServiceFactory(connectionUser, connectionPassword, scope));
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new ConnectionException(ConnectionExceptionCode.CANNOT_REACH, "", "Could not establish connection with your GitHub account. " + e.getMessage(), e);
        }

        logger.debug("GitHub session established");
    }

    @Disconnect
    public void disconnect() {
        setServiceFactory(null);
    }

    /**
     * Return serviceFactory was set or not
     */
    @ValidateConnection
    public boolean validateConnection() {
        return this.serviceFactory != null;
    }

    /**
     * Returns the connection identifier
     */
    @Override
    @ConnectionIdentifier
    public String toString() {
        return serviceFactory.getUser();
    }

}
