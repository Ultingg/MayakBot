package ru.kumkuat.application.GameModule.Repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.kumkuat.application.GameModule.Models.Audio;
import ru.kumkuat.application.GameModule.Models.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User getById(Long id);
}
