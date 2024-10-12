package com.ex.jakartalearn.filter;

import com.ex.jakartalearn.bean.auth.UserSessionBean;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
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

@WebFilter(urlPatterns = {"/dashboard.xhtml", "/task/create.xhtml"})
@Priority(1)
public class AuthenticationFilter implements Filter {

    @Inject
    private UserSessionBean userSessionBean;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code, if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (userSessionBean == null || !userSessionBean.isLoggedIn()) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/auth/login.xhtml");
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        // Cleanup code, if needed
    }
}
