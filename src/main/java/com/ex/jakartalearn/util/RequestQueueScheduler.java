package com.ex.jakartalearn.util;

import com.ex.jakartalearn.entity.Request;
import com.ex.jakartalearn.entity.RequestQueue;
import com.ex.jakartalearn.repository.RequestQueueRepository;
import com.ex.jakartalearn.repository.RequestRepository;
import jakarta.ejb.Schedule;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class RequestQueueScheduler {

    @Inject
    private RequestRepository requestRepository;

    @Inject
    private RequestQueueRepository requestQueueRepository;

    @Schedule(hour = "*", minute = "*/1", second = "0", persistent = false)
    public void processPendingRequests() {
        LocalDateTime twelveHoursAgo = LocalDateTime.now().minusHours(12);

        List<Request> pendingRequests = requestRepository.findRequestsOlderThan(twelveHoursAgo);

        for (Request request : pendingRequests) {
            RequestQueue requestQueue = new RequestQueue();
            requestQueue.setRequest(request);
            requestQueue.setUser(request.getUser());
            requestQueue.getRequest().setIsQueued(true);
            requestQueueRepository.save(requestQueue);

        }
        System.out.println("===================================\n Processed pending requests: " + pendingRequests.size() + "\n =======================================");
    }

}
