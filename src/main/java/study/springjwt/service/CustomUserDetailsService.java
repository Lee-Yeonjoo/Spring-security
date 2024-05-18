package study.springjwt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import study.springjwt.dto.CustomUserDetails;
import study.springjwt.entity.UserEntity;
import study.springjwt.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity userData = userRepository.findByUsername(username);
        System.out.println("userData.getUsername() = " + userData.getUsername());

        if (userData != null) { //조회한 엔티티를 검증

            return new CustomUserDetails(userData);
        }
        return null;
    }
}
