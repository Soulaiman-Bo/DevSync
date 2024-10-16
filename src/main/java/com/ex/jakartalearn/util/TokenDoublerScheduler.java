package com.ex.jakartalearn.util;

import com.ex.jakartalearn.entity.RequestQueue;
import com.ex.jakartalearn.entity.User;
import com.ex.jakartalearn.exceptions.UserNotFoundException;
import com.ex.jakartalearn.repository.RequestQueueRepository;
import com.ex.jakartalearn.repository.RequestRepository;
import com.ex.jakartalearn.service.UserService;
import jakarta.ejb.Schedule;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.List;

@Stateless
public class TokenDoublerScheduler {

    @Inject
    private RequestRepository requestRepository;

    @Inject
    private RequestQueueRepository requestQueueRepository;

    @Inject
    private UserService userService;

    //    @Schedule(hour = "1", minute = "0", second = "0", persistent = false)
    @Schedule(hour = "*", minute = "*/2", second = "0", persistent = false)
    public void processDoublingTokens() {
        System.out.println("===================================\n processDoublingTokens triggered \n =======================================");

        List<RequestQueue> requestQueues = requestQueueRepository.findAll();

        System.out.println("===================================\n Processed pending requests: " + requestQueues.size() + "\n =======================================");

        for (RequestQueue requestQueue : requestQueues) {
            try {
                User user = userService.getUserById(requestQueue.getUser().getId()).orElseThrow(() -> new UserNotFoundException("User not found"));
                Integer currentRefuseToken = user.getToken().getRefuse_token();
                user.getToken().setRefuse_token(currentRefuseToken + 2);
                requestQueue.getRequest().setIsFulfilled(true);
                requestQueueRepository.update(requestQueue);
                userService.updateUser(user);
            } catch (UserNotFoundException e) {
                e.printStackTrace();
                return;
            }
        }

        requestQueueRepository.deleteAll(requestQueues);
        System.out.println("Processed and cleared RequestQueue entries: " + requestQueues.size());

    }

}
