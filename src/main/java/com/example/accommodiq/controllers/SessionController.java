package com.example.accommodiq.controllers;

import com.example.accommodiq.dtos.CredentialsDto;
import com.example.accommodiq.dtos.UserLoginDto;
import com.example.accommodiq.security.jwt.JwtTokenUtil;
import com.example.accommodiq.services.interfaces.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sessions")
public class SessionController {
    final IAccountService accountService;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public SessionController(IAccountService accountService, AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil) {
        this.accountService = accountService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping
    public CredentialsDto login(@RequestBody CredentialsDto credentialsDto) {
        UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(credentialsDto.getEmail(),
                credentialsDto.getPassword());
        Authentication auth = authenticationManager.authenticate(authReq);

        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(auth);

        String token = jwtTokenUtil.generateToken(credentialsDto.getEmail());
        credentialsDto.setJwt(token);

        return credentialsDto;
    }
}
