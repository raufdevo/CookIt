package ca.gbc.cookit.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;



@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {


    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http.csrf().disable()
                .authorizeRequests(auth -> {
                    try {
                        auth.antMatchers("/", "/h2-console/**", "/login/**", "/register", "/error", "/fp/**").permitAll().anyRequest().authenticated().and()
                                .formLogin().usernameParameter("username").passwordParameter("password").loginPage("/login").defaultSuccessUrl("/").failureUrl("/login?error").and().logout().logoutUrl("/logout").logoutSuccessUrl("/");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .build();
    }

}

