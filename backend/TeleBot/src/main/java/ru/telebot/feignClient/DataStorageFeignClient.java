package ru.telebot.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.library.dto.UserDto;

import java.util.List;

@FeignClient(name = "dataStorage", url = "${serviceUrl.dataStorage}")
public interface DataStorageFeignClient {
    @GetMapping("/storage/users/getAll")
    List<UserDto> getUsers();
    @PostMapping("/storage/users/addNew")
    void newUser(UserDto user);
    @PostMapping("/storage/users/update")
    void updateUser(UserDto user);

    @GetMapping("/storage/admin/get")
    List<String> getAdmin();
    @PostMapping("/storage/admin/update")
    void updateAdmin(List<String> admin);
}