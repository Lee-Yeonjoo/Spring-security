package study.springjwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.springjwt.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Integer> { //식별자 타입은 래퍼런스 타입을 넣는다.

}
