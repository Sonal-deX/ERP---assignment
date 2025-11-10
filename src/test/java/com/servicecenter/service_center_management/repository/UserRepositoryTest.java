package com.servicecenter.service_center_management.repository;

import com.servicecenter.service_center_management.entity.User;
import com.servicecenter.service_center_management.entity.User.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for UserRepository.
 * Uses @DataJpaTest for repository layer testing with in-memory database.
 */
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User customer;
    private User employee;
    private User admin;

    @BeforeEach
    void setUp() {
        // TODO: Create and persist test users using entityManager.persist()
        // customer = new User(); ... entityManager.persist(customer);
        // employee = new User(); ... entityManager.persist(employee);
        // admin = new User(); ... entityManager.persist(admin);
        // entityManager.flush();
    }

    @Test
    void testFindByEmail_Success() {
        // TODO: Call userRepository.findByEmail()
        // TODO: Assert that Optional is present
        // TODO: Assert that user email matches
    }

    @Test
    void testFindByEmail_NotFound() {
        // TODO: Call userRepository.findByEmail() with non-existent email
        // TODO: Assert that Optional is empty
    }

    @Test
    void testExistsByEmail_ReturnsTrue() {
        // TODO: Call userRepository.existsByEmail()
        // TODO: Assert that it returns true for existing email
    }

    @Test
    void testExistsByEmail_ReturnsFalse() {
        // TODO: Call userRepository.existsByEmail() with non-existent email
        // TODO: Assert that it returns false
    }

    @Test
    void testFindByRole_ReturnsEmployees() {
        // TODO: Call userRepository.findByRole(Role.EMPLOYEE)
        // TODO: Assert that list contains only employees
        // TODO: Assert that list size is correct
    }

    @Test
    void testSaveUser_Success() {
        // TODO: Create new User
        // TODO: Call userRepository.save()
        // TODO: Assert that user is saved with generated ID
        // TODO: Assert that all fields are persisted correctly
    }

    @Test
    void testUpdateUser_Success() {
        // TODO: Retrieve existing user
        // TODO: Update user fields
        // TODO: Call userRepository.save()
        // TODO: Assert that changes are persisted
    }

    @Test
    void testDeleteUser_Success() {
        // TODO: Get user ID
        // TODO: Call userRepository.deleteById()
        // TODO: Assert that user no longer exists
    }
}
