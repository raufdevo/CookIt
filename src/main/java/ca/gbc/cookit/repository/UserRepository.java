package ca.gbc.cookit.repository;

import ca.gbc.cookit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
