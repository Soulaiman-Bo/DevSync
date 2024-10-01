package com.ex.servlet;

import com.ex.Enums.Role;
import com.ex.entity.User;
import com.ex.service.UserService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "HelloServlet", value = "/hello")
public class HelloServlet extends HttpServlet {

    @Inject
    private UserService userService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = new User("soula98", "soulaiman", "bouhlal", "soulain^gmail.com", "fssjndfgjsdf");

        userService.createUser(user);

        response.setContentType("text/html");
        response.getWriter().println("User created");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Handle POST requests if needed
    }
}
