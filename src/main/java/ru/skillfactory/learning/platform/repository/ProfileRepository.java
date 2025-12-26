package ru.skillfactory.learning.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.skillfactory.learning.platform.entity.Profile;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    @Query("SELECT p FROM Profile p LEFT JOIN FETCH p.user WHERE p.id = :id")
    Optional<Profile> findByIdWithUser(@Param("id") Long id);

    @Query("SELECT p FROM Profile p LEFT JOIN FETCH p.user WHERE p.user.id = :userId")
    Optional<Profile> findByUserIdWithUser(@Param("userId") Long userId);
}
