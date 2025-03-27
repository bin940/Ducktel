package com.ducktel.service;

import com.ducktel.domain.entity.Accommodation;
import com.ducktel.domain.entity.AccommodationLike;
import com.ducktel.domain.repository.AccommodationLikeRepository;
import com.ducktel.domain.repository.AccommodationRepository;
import com.ducktel.exception.CustomException;
import com.ducktel.domain.entity.User;
import com.ducktel.domain.repository.UserRepository;
import com.ducktel.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccommodationLikeRepository likeRepository;
    private final AccommodationRepository accommodationRepository;
    @Override
    public String registerUser(UserDTO userDTO) {
        if(userRepository.existsByUsername(userDTO.getUsername())) {
            throw new CustomException("USERNAME_ALREADY_EXISTS", "이미 존재하는 사용자명입니다.");
        }
        User user = userDTO.createUser(passwordEncoder);

        User result= userRepository.save(user);
        return result.getName();
    }

    @Override
    public UserDTO getProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("등록되지 않은 사용자입니다: " + userId));

        return user.getUser();
    }

    @Override
    public UserDTO updateProfile(String userId, UserDTO userData) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("등록되지 않은 사용자입니다: " + userId));
       user = userData.updateUser(user);

        User updatedUser = userRepository.save(user);

        return updatedUser.updateUser(userData);
    }
    //user return enum 상수로 관리!!
    @Override
    public String deleteProfile(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new CustomException("NOT FOUND", "유저를 찾을 수 없습니다. ID: " + userId);
        }
        userRepository.deleteById(userId);
        return "삭제되었습니다.";
    }

    @Override
    public String passWordReset(String userId, String newPassword) {
        int insert =userRepository.updatePassword(userId, passwordEncoder.encode(newPassword));
        log.info(insert + "개가 변경 되었습니다.");
        return "비밀번호가 변경 되었습니다.";
    }

    @Override
    public String toggleLike(String userId, Long accommodationId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("NOT_FOUND", "사용자 없음"));
        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> new CustomException("NOT_FOUND", "숙소 없음"));

        Optional<AccommodationLike> existingLike = likeRepository.findByUserAndAccommodation(user, accommodation);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());

            accommodation.setLikeCount(accommodation.getLikeCount() - 1);
            accommodationRepository.save(accommodation);

            return "좋아요 취소됨";
        } else {
            AccommodationLike newLike = new AccommodationLike();
            newLike.setUser(user);
            newLike.setAccommodation(accommodation);
            likeRepository.save(newLike);

            accommodation.setLikeCount(accommodation.getLikeCount() + 1);
            accommodationRepository.save(accommodation);

            return "좋아요 추가됨";
        }
    }
}


