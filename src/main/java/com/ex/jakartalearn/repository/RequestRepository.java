package com.ex.jakartalearn.repository;

import com.ex.jakartalearn.entity.Request;
import com.ex.jakartalearn.entity.Token;
import jakarta.ejb.Stateless;

@Stateless
public class RequestRepository extends BaseRepository<Request> {

    public RequestRepository() {
        super(Request.class);
    }

}
