package com.dark.online.repository;

import com.dark.online.dto.user.UserLoginDto;
import com.dark.online.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    @Query("SELECT new com.dark.online.dto.user.UserLoginDto(u.password, u.accountVerified, u.nickname, u.roles) FROM User u WHERE u.nickname = :nickname")
    Optional<UserLoginDto> findUserDtoByNickname(@Param("nickname") String nickname);

    @Query("SELECT u FROM User u WHERE u.nickname = :nickname")
    Optional<User> findByNickname(@Param("nickname") String nickname);


}
