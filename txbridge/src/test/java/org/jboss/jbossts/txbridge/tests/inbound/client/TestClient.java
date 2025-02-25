/*
   Copyright The Narayana Authors
   SPDX short identifier: Apache-2.0
 */

package org.jboss.jbossts.txbridge.tests.inbound.client;

import com.arjuna.mw.wst11.UserTransaction;
import com.arjuna.mw.wst11.UserTransactionFactory;
import com.arjuna.mw.wst11.client.JaxWSHeaderContextProcessor;
import com.arjuna.wst.TransactionRolledBackException;
import org.jboss.logging.Logger;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.handler.Handler;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.UnknownHostException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet which includes test methods for exercising the txbridge.
 *
 * @author Jonathan Halliday (jonathan.halliday@redhat.com) 2010-01
 */
@WebServlet(name = "Inbound Test Client Servlet", urlPatterns = TestClient.URL_PATTERN)
public class TestClient extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static Logger log = Logger.getLogger(TestClient.class);

    public static final String URL_PATTERN = "/testclient";

    private ServletContext context;
    private TestService testService;

    /**
     * Initialise the servlet.
     *
     * @param config The servlet configuration.
     */
    public void init(final ServletConfig config)
            throws ServletException {
        try {
            URL wsdlLocation = new URL("http://" + getLocalHost() + ":8080/txbridge-inbound-tests-service/TestServiceImpl?wsdl");
            QName serviceName = new QName("http://client.inbound.tests.txbridge.jbossts.jboss.org/", "TestServiceImplService");

            Service service = Service.create(wsdlLocation, serviceName);
            testService = service.getPort(TestService.class);

            BindingProvider bindingProvider = (BindingProvider) testService;
            List<Handler> handlers = new ArrayList<Handler>(1);
            handlers.add(new JaxWSHeaderContextProcessor());
            bindingProvider.getBinding().setHandlerChain(handlers);

            context = config.getServletContext();
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            UserTransaction ut = UserTransactionFactory.userTransaction();

            log.info("starting the transaction...");

            ut.begin();

            log.info("transaction ID= " + ut.toString());

            log.info("calling business Web Services...");

            testService.doNothing();

            log.info("terminating the transaction...");

            terminateTransaction(ut, false);
        } catch (final TransactionRolledBackException tre) {
            log.info("Transaction rolled back");
        } catch (Exception e) {
            log.info("problem: ", e);
        }

        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        out.println("finished");
        out.close();
    }

    private void terminateTransaction(UserTransaction userTransaction, boolean shouldCommit) throws Exception {
        log.info("shouldCommit=" + shouldCommit);

        if (shouldCommit) {
            userTransaction.commit();
        } else {
            userTransaction.rollback();
        }
    }

    static String getLocalHost() {
        return isIPv6() ? "[::1]" : "localhost";
    }

    static boolean isIPv6() {
        try {
            if (InetAddress.getLocalHost() instanceof Inet6Address || System.getenv("IPV6_OPTS") != null)
                return true;
        } catch (final UnknownHostException uhe) {
        }

        return false;
    }
}