package com.ex.jakartalearn.service;


import com.ex.jakartalearn.entity.Task;
import com.ex.jakartalearn.entity.Token;
import com.ex.jakartalearn.entity.User;
import com.ex.jakartalearn.exceptions.TokenNotFoundException;
import com.ex.jakartalearn.exceptions.UserNotFoundException;
import com.ex.jakartalearn.repository.TaskRepository;
import com.ex.jakartalearn.repository.TokenRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

@Stateless
public class TokenService {

    @Inject
    private TokenRepository tokenRepository;

    public Optional<Token> getTokenByUser(User user) {
        Token token =  tokenRepository.findByUser(user);
        return Optional.of(token);
    }

    public Token subtractToken(User user) {
        Token token = this.getTokenByUser(user).orElseThrow(() -> new TokenNotFoundException("Token Not Found"));

        if(token.getRefuse_token() <= 0){
            throw new IllegalArgumentException("You can not refuse this task, because you consumed all of your tokens");
        }

        token.setRefuse_token(token.getRefuse_token() - 1);

        return tokenRepository.update(token);
    }






}
