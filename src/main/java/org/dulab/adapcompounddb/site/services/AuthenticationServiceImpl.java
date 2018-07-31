package org.dulab.adapcompounddb.site.services;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dulab.adapcompounddb.models.entities.UserPrincipal;
import org.dulab.adapcompounddb.site.repositories.UserPrincipalRepository;
import org.hibernate.Hibernate;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final Logger LOG = LogManager.getLogger();
    private static final SecureRandom RANDOM;
    private static final int HASHING_ROUNDS = 10;

    static {
        try {
            RANDOM = SecureRandom.getInstanceStrong();
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private final UserPrincipalRepository userPrincipalRepository;

    @Autowired
    public AuthenticationServiceImpl(UserPrincipalRepository userPrincipalRepository) {
        this.userPrincipalRepository = userPrincipalRepository;
    }

    @Override
    @Transactional
    public UserPrincipal authenticate(String username, String password) {

        UserPrincipal principal = userPrincipalRepository.findUserPrincipalByUsername(username).orElse(null);

        if (principal == null) {
            LOG.warn("Authentication failed for non-existent user {}.", username);
            return null;
        }

        if (!BCrypt.checkpw(password, principal.getHashedPassword())) {
            LOG.warn("Authentication failed for user {}.", username);
            return null;
        }

        LOG.debug("User {} successfully authenticated.", username);

        return (UserPrincipal) Hibernate.unproxy(principal);
    }

    @Override
    @Transactional
    public void saveUser(UserPrincipal principal, String password) {
        if (password != null && password.length() > 0) {
            String salt = BCrypt.gensalt(HASHING_ROUNDS, RANDOM);
//            principal.setHashedPassword(password.getBytes());
            principal.setHashedPassword(BCrypt.hashpw(password, salt));
        }
        principal.assignDefaultRole();
        userPrincipalRepository.save(principal);
    }

    @Override
    @Transactional
    public Optional<UserPrincipal> findUser(long id) {
        return userPrincipalRepository.findById(id);
//        return Optional.ofNullable(userPrincipalRepository.findOne(id));
    }
}
