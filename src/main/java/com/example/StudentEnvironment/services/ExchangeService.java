package com.example.StudentEnvironment.services;

import com.example.StudentEnvironment.entities.ExchangeRequest;
import com.example.StudentEnvironment.entities.Place;
import com.example.StudentEnvironment.entities.User;
import com.example.StudentEnvironment.repositories.ExchangeRequestRepository;
import com.example.StudentEnvironment.repositories.PlaceRepository;
import com.example.StudentEnvironment.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExchangeService {
    private final ExchangeRequestRepository exchangeRequestRepository;
    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;

    /**
     * Получает список всех входящих запросов на обмен для пользователя.
     * @param user пользователь, которому направлены запросы
     * @return список ExchangeRequest, направленных пользователю
     */
    public List<ExchangeRequest> findRequestsToUser(User user) {
        return exchangeRequestRepository.findByToUser(user);
    }
    public void sendExchangeRequest(User from, User to) {
        if (exchangeRequestRepository.existsByFromUser(from)) {
            throw new IllegalStateException("Уже есть активный запрос");
        }
        if (from.getId().equals(to.getId())) {
            throw new IllegalArgumentException("Нельзя отправить запрос самому себе");
        }

        ExchangeRequest request = ExchangeRequest.builder()
                .fromUser(from)
                .toUser(to)
                .createdAt(LocalDateTime.now())
                .build();
        exchangeRequestRepository.save(request);
    }

    public void cancelExchangeRequest(User from) {
        exchangeRequestRepository.deleteByFromUser(from);
    }

    @Transactional
    public void confirmExchangeRequest(Long requestId) {
        ExchangeRequest request = exchangeRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Запрос не найден"));

        User from = request.getFromUser();
        User to = request.getToUser();

        Place placeFrom = placeRepository.findByUser(from);
        Place placeTo = placeRepository.findByUser(to);

        if (placeFrom == null || placeTo == null) {
            throw new IllegalStateException("Один из пользователей не в очереди");
        }

        LocalDateTime temp = placeFrom.getTime();
        placeFrom.setTime(placeTo.getTime());
        placeTo.setTime(temp);

        placeRepository.save(placeFrom);
        placeRepository.save(placeTo);

        from.setExchange_user(to);
        to.setExchange_user(from);
        userRepository.save(from);
        userRepository.save(to);

        exchangeRequestRepository.deleteByFromUserOrToUser(from, to);
    }

    public List<ExchangeRequest> getIncomingRequests(User user) {
        return exchangeRequestRepository.findByToUser(user);
    }
}
