package com.ex.jakartalearn.service;

import com.ex.jakartalearn.entity.Request;
import com.ex.jakartalearn.repository.RequestRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.Optional;

@Stateless
public class RequestService {

    @Inject
    private RequestRepository requestRepository;

    public Optional<Request> createRequest(Request request) {
        requestRepository.save(request);
        return Optional.of(request);
    }

}
