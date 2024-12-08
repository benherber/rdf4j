/*******************************************************************************
 * Copyright (c) 2015 Eclipse RDF4J contributors, Aduna, and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 *******************************************************************************/
package org.eclipse.rdf4j.http.server.repository.size;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rdf4j.common.webapp.views.SimpleResponseView;
import org.eclipse.rdf4j.http.protocol.Protocol;
import org.eclipse.rdf4j.http.server.ProtocolUtil;
import org.eclipse.rdf4j.http.server.ServerHTTPException;
import org.eclipse.rdf4j.http.server.repository.RepositoryInterceptor;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.springframework.context.ApplicationContextException;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * Handles requests for the size of (set of contexts in) a repository.
 *
 * @author Herko ter Horst
 */
@RestController
public class SizeController extends AbstractController {

	public SizeController() throws ApplicationContextException {
		setSupportedMethods(METHOD_GET, METHOD_HEAD);
	}

	@RequestMapping("/repositories/{repository}/size")
	public ModelAndView delegateRequest(
			@NonNull final HttpServletRequest request,
			@NonNull final HttpServletResponse response,
			@NonNull @PathVariable("repository") final String ignore
	) throws Exception {
		return handleRequest(request, response);
	}

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ProtocolUtil.logRequestParameters(request);

		Map<String, Object> model = new HashMap<>();
		final boolean headersOnly = METHOD_HEAD.equals(request.getMethod());

		if (!headersOnly) {
			Repository repository = RepositoryInterceptor.getRepository(request);

			ValueFactory vf = repository.getValueFactory();
			Resource[] contexts = ProtocolUtil.parseContextParam(request, Protocol.CONTEXT_PARAM_NAME, vf);

			try (RepositoryConnection repositoryCon = RepositoryInterceptor.getRepositoryConnection(request)) {
				long size = repositoryCon.size(contexts);
				model.put(SimpleResponseView.CONTENT_KEY, String.valueOf(size));
			} catch (RepositoryException e) {
				throw new ServerHTTPException("Repository error: " + e.getMessage(), e);
			}

		}

		return new ModelAndView(SimpleResponseView.getInstance(), model);
	}
}
