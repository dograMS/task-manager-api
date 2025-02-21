package com.example.todo.filter;

import com.example.todo.model.UserLogin;
import com.example.todo.service.JwtService;
import com.example.todo.service.UserLoginService;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtHandler;
import io.jsonwebtoken.JwtHandlerAdapter;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static io.jsonwebtoken.Jwts.parserBuilder;


@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            String tokens = getTokens(request);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if(tokens != null && auth == null){
                String username = jwtService.getUsername(tokens);

                UserLogin userLoginDetails = applicationContext.getBean(UserLoginService.class)
                        .loadUserByUsername(username);


                if(jwtService.validateTokens(tokens, userLoginDetails)){

                    UsernamePasswordAuthenticationToken upat = new UsernamePasswordAuthenticationToken(userLoginDetails, null,
                            userLoginDetails.getAuthorities());
                    upat.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(upat);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            filterChain.doFilter(request,response);
        }

    }

    private String getTokens(HttpServletRequest request){

        String bearerTokens = request.getHeader("Authorization");
        if(bearerTokens != null && bearerTokens.startsWith("Bearer "))
            return bearerTokens.substring(7);

        return null;
    }
}
