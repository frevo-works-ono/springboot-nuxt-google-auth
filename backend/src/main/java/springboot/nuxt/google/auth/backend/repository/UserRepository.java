package springboot.nuxt.google.auth.backend.repository;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
    public Optional<Users> findByEmail(String email) {
        return Optional.ofNullable(new Users("h.ono@frevo-works.co.jp", "おの"));
    }
}
