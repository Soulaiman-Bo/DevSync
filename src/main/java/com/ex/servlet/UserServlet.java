package com.ex.servlet;

import com.ex.entity.User;
import com.ex.service.UserService;
import jakarta.inject.Inject;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "UserServlet", urlPatterns = {"/users/*"})
public class UserServlet extends HttpServlet {

    @Inject
    private UserService userService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // List all users
            List<User> users = userService.getAllUsers();
            request.setAttribute("users", users);
            request.getRequestDispatcher("/WEB-INF/users/list.jsp").forward(request, response);
        } else if (pathInfo.startsWith("/edit/")) {
            // Show edit form
            Long userId = Long.parseLong(pathInfo.substring(6));
            User user = userService.getUserById(userId);
            request.setAttribute("user", user);
            request.getRequestDispatcher("/WEB-INF/users/edit.jsp").forward(request, response);
        } else if (pathInfo.equals("/new")) {
            // Show create form
            request.getRequestDispatcher("/WEB-INF/users/create.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String method = request.getParameter("_method");

        if (method != null && method.equalsIgnoreCase("DELETE")) {
            doDelete(request, response);
        } else {
            // Handle regular POST for creating a new user
            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                // Create a new user
                User user = new User(
                        request.getParameter("username"),
                        request.getParameter("firstname"),
                        request.getParameter("lastname"),
                        request.getParameter("email"),
                        request.getParameter("password")
                );
                userService.createUser(user);
                response.sendRedirect(request.getContextPath() + "/users");
            }
        }
    }


    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo.startsWith("/")) {
            Long userId = Long.parseLong(pathInfo.substring(1));
            User user = userService.getUserById(userId);
            if (user != null) {
                user.setUsername(request.getParameter("username"));
                user.setFirstname(request.getParameter("firstname"));
                user.setLastname(request.getParameter("lastname"));
                user.setEmail(request.getParameter("email"));
                user.setPassword(request.getParameter("password"));
                userService.updateUser(user);
            }
        }
        response.sendRedirect(request.getContextPath() + "/users");
    }


    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo != null && pathInfo.startsWith("/")) {
            Long userId = Long.parseLong(pathInfo.substring(1));
            userService.deleteUser(userId);
        }
        response.sendRedirect(request.getContextPath() + "/users");
    }

}
