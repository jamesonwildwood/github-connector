/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.modules.github;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.eclipse.egit.github.core.Authorization;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CollaboratorService;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.DataService;
import org.eclipse.egit.github.core.service.DeployKeyService;
import org.eclipse.egit.github.core.service.DownloadService;
import org.eclipse.egit.github.core.service.GistService;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.LabelService;
import org.eclipse.egit.github.core.service.MilestoneService;
import org.eclipse.egit.github.core.service.OAuthService;
import org.eclipse.egit.github.core.service.TeamService;
import org.eclipse.egit.github.core.service.UserService;
import org.eclipse.egit.github.core.service.WatcherService;

public class ServiceFactory {

	private static final String BASE_URL = "api.github.com";
	private IssueService defaultIssueService;
	private WatcherService defaultWatcherService;
	private CollaboratorService defaultCollaboratorService;
	private CommitService defaultCommitService;
	private DeployKeyService defaultDeployKeyService;
	private DownloadService defaultDownloadService;
	private GistService defaultGistService;
	private LabelService defaultLabelService;
	private MilestoneService defaultMilestoneService;
	private UserService defaultUserService;
	private TeamService defaultTeamService;
	private ExtendedRepositoryService defaultRepositoryService;
	private OAuthService defaultOAuthService;
	private DataService defaultDataService;
	private final String password;
	private final String user;
	private String token;
	
	public ServiceFactory(String user, String password) {
		this.user = user;
		this.password = password;
	}
	

	public ServiceFactory(String user, String password, String scope)
			throws IOException {
		this.user = user;
		this.password = password;
		this.token = getAccessToken(scope);
	}

	public String getAccessToken(String scope) throws IOException {

		List<String> scopes = null;

		List<Authorization> authorizations = getOAuthService()
				.getAuthorizations();

		if (!authorizations.isEmpty()) {
			if (scope != null && !scope.isEmpty()) {
				scopes = Arrays.asList(scope.split(","));
				if (scopes != null && !scopes.isEmpty()) {
					for (Authorization auth : authorizations) {
						// Check the authorization contains all the required
						// scopes
						if (auth.getScopes().containsAll(scopes)) {
							return auth.getToken();
						}
					}
				}
			} else {
				// No specific scope requested, so return default authorization
				return authorizations.get(0).getToken();
			}
		}
		// Otherwise create new authorization
		return getOAuthService().createAuthorization(
				createNewAuthorization(scopes)).getToken();

	}

	public Authorization createNewAuthorization(List<String> scopes) {
		Authorization auth = new Authorization();
		auth.setScopes(scopes);
		auth.setNote("Mule GitHub connector");

		return auth;
	}

	public OAuthService getOAuthService() {
		if (defaultOAuthService == null) {
			GitHubClient client = new GitHubClient(BASE_URL);
			client.setCredentials(user, password);
			setDefaultOAuthService(new OAuthService(client));
		}
		return defaultOAuthService;
	}

	public DataService getDataService() {
		if (defaultDataService == null) {
			GitHubClient client = new GitHubClient(BASE_URL);
			client.setCredentials(user, password);
			setDefaultDataService(new DataService(client));
		}
		return defaultDataService;
	}

	public IssueService getIssueService() {
		if (defaultIssueService == null) {
			GitHubClient client = getGitHubClient();
			setDefaultIssueService(new IssueService(client));
		}
		return defaultIssueService;
	}

	public WatcherService getWatcherService() {
		if (defaultWatcherService == null) {
			GitHubClient client = getGitHubClient();
			setDefaultWatcherService(new WatcherService(client));
		}
		return defaultWatcherService;
	}

	public CommitService getCommitService() {
		if (defaultCommitService == null) {
			GitHubClient client = getGitHubClient();
			setDefaultCommitService(new CommitService(client));
		}
		return defaultCommitService;
	}

	public CollaboratorService getCollaboratorService() {
		if (defaultCollaboratorService == null) {
			GitHubClient client = getGitHubClient();
			setDefaultCollaboratorService(new CollaboratorService(client));
		}
		return defaultCollaboratorService;
	}

	public DeployKeyService getDeployKeyService() {
		if (defaultDeployKeyService == null) {
			GitHubClient client = getGitHubClient();
			setDefaultDeployKeyService(new DeployKeyService(client));
		}
		return defaultDeployKeyService;
	}

	public DownloadService getDownloadService() {
		if (defaultDownloadService == null) {
			GitHubClient client = getGitHubClient();
			setDefaultDownloadService(new DownloadService(client));
		}
		return defaultDownloadService;
	}

	public GistService getGistService() {
		if (defaultGistService == null) {
			GitHubClient client = getGitHubClient();
			setGistService(new GistService(client));
		}
		return defaultGistService;
	}

	public LabelService getLabelService() {
		if (defaultLabelService == null) {
			GitHubClient client = getGitHubClient();
			setLabelService(new LabelService(client));
		}
		return defaultLabelService;
	}

	public MilestoneService getMilestoneService() {
		if (defaultMilestoneService == null) {
			GitHubClient client = getGitHubClient();
			setMilestoneService(new MilestoneService(client));
		}
		return defaultMilestoneService;
	}

	public UserService getUserService() {
		if (defaultUserService == null) {
			GitHubClient client = getGitHubClient();
			setDefaultUserService(new UserService(client));
		}
		return defaultUserService;
	}

	public TeamService getTeamService() {
		if (defaultTeamService == null) {
			GitHubClient client = getGitHubClient();
			setDefaultTeamService(new TeamService(client));
		}
		return defaultTeamService;
	}

	public ExtendedRepositoryService getRepositoryService() {
		if (defaultRepositoryService == null) {
			GitHubClient client = getGitHubClient();
			setDefaultRepositoryService(new ExtendedRepositoryService(client));
		}
		return defaultRepositoryService;
	}

	private GitHubClient getGitHubClient() {
		GitHubClient client = new GitHubClient(BASE_URL);
		client.setOAuth2Token(token);
		return client;
	}

	public String getUser(){
		return this.user;
	}
	
	public void setDefaultIssueService(IssueService defaultIssueService) {
		this.defaultIssueService = defaultIssueService;
	}

	public void setDefaultDataService(DataService defaultDataService) {
		this.defaultDataService = defaultDataService;
	}

	public void setDefaultWatcherService(
			WatcherService defaultWatcherService) {
		this.defaultWatcherService = defaultWatcherService;
	}

	public void setDefaultCollaboratorService(
			CollaboratorService defaultCollaboratorService) {
		this.defaultCollaboratorService = defaultCollaboratorService;
	}

	public void setDefaultCommitService(
			CommitService defaultCommitService) {
		this.defaultCommitService = defaultCommitService;
	}

	public void setDeployKeyService(
			DeployKeyService defaultDeployKeyService) {
		this.defaultDeployKeyService = defaultDeployKeyService;
	}

	public void setDownloadService(DownloadService defaultDownloadService) {
		this.defaultDownloadService = defaultDownloadService;
	}

	public void setGistService(GistService defaultGistService) {
		this.defaultGistService = defaultGistService;
	}

	public void setLabelService(LabelService defaultLabelService) {
		this.defaultLabelService = defaultLabelService;
	}

	public void setMilestoneService(
			MilestoneService defaultMilestoneService) {
		this.defaultMilestoneService = defaultMilestoneService;
	}

	public void setDefaultUserService(UserService defaultUserService) {
		this.defaultUserService = defaultUserService;
	}

	public void setDefaultTeamService(TeamService defaultTeamService) {
		this.defaultTeamService = defaultTeamService;
	}

	public void setDefaultRepositoryService(
			ExtendedRepositoryService defaultRepositoryService) {
		this.defaultRepositoryService = defaultRepositoryService;
	}

	public void setDefaultOAuthService(OAuthService defaultOAuthService) {
		this.defaultOAuthService = defaultOAuthService;
	}

	public void setDefaultDeployKeyService(
			DeployKeyService defaultDeployKeyService) {
		this.defaultDeployKeyService = defaultDeployKeyService;
	}

	public void setDefaultDownloadService(
			DownloadService defaultDownloadService) {
		this.defaultDownloadService = defaultDownloadService;
	}
}