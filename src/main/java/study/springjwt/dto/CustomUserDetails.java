package study.springjwt.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import study.springjwt.entity.UserEntity;

import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final UserEntity userEntity;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { //Role값을 반환

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() { //GrantedQuthority()를 구현
            @Override
            public String getAuthority() {
                return userEntity.getRole();
            }
        });

        return collection;
    }

    @Override
    public String getPassword() { //패스워드 값을 반환

        return userEntity.getPassword();
    }

    @Override
    public String getUsername() { //username을 반환

        return userEntity.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() { //전부 true로 바꿔준다. 계정이 만료되었는지 여부
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
