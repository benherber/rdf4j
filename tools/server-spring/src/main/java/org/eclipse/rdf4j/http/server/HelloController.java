package org.eclipse.rdf4j.http.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rdf4j.common.webapp.views.EmptySuccessView;
import org.springframework.context.ApplicationContextException;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

@RestController
public class HelloController extends AbstractController {

	public HelloController() throws ApplicationContextException {
		setSupportedMethods(METHOD_GET, METHOD_HEAD);
	}

	@Override
	@RequestMapping("/")
	public ModelAndView handleRequest(
			@NonNull final HttpServletRequest request,
			@NonNull final HttpServletResponse response
	) throws Exception {
		return super.handleRequest(request, response);
	}

	@Override
	protected ModelAndView handleRequestInternal(
			@NonNull final HttpServletRequest request,
			@NonNull final HttpServletResponse response
	) throws Exception {
		response.setStatus(200);
		try (final var out = response.getOutputStream()) {
			out.println("Hello, World!");
		}

		return new ModelAndView(EmptySuccessView.getInstance());
	}
}