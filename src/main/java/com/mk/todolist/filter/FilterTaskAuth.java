package com.mk.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mk.todolist.users.IUserRepository;
import com.mk.todolist.users.UserModel;

import at.favre.lib.crypto.bcrypt.BCrypt;
import at.favre.lib.crypto.bcrypt.BCrypt.Result;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {
    @Autowired
    private IUserRepository usersRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain)
            throws ServletException, IOException {
        String path = req.getServletPath();
        if (path.startsWith("/tasks")) {
            String headerAuthorization = req.getHeader("Authorization");

            if (headerAuthorization != null) {
                String encoded = headerAuthorization.substring("basic".length()).trim();
                byte[] decoded = Base64.getDecoder().decode(encoded);

                String auth = new String(decoded);
                String[] credentials = auth.split(":");
                String username = credentials[0];
                String password = credentials[1];

                UserModel user = this.usersRepository.findByUsername(username);

                if (user instanceof UserModel) {
                    Result match = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword().toCharArray());

                    if (match.verified) {
                        req.setAttribute("id", user.getId());
                        filterChain.doFilter(req, res);

                    } else {
                        res.sendError(401, "Invalid credentials");

                    }
                } else {
                    res.sendError(401, "Invalid credentials");

                }
            } else {
                filterChain.doFilter(req, res);

            }
        } else {
            filterChain.doFilter(req, res);

        }
    }

}
