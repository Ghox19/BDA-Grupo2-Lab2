package com.grupo2backend.JWT;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JWTAuthenticationFilter extends OncePerRequestFilter {
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String path = request.getRequestURI();

        if (path.startsWith("/Auth") || path.startsWith("/public") || path.startsWith("/auditoria")){
            chain.doFilter(request,response);
            return;
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("JWT")) {
                    try {
                        String token = cookie.getValue();
                        DecodedJWT decodedJWT = JWTUtil.verifyToken(token);

                        String name = decodedJWT.getSubject();
                        String rol = decodedJWT.getClaim("rol").asString();

                        if (name != null) {
                            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_"+rol));

                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(name, null, authorities);
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                    } catch (JWTVerificationException e) {
                        System.out.println("Invalid JWT");
                    }
                }
            }
        }
        chain.doFilter(request, response);
    }
}
