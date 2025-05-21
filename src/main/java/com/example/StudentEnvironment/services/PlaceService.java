package com.example.StudentEnvironment.services;

import com.example.StudentEnvironment.entities.Place;
import com.example.StudentEnvironment.entities.User;
import com.example.StudentEnvironment.repositories.PlaceRepository;
import com.example.StudentEnvironment.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
/**
 * Сервис для работы с очередью мест.
 */
@Service
@AllArgsConstructor
public class PlaceService {
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    /**
     * Метод создания места в таблице
     * @param time время
     * @param user_id ID пользователя
     */
    public void create(LocalDateTime time, Long user_id) {
        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        Place place = Place.builder()
                .time(time)
                .user(user)
                .build();
        placeRepository.save(place);
    }


    /**
     * Метод проверки существования места
     * @param id ID места
     * @return истина или ложь
     */
    public boolean exists(Long id) {
        List<Place> places = placeRepository.findAll();
        for (Place place : places){
            if (place.getId().equals(id)){
                return true;
            }
        }
        return false;
    }



    /**
     * Метод получения всех мест
     * @return список всех мест
     */
    public List<Place> readAll() {
        return placeRepository.findAll();
    }

    /**
     * Метод получения всех мест определенного пользователя
     * @param id ID пользователя
     * @return список мест пользователя
     */
    public List<Place> readAllByUserId(Long id) {
        List<Place> places = placeRepository.findAll();
        List<Place> filtered_places = new ArrayList<>();
        for(Place place : places)
        {
            if(place.getUser().getId().equals(id))
            {
                filtered_places.add(place);
            }
        }
        return filtered_places;
    }
    /**
     * Метод получения места по ID
     * @param place_id ID места
     * @return место
     */
    public Place getPlaceByID(Long place_id){
        List<Place> places = placeRepository.findAll();
        for (Place place : places){
            if (place.getId().equals(place_id)){
                return place;
            }
        }
        return null;
    }
    /**
     * Метод удаления места. Доступен владельцу или старосте.
     * @param placeId ID места
     * @param currentUser текущий пользователь
     */
    public void deletePlace(Long placeId, User currentUser) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new RuntimeException("Place not found"));

        boolean isOwner = place.getUser().getId().equals(currentUser.getId());
        boolean isHeadman = currentUser.getRole().equals("HEADMAN");

        if (!isOwner && !isHeadman) {

            throw new AccessDeniedException("Недостаточно прав");
        }

        placeRepository.deleteById(placeId);
        System.out.println("Удалили место");
    }
    /**
     * Метод добавления пользователя в конец очереди
     * @param user пользователь
     */
    public void addToEnd(User user) {
        // Удаляем старое место, если есть
        Place oldPlace = placeRepository.findByUser(user);
        if (oldPlace != null) {
            placeRepository.deleteById(oldPlace.getId());
        }

        // Находим самое позднее время в очереди
        LocalDateTime maxTime = placeRepository.findMaxTime();
        LocalDateTime newTime = (maxTime != null) ? maxTime.plusSeconds(1) : LocalDateTime.now();

        // Проверяем, существует ли уже место для данного пользователя
        Place existingPlace = placeRepository.findByUser(user);
        if (existingPlace == null) {
            // Сохраняем новое место
            Place newPlace = new Place();
            newPlace.setUser(user);
            newPlace.setTime(newTime);
            placeRepository.save(newPlace);
        } else {
            // Если место уже существует, просто обновляем время
            existingPlace.setTime(newTime);
            placeRepository.save(existingPlace);
        }
    }

    /**
     * Метод отправки заявки на обмен местами
     * @param fromUser отправитель
     * @param toPlaceId ID места, с которым хотят обменяться
     */
    public void requestExchange(User fromUser, Long toPlaceId) {
        Place toPlace = placeRepository.findById(toPlaceId)
                .orElseThrow(() -> new RuntimeException("Place not found"));

        if (toPlace.getUser().getId().equals(fromUser.getId())) {
            throw new IllegalArgumentException("Нельзя обмениваться с самим собой");
        }

        // fromUser хочет обменяться с toUser
        fromUser.setExchange_user(toPlace.getUser());
        userRepository.save(fromUser);
    }

    /**
     * Метод вставки пользователя перед другим. Доступен только старосте.
     * @param beforePlaceId ID места перед которым нужно вставить
     * @param studentId ID студента
     * @param headman текущий пользователь (должен быть старостой)
     */
    @Transactional
    public void insertBefore(Long beforePlaceId, Long studentId, User headman) {
        // Проверка на роль
        if (!"HEADMAN".equals(headman.getRole())) {
            throw new AccessDeniedException("Только староста может вставлять студентов");
        }

        // Поиск студента
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Поиск места, перед которым вставляем
        Place beforePlace = placeRepository.findById(beforePlaceId)
                .orElseThrow(() -> new RuntimeException("Place not found"));

        // Если студент пытается вставить себя перед собой — ничего не делаем
        if (beforePlace.getUser().getId().equals(student.getId())) {
            System.out.println("Пользователь уже стоит на этом месте");
            return;
        }

        // Назначаем новое время чуть раньше
        LocalDateTime newTime = beforePlace.getTime().minusNanos(1);

        // Либо обновляем время, либо создаём новую запись
        Place place = placeRepository.findByUser(student);
        if (place == null) {
            place = new Place();
            place.setUser(student);
        }

        place.setTime(newTime);
        placeRepository.save(place);
    }

//    public void insertBefore(Long beforePlaceId, Long studentId, User headman) {
//        if (!headman.getRole().equals("HEADMAN")) {
//            throw new AccessDeniedException("Только староста может вставлять студентов");
//        }
//
//        User student = userRepository.findById(studentId)
//                .orElseThrow(() -> new RuntimeException("Student not found"));
//
//        // Удаляем старое место студента, если есть
//        Place existingPlace = placeRepository.findByUser(student);
//        if (existingPlace != null) {
//            placeRepository.delete(existingPlace);
//        }
//
//        Place beforePlace = placeRepository.findById(beforePlaceId)
//                .orElseThrow(() -> new RuntimeException("Place not found"));
//
//        LocalDateTime newTime = beforePlace.getTime().minusNanos(1);
////
////        if (existingPlace == null) {
////            Place newPlace = new Place();
////            newPlace.setUser(student);
////            newPlace.setTime(newTime);
////            placeRepository.save(newPlace);
////        } else {
////            existingPlace.setTime(newTime);
////            placeRepository.save(existingPlace);
////        }
//
//
//        Place place = placeRepository.findByUser(student);
//
//        if (place == null) {
//            place = new Place();
//            place.setUser(student);
//        }
//        place.setTime(beforePlace.getTime().minusNanos(1));
//        placeRepository.save(place);
//    }

    /**
     * Метод удаления места по ID
     * @param id ID места
     */
    public void delete(Long id) {
        placeRepository.deleteById(id);
    }
}
