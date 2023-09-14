package ca.gbc.cookit.authentication;


import ca.gbc.cookit.constant.Constants;
import ca.gbc.cookit.exception.BadRequestRuntimeException;
import ca.gbc.cookit.model.User;
import ca.gbc.cookit.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        try {
            User userByUsername = this.userService.findByUsername(s);
            return new CustomUserDetails(userByUsername);
        } catch (BadRequestRuntimeException badRequestRuntimeException) {
            if (badRequestRuntimeException.getMessageCode().equals(Constants.USER_NOT_FOUND)) {
                throw new UsernameNotFoundException("user not found with username: " + s);
            } else {
                throw badRequestRuntimeException;
            }
        }
    }
}
