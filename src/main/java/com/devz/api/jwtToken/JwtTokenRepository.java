package com.devz.api.jwtToken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JwtTokenRepository extends JpaRepository<JwtToken, UUID> {

    @Query(value = """
      select t from JwtToken t inner join AppUser u\s
      on t.user.userId = u.userId\s
      where u.userId = :userId and (t.expired = false or t.revoked = false)\s
      """)
    List<JwtToken> findAllValidTokenByUser(UUID userId);

    Optional<JwtToken> findByToken(String jwtToken);
}
