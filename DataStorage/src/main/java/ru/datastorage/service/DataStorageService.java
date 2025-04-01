package ru.datastorage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.datastorage.db.entity.Phone;
import ru.datastorage.db.service.PhonesServiceDB;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataStorageService {
    private final PhonesServiceDB phonesServiceDB;

    public void addNewPhone(Phone phone) {
        phonesServiceDB.addPhone(phone);
    }
}
