package controllers;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;*/

@WebServlet("/GoToHomeLecturer")
public class GoToHomeLecturer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	// private TemplateEngine templateEngine;

	public GoToHomeLecturer() {
		super();
	}

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		/*
		 * ServletContextTemplateResolver templateResolver = new
		 * ServletContextTemplateResolver(servletContext);
		 * templateResolver.setTemplateMode(TemplateMode.HTML); this.templateEngine =
		 * new TemplateEngine();
		 * this.templateEngine.setTemplateResolver(templateResolver);
		 * templateResolver.setSuffix(".html");
		 */
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {

	}
}
