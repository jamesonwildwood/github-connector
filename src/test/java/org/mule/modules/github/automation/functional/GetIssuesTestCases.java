/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */
package org.mule.modules.github.automation.functional;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;

import org.eclipse.egit.github.core.Issue;
import org.junit.Test;
import org.mule.modules.github.GitHubConnector;
import org.mule.tools.devkit.ctf.junit.AbstractTestCase;

public class GetIssuesTestCases extends AbstractTestCase<GitHubConnector> {

    public GetIssuesTestCases() {
        super(GitHubConnector.class);
    }

    @Test
    public void testGetIssues() throws IOException {
        List<Issue> issues = getConnector().getIssues("myUser", "myRepo", null);
        assertThat(issues, notNullValue());
        assertThat(issues.size(), greaterThanOrEqualTo(0));
    }

}
