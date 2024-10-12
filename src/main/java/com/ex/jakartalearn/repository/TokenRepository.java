package com.ex.jakartalearn.repository;

import com.ex.jakartalearn.entity.Token;
import com.ex.jakartalearn.entity.User;
import jakarta.ejb.Stateless;

@Stateless
public class TokenRepository  extends BaseRepository<Token>{

    public TokenRepository() {
        super(Token.class);
    }

    public Token findByUser(User user) {
        return entityManager.createQuery(
                        "SELECT t FROM Token t WHERE t.user = :user",
                        Token.class)
                .setParameter("user", user)
                .getSingleResult();
    }

}
