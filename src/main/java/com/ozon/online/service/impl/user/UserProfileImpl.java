package com.ozon.online.service.impl.user;

import com.ozon.online.dto.user.DeleteProfileDto;
import com.ozon.online.entity.User;
import com.ozon.online.exception.ErrorResponse;
import com.ozon.online.repository.UserRepository;
import com.ozon.online.service.DeleteService;
import com.ozon.online.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserProfileImpl implements DeleteService {
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

//    @Override
//    @Transactional
//    public ResponseEntity<?> getProfile(@RequestBody ProfileDto deleteProfileDto) {
//        Optional<User> userOptional = userService.getAuthenticationPrincipalUserByNickname();
//        if (userOptional.isPresent() && passwordEncoder.matches(deleteProfileDto.getPassword(), userOptional.get().getPassword())) {
//            userRepository.delete(userOptional.get());
//            return ResponseEntity.ok().body("PROFILE_DELETED_SUCCESSFULLY");
//        } else {
//            return ResponseEntity.ok().body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), "PROFILE_NOT_DELETED"));
//        }
//    }

    @Override
    @Transactional
    public ResponseEntity<?> deleteProfile(@RequestBody DeleteProfileDto deleteProfileDto) {
        User userOptional = userService.getAuthenticationPrincipalUserByNickname().orElseThrow();

        if (passwordEncoder.matches(deleteProfileDto.getPassword(), userOptional.getPassword())) {
            userRepository.delete(userOptional);
            return ResponseEntity.ok().body("PROFILE_DELETED_SUCCESSFULLY");
        }

        return ResponseEntity.ok().body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), "PROFILE_NOT_DELETED"));
    }


}
