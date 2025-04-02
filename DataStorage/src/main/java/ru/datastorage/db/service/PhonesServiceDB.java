package ru.datastorage.db.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.datastorage.db.entity.Phone;
import ru.datastorage.db.repository.PhonesRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PhonesServiceDB {
    private final PhonesRepository phonesRepository;

    public void addPhone(Phone phone) {
        log.info("Добавление iPhone {} в БД", phone.getModel());
        phonesRepository.save(phone);
    }

    public Phone updatePhone(Phone phone) {
        log.info("Обновление iphone {}", phone.getModel());
        if (phonesRepository.existsById(phone.getModel())) {
            phonesRepository.save(phone);
            log.info("iPhone {} обновлён успешно", phone.getModel());
        } else {
            log.error("Phone модели: {} не найден", phone.getModel());
            throw new EntityNotFoundException("iPhone " + phone.getModel() + " не найден");
        }
        return phonesRepository.save(phone);
    }

    public Phone getPhoneById(String phoneModel) {
        return phonesRepository.findById(phoneModel)
                .orElseThrow(() -> new EntityNotFoundException("Phone модели: " + phoneModel + " не найден"));
    }

    public List<Phone> getAll() {
        return phonesRepository.findAll();
    }
}
