package ru.datastorage.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.datastorage.db.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
}