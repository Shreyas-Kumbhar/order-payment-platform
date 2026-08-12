package com.shreyas.order_payment_platform.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthentication extends OncePerRequestFilter {
            final private JwtTokenProvider jwtTokenProvider;
            final private CustomUserDetailsService customUserDetailsService;

            @Override
            protected void doFilterInternal(@NonNull HttpServletRequest request,
                                            @NonNull HttpServletResponse response,
                                            @NonNull FilterChain filterChain) throws IOException, ServletException{
                String token=extractTokenFromRequest(request);
                if(token!=null && jwtTokenProvider.validateToken(token)){
                    String username=jwtTokenProvider.getUsernameFromToken(token);
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
                filterChain.doFilter(request, response);
            }
            private String extractTokenFromRequest(HttpServletRequest request){
                String bearerToken = request.getHeader("Authorization");
                if(bearerToken != null && bearerToken.startsWith("Bearer ")){
                    return bearerToken.substring(7);
                }
                return null;
            }
}
