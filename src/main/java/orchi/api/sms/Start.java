package orchi.api.sms;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *david
 */
public class Start {
	private static Logger log = LoggerFactory.getLogger(Start.class);
	private static Server server;

	public static void main(String[] args) throws Exception {
		server = new Server();

		// HTTP Configuration
		HttpConfiguration http_config = new HttpConfiguration();

		http_config.setSecureScheme("https");
		http_config.setSecurePort(2525);
		http_config.setOutputBufferSize(32768);
		http_config.setRequestHeaderSize(8192);
		http_config.setResponseHeaderSize(8192);
		http_config.setSendServerVersion(false);
		http_config.setSendDateHeader(false);

		ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(http_config));
		http.setPort(8080);
		http.setHost("orchi");
		// http.setIdleTimeout(30000);

		server.addConnector(http);

		ServletContextHandler servletContext = new ServletContextHandler(ServletContextHandler.SESSIONS);

		servletContext.setContextPath("/api");

		servletContext.addServlet(API.class, "/").setAsyncSupported(true);
		servletContext.addServlet(TEST.class, "/test").setAsyncSupported(true);

		ContextHandlerCollection contexts = new ContextHandlerCollection();

		contexts.setHandlers(new Handler[] { servletContext });

		server.setHandler(contexts);

		log.info("Iniciando servidor de API SMS");
		server.start();
		server.join();
	}
}
