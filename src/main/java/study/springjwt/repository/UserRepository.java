package study.springjwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.springjwt.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Integer> { //식별자 타입은 래퍼런스 타입을 넣는다.

    Boolean existsByUsername(String username); //메소드 이름으로 쿼리 생성. username에 해당하는 데이터가 존재하는지
}
