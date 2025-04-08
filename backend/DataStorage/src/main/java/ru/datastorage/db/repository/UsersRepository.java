package ru.datastorage.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.datastorage.db.entity.User;

public interface UsersRepository extends JpaRepository<User, Integer> {
}