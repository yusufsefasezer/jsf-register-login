package com.yusufsezer.filter;

import com.yusufsezer.entity.Person;
import com.yusufsezer.helper.Helper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

@WebFilter(
        urlPatterns = {
            "/index.xhtml"
        }
)
public class AuthFilter implements Filter {

    private static final boolean DEGUG = true;
    private FilterConfig filterConfig = null;

    public AuthFilter() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        if (DEGUG) {
            log("AuthFilter:doFilter()");
        }

        Throwable problem = null;
        try {

            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletRequest.setCharacterEncoding("UTF-8");

            Person loginPerson = Helper.getLoginPerson(httpServletRequest);
            String reqURL = httpServletRequest.getServletPath();
            if (loginPerson == null) {

                boolean isSpecialPage = !reqURL.contains(Helper.LOGIN_PAGE) && !reqURL.contains(Helper.REGISTER_PAGE);

                if (isSpecialPage) {
                    httpServletResponse.sendRedirect(Helper.LOGIN_PAGE);
                    return;
                }

            }

            chain.doFilter(request, response);
        } catch (IOException | ServletException t) {
            problem = t;
        }

        if (problem != null) {
            if (problem instanceof ServletException servletException) {
                throw servletException;
            }
            if (problem instanceof IOException iOException) {
                throw iOException;
            }
            sendProcessingError(problem, response);
        }
    }

    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }

    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
        if (filterConfig != null) {
            if (DEGUG) {
                log("AuthFilter:Initializing filter");
            }
        }
    }

    @Override
    public String toString() {
        if (filterConfig == null) {
            return ("AuthFilter()");
        }
        StringBuilder sb = new StringBuilder("AuthFilter(");
        sb.append(filterConfig);
        sb.append(")");
        return (sb.toString());
    }

    private void sendProcessingError(Throwable t, ServletResponse response) {
        String stackTrace = getStackTrace(t);

        if (stackTrace != null && !stackTrace.equals("")) {
            try {
                response.setContentType("text/html");
                try (PrintStream ps = new PrintStream(response.getOutputStream()); PrintWriter pw = new PrintWriter(ps)) {
                    pw.print("<html>\n<head>\n<title>Error</title>\n</head>\n<body>\n"); //NOI18N

                    // PENDING! Localize this for next official release
                    pw.print("<h1>The resource did not process correctly</h1>\n<pre>\n");
                    pw.print(stackTrace);
                    pw.print("</pre></body>\n</html>"); //NOI18N
                }
                response.getOutputStream().close();
            } catch (IOException ex) {
            }
        } else {
            try {
                try (PrintStream ps = new PrintStream(response.getOutputStream())) {
                    t.printStackTrace(ps);
                }
                response.getOutputStream().close();
            } catch (IOException ex) {
            }
        }
    }

    public static String getStackTrace(Throwable t) {
        String stackTrace = null;
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            pw.close();
            sw.close();
            stackTrace = sw.getBuffer().toString();
        } catch (IOException ex) {
        }
        return stackTrace;
    }

    public void log(String msg) {
        filterConfig.getServletContext().log(msg);
    }

}
