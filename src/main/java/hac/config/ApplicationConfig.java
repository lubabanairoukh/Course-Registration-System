package hac.config;

import hac.entity.Student;
import hac.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class ApplicationConfig {

    private StudentRepository studentRepository;
    @Autowired
    public void studentRepository(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder bCryptPasswordEncoder) {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        String filePath = "file.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                String username = parts[0];
                String password = parts[1];

                manager.createUser(User.withUsername(username)
                        .password(bCryptPasswordEncoder.encode(password))
                        .roles("STUDENT")
                        .build());
                studentRepository.save(new Student(username));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        manager.createUser(User.withUsername("211559901")
                .password(bCryptPasswordEncoder.encode("lubabaNairoukh"))
                .roles("ADMIN")
                .build());
        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .csrf(withDefaults())

                .authorizeHttpRequests((requests) -> requests
                                .requestMatchers("/img/**", "/", "/logout").permitAll()
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .requestMatchers("/student/**").hasRole("STUDENT")
                                .requestMatchers("/**").hasAnyRole("ADMIN", "STUDENT")
                )
                .formLogin((form) -> form
                                .loginPage("/login")
//                                .loginProcessingUrl("/login")
                                .successHandler((request, response, authentication) -> {
                                    for (GrantedAuthority auth : authentication.getAuthorities()) {
                                        if (auth.getAuthority().equals("ROLE_ADMIN")) {
                                            response.sendRedirect("/admin");
                                            return;
                                        }
                                        if(auth.getAuthority().equals("ROLE_STUDENT")) {
                                            response.sendRedirect("/student");
                                            return;
                                        }
                                    }
                                    response.sendRedirect("/");
                                })
//                                .failureUrl("/")

                        .permitAll()
                )
                .logout(
                        logout -> logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .permitAll()
                )
                .exceptionHandling(
                        (exceptionHandling) -> exceptionHandling
                                .accessDeniedPage("/403")
                )

        ;

        return http.build();

    }


}
