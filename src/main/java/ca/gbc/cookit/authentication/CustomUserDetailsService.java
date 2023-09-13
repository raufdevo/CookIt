package ca.gbc.cookit.authentication;

import ca.gbc.cookit.model.User;
import ca.gbc.cookit.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User userByUsername = this.userService.findByUsername(username);

        if (userByUsername.getUsername().equals(username)) {
            return new CustomUserDetails(userByUsername);
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
}
