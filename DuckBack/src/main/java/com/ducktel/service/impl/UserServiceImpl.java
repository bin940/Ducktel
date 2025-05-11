package com.ducktel.service.impl;

import com.ducktel.domain.entity.Accommodation;
import com.ducktel.domain.entity.AccommodationLike;
import com.ducktel.domain.entity.User;
import com.ducktel.domain.repository.AccommodationLikeRepository;
import com.ducktel.domain.repository.AccommodationRepository;
import com.ducktel.domain.repository.UserRepository;
import com.ducktel.dto.UserDTO;
import com.ducktel.exception.CustomException;
import com.ducktel.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        log.debug("유저 등록 요청: {}", userDTO);

        if (userRepository.existsByUsername(userDTO.getUsername())) {
            log.warn("유저 등록 실패 - 이미 존재하는 사용자명: {}", userDTO.getUsername());
            throw new CustomException(400, "BAD_REQUEST", "이미 존재하는 사용자명입니다.");
        }

        User user = userDTO.createUser(passwordEncoder);
        User result = userRepository.save(user);

        log.info("유저 등록 성공: userId={}", result.getUserId());
        return result.getName();
    }

    @Override
    public UserDTO getProfile(String userId) {
        log.debug("유저 프로필 조회 요청: userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("유저 프로필 조회 실패 - 등록되지 않은 사용자: userId={}", userId);
                    return new CustomException(404, "NOT_FOUND", "등록되지 않은 사용자입니다: " + userId);
                });

        log.info("유저 프로필 조회 성공: userId={}", userId);
        return user.getUser();
    }

    @Override
    public UserDTO updateProfile(String userId, UserDTO userData) {
        log.debug("유저 프로필 업데이트 요청: userId={}, userData={}", userId, userData);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("유저 프로필 업데이트 실패 - 등록되지 않은 사용자: userId={}", userId);
                    return new CustomException(404, "NOT_FOUND", "등록되지 않은 사용자입니다: " + userId);
                });

        user = userData.updateUser(user);
        User updatedUser = userRepository.save(user);

        log.info("유저 프로필 업데이트 성공: userId={}", userId);
        return updatedUser.updateUser(userData);
    }

    @Override
    public String deleteProfile(String userId) {
        log.debug("유저 프로필 삭제 요청: userId={}", userId);

        if (!userRepository.existsById(userId)) {
            log.warn("유저 프로필 삭제 실패 - 유저를 찾을 수 없음: userId={}", userId);
            throw new CustomException(404, "NOT_FOUND", "유저를 찾을 수 없습니다. ID: " + userId);
        }

        userRepository.deleteById(userId);
        log.info("유저 프로필 삭제 성공: userId={}", userId);
        return "삭제되었습니다.";
    }

    @Override
    public String passWordReset(String userId, String newPassword) {
        log.debug("비밀번호 재설정 요청: userId={}", userId);

        int insert = userRepository.updatePassword(userId, passwordEncoder.encode(newPassword));
        log.info("비밀번호 재설정 성공: userId={}, 변경된 레코드 수={}", userId, insert);

        return "비밀번호가 변경되었습니다.";
    }

    @Override
    public String toggleLike(String userId, Long accommodationId) {
        log.debug("좋아요 토글 요청: userId={}, accommodationId={}", userId, accommodationId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("좋아요 토글 실패 - 사용자 없음: userId={}", userId);
                    return new CustomException(404, "NOT_FOUND", "사용자 없음");
                });

        Accommodation accommodation = accommodationRepository.findById(accommodationId)
                .orElseThrow(() -> {
                    log.warn("좋아요 토글 실패 - 숙소 없음: accommodationId={}", accommodationId);
                    return new CustomException(404, "NOT_FOUND", "숙소 없음");
                });

        Optional<AccommodationLike> existingLike = likeRepository.findByUserAndAccommodation(user, accommodation);

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            accommodation.setLikeCount(accommodation.getLikeCount() - 1);
            accommodationRepository.save(accommodation);

            log.info("좋아요 취소 성공: userId={}, accommodationId={}", userId, accommodationId);
            return "좋아요 취소됨";
        } else {
            AccommodationLike newLike = new AccommodationLike();
            newLike.setUser(user);
            newLike.setAccommodation(accommodation);
            likeRepository.save(newLike);

            accommodation.setLikeCount(accommodation.getLikeCount() + 1);
            accommodationRepository.save(accommodation);

            log.info("좋아요 추가 성공: userId={}, accommodationId={}", userId, accommodationId);
            return "좋아요 추가됨";
        }
    }
}


