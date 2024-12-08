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
package org.eclipse.rdf4j.http.server.repository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rdf4j.http.server.repository.handler.DefaultQueryRequestHandler;
import org.eclipse.rdf4j.http.server.repository.handler.DefaultRepositoryRequestHandler;
import org.eclipse.rdf4j.http.server.repository.handler.QueryRequestHandler;
import org.eclipse.rdf4j.http.server.repository.handler.RepositoryRequestHandler;
import org.eclipse.rdf4j.http.server.repository.resolver.DefaultRepositoryResolver;
import org.eclipse.rdf4j.http.server.repository.resolver.RepositoryResolver;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * Handles queries and admin (delete) operations on a repository and renders the results in a format suitable to the
 * type of operation.
 *
 * @author Herko ter Horst
 */
@RestController
public class RepositoryController extends AbstractRepositoryController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private QueryRequestHandler queryRequestHandler;
	private RepositoryRequestHandler repositoryRequestHandler;

	@Autowired
	public RepositoryController(final RepositoryManager repMan) throws ApplicationContextException {
		setRepositoryManager(repMan);
	}

	@RequestMapping("/repositories/{repository}")
	public ModelAndView delegateRequest(
			@NonNull final HttpServletRequest request,
			@NonNull final HttpServletResponse response,
			@NonNull @PathVariable("repository") final String ignore
	) throws Exception {
		return handleRequest(request, response);
	}

	public void setRepositoryManager(RepositoryManager repMan) {
		if (logger.isDebugEnabled()) {
			logger.debug("setRepositoryManager {}", repMan);
		}

		RepositoryResolver repositoryResolver = new DefaultRepositoryResolver(repMan);
		queryRequestHandler = new DefaultQueryRequestHandler(repositoryResolver);
		repositoryRequestHandler = new DefaultRepositoryRequestHandler(repositoryResolver);
	}

	@Override
	protected QueryRequestHandler getQueryRequestHandler() {
		return queryRequestHandler;
	}

	@Override
	protected RepositoryRequestHandler getRepositoryRequestHandler() {
		return repositoryRequestHandler;
	}

}
