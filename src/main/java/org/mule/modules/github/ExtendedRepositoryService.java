/**
 * Copyright (c) MuleSoft, Inc. All rights reserved. http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.md file.
 */

package org.mule.modules.github;

import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_REPOS;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.egit.github.core.Blob;
import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.service.RepositoryService;

/**
 * Temporary extension of {@link RepositoryService}, in order to support file
 * content retrieval (http://developer.github.com/v3/repos/contents/) A Pull
 * Request was made to get official support for this
 * (https://github.com/eclipse/egit-github/pull/8), but until it gets merged, we
 * can use this class
 * 
 * @author marianosimone
 * 
 */
public class ExtendedRepositoryService extends RepositoryService {

	/** */
    private static String SEGMENT_CONTENTS = "/contents"; //$NON-NLS-1$

	public ExtendedRepositoryService() {
        super();
    }

    public ExtendedRepositoryService(final GitHubClient client) {
        super(client);
    }

    /**
     * Get the contents of a given file
     * 
     * @param repository
     * @param path
     * @return
     * @throws IOException
     *             because of connectivity issues or if the file doesn't exist
     */
    public Blob getContents(IRepositoryIdProvider repository, String path, String branch) throws IOException {
        String id = getId(repository);
        StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
        uri.append('/').append(id);
        uri.append(SEGMENT_CONTENTS);
        uri.append("/").append(path);
        GitHubRequest request = createRequest();
        request.setUri(uri);
        request.setType(Blob.class);

        if (branch != null) {
            Map<String, String> param = new HashMap<String, String>();
            param.put("ref", branch);
            request.setParams(param);
        }

        return (Blob) client.get(request).getBody();
    }
}
