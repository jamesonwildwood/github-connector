/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */
package org.mule.modules.github.automation.system;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Properties;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.mule.api.ConnectionException;
import org.mule.modules.github.Config;
import org.mule.tools.devkit.ctf.configuration.util.ConfigurationUtils;
import org.mule.tools.devkit.ctf.exceptions.ConfigurationLoadingFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigTestCases {

    private static final Logger logger = LoggerFactory.getLogger(ConfigTestCases.class);

    @Rule
    public TestName name = new TestName();

    private Config config;
    private Properties credentials;
    private String username;
    private String password;
    private String scope;

    @Before
    public void setUp() throws ConnectionException, ConfigurationLoadingFailedException {

        logger.debug("\n\n++++++++++++++++++++ TEST {} ++++++++++++++++++++\n", name.getMethodName());

        credentials = ConfigurationUtils.getAutomationCredentialsProperties();
        username = credentials.getProperty("config.connectionUser");
        password = credentials.getProperty("config.connectionPassword");
        scope = credentials.getProperty("config.scope");

    }

    @Test
    public void validCredentials() throws ConnectionException, IOException {
        config = new Config();
        config.connect(username, password, scope);
        assertThat(config.getServiceFactory(), notNullValue());
        assertThat(config.getServiceFactory().getOAuthService().getClient().getUser(), is(username));
    }

    @Test(expected = ConnectionException.class)
    public void invalidUsernameCredentials() throws ConnectionException, IOException {
        config = new Config();
        config.connect("INVALID_USER", password, scope);
        assertThat(config.getServiceFactory(), nullValue());
    }

    @Test(expected = ConnectionException.class)
    public void emptyUsernameCredentials() throws ConnectionException, IOException {
        config = new Config();
        config.testConnectivity("", password, scope);
        assertThat(config.getServiceFactory(), nullValue());
    }

    @Test(expected = ConnectionException.class)
    public void invalidPasswordCredentials() throws ConnectionException, IOException {
        config = new Config();
        config.testConnectivity(username, "INVALID_PASSWORD", scope);
        assertThat(config.getServiceFactory(), nullValue());
    }

    @Test(expected = ConnectionException.class)
    public void emptyPasswordCredentials() throws ConnectionException, IOException {
        config = new Config();
        config.testConnectivity(username, "", scope);
        assertThat(config.getServiceFactory(), nullValue());
    }

    @Test
    public void validNoScope() throws ConnectionException, IOException {
        config = new Config();
        config.testConnectivity(username, password, "");
        assertThat(config.getServiceFactory(), nullValue());
    }

    @Test
    public void nullScope() throws ConnectionException, IOException {
        config = new Config();
        config.testConnectivity(username, password, null);
        assertThat(config.getServiceFactory(), nullValue());
    }

    @Test(expected = ConnectionException.class)
    public void invalidScope() throws ConnectionException, IOException {
        config = new Config();
        config.testConnectivity(username, password, "INVALID_SCOPE");
        assertThat(config.getServiceFactory(), nullValue());
    }

}
