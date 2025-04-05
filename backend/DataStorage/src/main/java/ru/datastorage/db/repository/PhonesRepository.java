package ru.datastorage.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.datastorage.db.entity.Phone;

public interface PhonesRepository extends JpaRepository<Phone, String> {
}