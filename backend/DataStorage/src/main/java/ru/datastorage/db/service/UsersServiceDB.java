package ru.datastorage.db.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.datastorage.db.entity.User;
import ru.datastorage.db.repository.UsersRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UsersServiceDB {
    private final UsersRepository usersRepository;

    public void addUser(User user) {
        log.info("Добавление user {} в БД", user.getCode());
        usersRepository.save(user);
    }

    public void updateUser(User user) {
        log.info("Обновление user с кодом : {}", user.getCode());
        if (usersRepository.existsById(user.getCode())) {
            usersRepository.save(user);
            log.info("User с кодом: {} обновлён успешно", user.getCode());
        } else {
            log.error("User c кодом: {} не найден", user.getCode());
            throw new EntityNotFoundException("User " + user.getCode() + " не найден");
        }
        usersRepository.save(user);
    }

    public List<User> getAll() {
        return usersRepository.findAll();
    }
}
