package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> findAll() {
        return users.values();
    }

    public User create(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        } else if (users.containsValue(user)) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        users.put(user.getId(), user);
        return user;
    }

    public User update(User newUser) {
        if (users.containsKey(newUser.getId())) {
            if (newUser.getId() == null) {
                throw new ConditionsNotMetException("Id должен быть указан");
            } else if (users.containsValue(newUser)) {
                throw new DuplicatedDataException("Этот имейл уже используется");
            }
            if (newUser.getEmail() == null) {
                newUser.setEmail(users.get(newUser.getId()).getEmail());
            }
            if (newUser.getPassword() == null) {
                newUser.setPassword(users.get(newUser.getId()).getPassword());
            }
            if (newUser.getUsername() == null) {
                newUser.setUsername(users.get(newUser.getId()).getUsername());
            }
            newUser.setRegistrationDate(users.get(newUser.getId()).getRegistrationDate());
            users.put(newUser.getId(), newUser);
            return newUser;
        }
        throw new NotFoundException("Пользователь не найден.");
    }

    public User findUserById(Long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new NotFoundException("Пользователь с ID " + id + " не найден.");
        }
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
