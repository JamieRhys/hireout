package com.sycosoft.hireout.services.user

import com.sycosoft.hireout.TestStrings
import com.sycosoft.hireout.database.entities.User
import com.sycosoft.hireout.database.entities.UserRole
import com.sycosoft.hireout.database.repositories.UserRepository
import com.sycosoft.hireout.database.repositories.UserRoleRepository
import com.sycosoft.hireout.database.result.ResultCode
import jakarta.persistence.PersistenceException
import org.hibernate.StaleStateException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.dao.*
import java.util.*

@SpringBootTest
@AutoConfigureTestDatabase
@ExtendWith(MockitoExtension::class)
class UserServiceImplTest {
    @Autowired
    private lateinit var userService: UserService

    @MockBean
    private lateinit var  userRepository: UserRepository

    @MockBean
    private lateinit var roleRepository: UserRoleRepository

    private val testUUID = UUID.randomUUID()
    private val adminUUID = UUID.randomUUID()
    private lateinit var user: User
    private lateinit var adminUser: User

    private lateinit var userRoles: List<UserRole>
    private lateinit var testRole: UserRole

    @BeforeEach
    fun setUp() {

        userRoles  = listOf(
            UserRole.Builder().id(1).roleName("ROLE_ADMIN").build(),
            UserRole.Builder().id(2).roleName("ROLE_POWER_USER").build(),
            UserRole.Builder().id(3).roleName("ROLE_USER").build(),
            UserRole.Builder().id(4).roleName("ROLE_ACCOUNTING").build(),
            UserRole.Builder().id(5).roleName("ROLE_READ_ONLY").build()
        )

        testRole = UserRole.Builder().id(999).roleName(TestStrings.TEST_ROLE_NAME).build()

        user = User.Builder()
            .uuid(testUUID)
            .firstName(TestStrings.USER_FIRST_NAME_VALID)
            .lastName(TestStrings.USER_LAST_NAME_VALID)
            .username(TestStrings.USER_USERNAME_VALID)
            .password(TestStrings.USER_PASSWORD_VALID)
            .isDeleted(false)
            .build()

        adminUser = User.Builder()
            .uuid(adminUUID)
            .firstName("Admin")
            .lastName("User")
            .username("admin.user")
            .password("password123")
            .isDeleted(false)
            .build()

        Mockito.`when`(userRepository.findById(adminUUID)).thenReturn(Optional.of(adminUser))
        Mockito.`when`(userRepository.getUserByUsername(adminUser.username!!)).thenReturn(Optional.of(adminUser))

        userRoles.forEach { userRole ->
            Mockito.`when`(roleRepository.findById(userRole.id!!)).thenReturn(Optional.of(userRole))
            Mockito.`when`(roleRepository.getRoleByName(userRole.roleName!!)).thenReturn(Optional.of(userRole))
        }
        Mockito.`when`(roleRepository.findAll()).thenReturn(userRoles.toMutableList())
    }
//region User Tests
//region User Save Method Tests
//region Valid User Object Test
    @Test
    fun givenValidUserObject_whenSaved_thenReturnUser() {
        val test = User.Builder()
            .firstName(TestStrings.USER_FIRST_NAME_VALID)
            .lastName(TestStrings.USER_LAST_NAME_VALID)
            .username(TestStrings.USER_USERNAME_VALID)
            .password(TestStrings.USER_PASSWORD_VALID)
            .isDeleted(false)
            .build()
        Mockito.`when`(userRepository.save(test)).thenReturn(user)

        val savedTest = userService.saveUser(test)

        Mockito.verify(userRepository, Mockito.atLeastOnce()).save(test)
        assertEquals(savedTest.code, ResultCode.CREATION_SUCCESS)
        assertNull(savedTest.errorMessage)
        assertNotNull(savedTest.entity)
        assertEquals(savedTest.entity?.uuid, testUUID)
        assertEquals(savedTest.entity?.firstName, TestStrings.USER_FIRST_NAME_VALID)
        assertEquals(savedTest.entity?.lastName, TestStrings.USER_LAST_NAME_VALID)
        assertEquals(savedTest.entity?.username, TestStrings.USER_USERNAME_VALID)
        assertEquals(savedTest.entity?.password, TestStrings.USER_PASSWORD_VALID)
        assertFalse(savedTest.entity?.isDeleted!!)
    }
//endregion
//region First Name Tests

    @Test
    fun givenNullFirstName_whenSaved_thenReturnFailureResult() {
        val test = User.Builder()
            .lastName(TestStrings.USER_LAST_NAME_VALID)
            .username(TestStrings.USER_USERNAME_VALID)
            .password(TestStrings.USER_PASSWORD_VALID)
            .isDeleted(false)
            .build()

        val savedTest = userService.saveUser(test)

        Mockito.verify(userRepository, Mockito.never()).save(test)
        assertEquals(savedTest.code, ResultCode.CREATION_FAILURE)
        assertNull(savedTest.entity)
        assertNotNull(savedTest.errorMessage)
        assertEquals(savedTest.errorMessage, UserService.ErrorMessages.FIRST_NAME_NULL_OR_BLANK)
    }

    @Test
    fun givenBlankFirstName_whenSaved_thenReturnFailureResult() {
        val test = User.Builder()
            .firstName("")
            .lastName(TestStrings.USER_LAST_NAME_VALID)
            .username(TestStrings.USER_USERNAME_VALID)
            .password(TestStrings.USER_PASSWORD_VALID)
            .isDeleted(false)
            .build()

        val savedTest = userService.saveUser(test)

        Mockito.verify(userRepository, Mockito.never()).save(test)
        assertEquals(savedTest.code, ResultCode.CREATION_FAILURE)
        assertNull(savedTest.entity)
        assertNotNull(savedTest.errorMessage)
        assertEquals(savedTest.errorMessage, UserService.ErrorMessages.FIRST_NAME_NULL_OR_BLANK)
    }

//endregion
//region Last Name Tests

    @Test
    fun givenNullLastName_whenSaved_thenReturnFailureResult() {
        val test = User.Builder()
            .firstName(TestStrings.USER_FIRST_NAME_VALID)
            .username(TestStrings.USER_USERNAME_VALID)
            .password(TestStrings.USER_PASSWORD_VALID)
            .isDeleted(false)
            .build()

        val savedTest = userService.saveUser(test)

        Mockito.verify(userRepository, Mockito.never()).save(test)
        assertEquals(savedTest.code, ResultCode.CREATION_FAILURE)
        assertNull(savedTest.entity)
        assertNotNull(savedTest.errorMessage)
        assertEquals(savedTest.errorMessage, UserService.ErrorMessages.LAST_NAME_NULL_OR_BLANK)
    }

    @Test
    fun givenBlankLastName_whenSaved_thenReturnFailureResult() {
        val test = User.Builder()
            .firstName(TestStrings.USER_FIRST_NAME_VALID)
            .lastName("")
            .username(TestStrings.USER_USERNAME_VALID)
            .password(TestStrings.USER_PASSWORD_VALID)
            .isDeleted(false)
            .build()

        val savedTest = userService.saveUser(test)

        Mockito.verify(userRepository, Mockito.never()).save(test)
        assertEquals(savedTest.code, ResultCode.CREATION_FAILURE)
        assertNull(savedTest.entity)
        assertNotNull(savedTest.errorMessage)
        assertEquals(savedTest.errorMessage, UserService.ErrorMessages.LAST_NAME_NULL_OR_BLANK)
    }

//endregion
//region Username Tests

    @Test
    fun givenNullUsername_whenSaved_thenReturnFailureResult() {
        val test = User.Builder()
            .firstName(TestStrings.USER_FIRST_NAME_VALID)
            .lastName(TestStrings.USER_LAST_NAME_VALID)
            .password(TestStrings.USER_PASSWORD_VALID)
            .isDeleted(false)
            .build()

        val savedTest = userService.saveUser(test)

        Mockito.verify(userRepository, Mockito.never()).save(test)
        assertEquals(savedTest.code, ResultCode.CREATION_FAILURE)
        assertNull(savedTest.entity)
        assertNotNull(savedTest.errorMessage)
        assertEquals(savedTest.errorMessage, UserService.ErrorMessages.USERNAME_NULL_OR_BLANK)
    }

    @Test
    fun givenBlankUsername_whenSaved_thenReturnFailureResult() {
        val test = User.Builder()
            .firstName(TestStrings.USER_FIRST_NAME_VALID)
            .lastName(TestStrings.USER_LAST_NAME_VALID)
            .username("")
            .password(TestStrings.USER_PASSWORD_VALID)
            .isDeleted(false)
            .build()

        val savedTest = userService.saveUser(test)

        Mockito.verify(userRepository, Mockito.never()).save(test)
        assertEquals(savedTest.code, ResultCode.CREATION_FAILURE)
        assertNull(savedTest.entity)
        assertNotNull(savedTest.errorMessage)
        assertEquals(savedTest.errorMessage, UserService.ErrorMessages.USERNAME_NULL_OR_BLANK)
    }

    @Test
    fun givenExistingUsername_whenSaved_thenReturnFailureResult() {
        // Given
        val test = User.Builder()
            .firstName(TestStrings.USER_FIRST_NAME_VALID)
            .lastName(TestStrings.USER_LAST_NAME_VALID)
            .username(TestStrings.USER_USERNAME_VALID)
            .password(TestStrings.USER_PASSWORD_VALID)
            .isDeleted(false)
            .build()
        Mockito.`when`(userRepository.getUserByUsername(test.username!!)).thenReturn(Optional.of(user))

        // When
        val savedTest = userService.saveUser(test)

        // Then
        Mockito.verify(userRepository, Mockito.never()).save(test)  // Verify that the save method is never called again
        assertEquals(ResultCode.CREATION_FAILURE, savedTest.code)
        assertNull(savedTest.entity)
        assertNotNull(savedTest.errorMessage)
        // Adjust the error message verification according to your implementation
        assertEquals(UserService.ErrorMessages.USERNAME_NOT_UNIQUE, savedTest.errorMessage)
    }

//endregion
//region Password Tests

    @Test
    fun givenNullPassword_whenSaved_thenReturnFailureResult() {
        val test = User.Builder()
            .firstName(TestStrings.USER_FIRST_NAME_VALID)
            .lastName(TestStrings.USER_LAST_NAME_VALID)
            .username(TestStrings.USER_USERNAME_VALID)
            .isDeleted(false)
            .build()

        val savedUser = userService.saveUser(test)

        Mockito.verify(userRepository, Mockito.never()).save(test)
        assertEquals(ResultCode.CREATION_FAILURE, savedUser.code)
        assertNull(savedUser.entity)
        assertNotNull(savedUser.errorMessage)
        assertEquals(UserService.ErrorMessages.PASSWORD_NULL_OR_BLANK, savedUser.errorMessage)

    }

    @Test
    fun givenBlankPassword_whenSaved_thenReturnFailureResult() {
        val test = User.Builder()
            .firstName(TestStrings.USER_FIRST_NAME_VALID)
            .lastName(TestStrings.USER_LAST_NAME_VALID)
            .username(TestStrings.USER_USERNAME_VALID)
            .password("")
            .isDeleted(false)
            .build()

        val savedUser = userService.saveUser(test)

        Mockito.verify(userRepository, Mockito.never()).save(test)
        assertEquals(ResultCode.CREATION_FAILURE, savedUser.code)
        assertNull(savedUser.entity)
        assertNotNull(savedUser.errorMessage)
        assertEquals(UserService.ErrorMessages.PASSWORD_NULL_OR_BLANK, savedUser.errorMessage)

    }

//endregion
//endregion
//region User Update Method Tests

    @Test
    fun givenNullUUID_whenUpdating_thenReturnFailureResult() {
        val test = user.copy(uuid = null)

        val savedUser = userService.updateUser(test)

        Mockito.verify(userRepository, Mockito.never()).save(test)
        assertEquals(ResultCode.UPDATE_FAILURE, savedUser.code)
        assertNull(savedUser.entity)
        assertNotNull(savedUser.errorMessage)
        assertEquals(UserService.ErrorMessages.USER_UUID_NULL_OR_BLANK, savedUser.errorMessage)
    }

    @Test
    fun givenInvalidUUID_whenUpdating_thenReturnFailureResult() {
        val test = user.copy(uuid = UUID.randomUUID())

        val savedUser = userService.updateUser(test)
        Mockito.verify(userRepository, Mockito.never()).save(test)
        assertEquals(ResultCode.UPDATE_FAILURE, savedUser.code)
        assertNull(savedUser.entity)
        assertNotNull(savedUser.errorMessage)
        assertEquals(UserService.ErrorMessages.USER_NOT_FOUND_UUID + test.uuid, savedUser.errorMessage)
    }

//region First Name Tests

    @Test
    fun givenValidFirstNameUpdate_whenUpdating_thenReturnSuccessResult() {
        val test = user.copy(firstName = TestStrings.USER_FIRST_NAME_VALID_UPDATE)
        Mockito.`when`(userRepository.findById(test.uuid!!)).thenReturn(Optional.of(user))
        Mockito.`when`(userRepository.save(test)).thenReturn(test)

        val savedUser = userService.updateUser(test)

        Mockito.verify(userRepository, Mockito.atLeastOnce()).save(test)
        assertEquals(ResultCode.UPDATE_SUCCESS, savedUser.code)
        assertNotNull(savedUser.entity)
        assertEquals(savedUser.entity?.uuid, test.uuid)
        assertEquals(savedUser.entity?.firstName, TestStrings.USER_FIRST_NAME_VALID_UPDATE)
        assertEquals(savedUser.entity?.lastName, test.lastName)
        assertEquals(savedUser.entity?.username, test.username)
        assertEquals(savedUser.entity?.password, test.password)
        assertFalse(savedUser.entity?.isDeleted!!)
    }

    @Test
    fun givenFirstNameNothingUpdated_whenUpdating_thenReturnSuccessResult() {
        val test = user.copy()
        Mockito.`when`(userRepository.findById(test.uuid!!)).thenReturn(Optional.of(user))

        val savedUser = userService.updateUser(test)

        Mockito.verify(userRepository, Mockito.never()).save(test)
        assertEquals(ResultCode.UPDATE_FAILURE, savedUser.code)
        assertNull(savedUser.entity)
        assertNotNull(savedUser.errorMessage)
        assertEquals(UserService.ErrorMessages.NOTHING_TO_UPDATE, savedUser.errorMessage)
    }

    @Test
    fun givenFirstNameNull_whenUpdating_thenReturnFailureResult() {
        val test = user.copy(firstName = null)
        Mockito.`when`(userRepository.findById(test.uuid!!)).thenReturn(Optional.of(user))

        val savedUser = userService.updateUser(test)

        Mockito.verify(userRepository, Mockito.never()).save(test)
        assertEquals(ResultCode.UPDATE_FAILURE, savedUser.code)
        assertNull(savedUser.entity)
        assertNotNull(savedUser.errorMessage)
        assertEquals(UserService.ErrorMessages.NOTHING_TO_UPDATE, savedUser.errorMessage)
    }

    @Test
    fun givenFirstNameBlank_whenUpdating_thenReturnFailureResult() {
        val test = user.copy(firstName = "")
        Mockito.`when`(userRepository.findById(test.uuid!!)).thenReturn(Optional.of(user))

        val savedUser = userService.updateUser(test)

        Mockito.verify(userRepository, Mockito.never()).save(test)
        assertEquals(ResultCode.UPDATE_FAILURE, savedUser.code)
        assertNull(savedUser.entity)
        assertNotNull(savedUser.errorMessage)
        assertEquals(UserService.ErrorMessages.NOTHING_TO_UPDATE, savedUser.errorMessage)
    }

//endregion
//region Last Name Tests

    @Test
    fun givenValidLastNameUpdate_whenUpdating_thenReturnSuccessResult() {
        val test = user.copy(lastName = TestStrings.USER_LAST_NAME_VALID_UPDATE)
        Mockito.`when`(userRepository.findById(test.uuid!!)).thenReturn(Optional.of(user))
        Mockito.`when`(userRepository.save(test)).thenReturn(test)

        val savedUser = userService.updateUser(test)

        Mockito.verify(userRepository, Mockito.atLeastOnce()).save(test)
        assertEquals(ResultCode.UPDATE_SUCCESS, savedUser.code)
        assertNotNull(savedUser.entity)
        assertEquals(savedUser.entity?.uuid, test.uuid)
        assertEquals(savedUser.entity?.firstName, test.firstName)
        assertEquals(savedUser.entity?.lastName, TestStrings.USER_LAST_NAME_VALID_UPDATE)
        assertEquals(savedUser.entity?.username, test.username)
        assertEquals(savedUser.entity?.password, test.password)
        assertFalse(savedUser.entity?.isDeleted!!)
    }

    @Test
    fun givenLastNameNothingUpdated_whenUpdating_thenReturnSuccessResult() {
        val test = user.copy()
        Mockito.`when`(userRepository.findById(test.uuid!!)).thenReturn(Optional.of(user))

        val savedUser = userService.updateUser(test)

        Mockito.verify(userRepository, Mockito.never()).save(test)
        assertEquals(ResultCode.UPDATE_FAILURE, savedUser.code)
        assertNull(savedUser.entity)
        assertNotNull(savedUser.errorMessage)
        assertEquals(UserService.ErrorMessages.NOTHING_TO_UPDATE, savedUser.errorMessage)
    }

    @Test
    fun givenLastNameNull_whenUpdating_thenReturnFailureResult() {
        val test = user.copy(lastName = null)
        Mockito.`when`(userRepository.findById(test.uuid!!)).thenReturn(Optional.of(user))

        val savedUser = userService.updateUser(test)

        Mockito.verify(userRepository, Mockito.never()).save(test)
        assertEquals(ResultCode.UPDATE_FAILURE, savedUser.code)
        assertNull(savedUser.entity)
        assertNotNull(savedUser.errorMessage)
        assertEquals(UserService.ErrorMessages.NOTHING_TO_UPDATE, savedUser.errorMessage)
    }

    @Test
    fun givenLastNameBlank_whenUpdating_thenReturnFailureResult() {
        val test = user.copy(lastName = "")
        Mockito.`when`(userRepository.findById(test.uuid!!)).thenReturn(Optional.of(user))

        val savedUser = userService.updateUser(test)

        Mockito.verify(userRepository, Mockito.never()).save(test)
        assertEquals(ResultCode.UPDATE_FAILURE, savedUser.code)
        assertNull(savedUser.entity)
        assertNotNull(savedUser.errorMessage)
        assertEquals(UserService.ErrorMessages.NOTHING_TO_UPDATE, savedUser.errorMessage)
    }

//endregion
//region Username Tests

    @Test
    fun givenValidUsernameUpdate_whenUpdating_thenReturnFailureResult() {
        val test = user.copy(username = TestStrings.USER_USERNAME_VALID_UPDATE)
        Mockito.`when`(userRepository.findById(test.uuid!!)).thenReturn(Optional.of(user))

        val savedUser = userService.updateUser(test)

        Mockito.verify(userRepository, Mockito.never()).save(test)
        assertEquals(ResultCode.UPDATE_FAILURE, savedUser.code)
        assertNull(savedUser.entity)
        assertNotNull(savedUser.errorMessage)
        assertEquals(UserService.ErrorMessages.USER_USERNAME_CANNOT_BE_CHANGED, savedUser.errorMessage)
    }

    @Test
    fun givenUsernameNothingUpdated_whenUpdating_thenReturnFailureResult() {
        val test = user.copy()
        Mockito.`when`(userRepository.findById(test.uuid!!)).thenReturn(Optional.of(user))

        val savedUser = userService.updateUser(test)

        Mockito.verify(userRepository, Mockito.never()).save(test)
        assertEquals(ResultCode.UPDATE_FAILURE, savedUser.code)
        assertNull(savedUser.entity)
        assertNotNull(savedUser.errorMessage)
        assertEquals(UserService.ErrorMessages.NOTHING_TO_UPDATE, savedUser.errorMessage)
    }

    @Test
    fun givenUsernameNull_whenUpdating_thenReturnFailureResult() {
        val test = user.copy(username = null)
        Mockito.`when`(userRepository.findById(test.uuid!!)).thenReturn(Optional.of(user))

        val savedUser = userService.updateUser(test)

        Mockito.verify(userRepository, Mockito.never()).save(test)
        assertEquals(ResultCode.UPDATE_FAILURE, savedUser.code)
        assertNull(savedUser.entity)
        assertNotNull(savedUser.errorMessage)
        assertEquals(UserService.ErrorMessages.NOTHING_TO_UPDATE, savedUser.errorMessage)
    }

    @Test
    fun givenUsernameBlank_whenUpdating_thenReturnFailureResult() {
        val test = user.copy(username = "")
        Mockito.`when`(userRepository.findById(test.uuid!!)).thenReturn(Optional.of(user))

        val savedUser = userService.updateUser(test)

        Mockito.verify(userRepository, Mockito.never()).save(test)
        assertEquals(ResultCode.UPDATE_FAILURE, savedUser.code)
        assertNull(savedUser.entity)
        assertNotNull(savedUser.errorMessage)
        assertEquals(UserService.ErrorMessages.NOTHING_TO_UPDATE, savedUser.errorMessage)
    }

//endregion
//region Password Tests

    @Test
    fun givenValidPasswordUpdate_whenUpdating_thenReturnSuccessResult() {
        val test = user.copy(password = TestStrings.USER_PASSWORD_VALID_UPDATE)
        Mockito.`when`(userRepository.findById(test.uuid!!)).thenReturn(Optional.of(user))
        Mockito.`when`(userRepository.save(test)).thenReturn(test)

        val savedUser = userService.updateUser(test)

        Mockito.verify(userRepository, Mockito.atLeastOnce()).save(test)
        assertEquals(ResultCode.UPDATE_SUCCESS, savedUser.code)
        assertNotNull(savedUser.entity)
        assertEquals(savedUser.entity?.uuid, test.uuid)
        assertEquals(savedUser.entity?.firstName, test.firstName)
        assertEquals(savedUser.entity?.lastName, test.lastName)
        assertEquals(savedUser.entity?.username, test.username)
        assertEquals(savedUser.entity?.password, TestStrings.USER_PASSWORD_VALID_UPDATE)
        assertFalse(savedUser.entity?.isDeleted!!)
    }

    @Test
    fun givenPasswordNothingUpdated_whenUpdating_thenReturnSuccessResult() {
        val test = user.copy()
        Mockito.`when`(userRepository.findById(test.uuid!!)).thenReturn(Optional.of(user))

        val savedUser = userService.updateUser(test)

        Mockito.verify(userRepository, Mockito.never()).save(test)
        assertEquals(ResultCode.UPDATE_FAILURE, savedUser.code)
        assertNull(savedUser.entity)
        assertNotNull(savedUser.errorMessage)
        assertEquals(UserService.ErrorMessages.NOTHING_TO_UPDATE, savedUser.errorMessage)
    }

    @Test
    fun givenPasswordNull_whenUpdating_thenReturnFailureResult() {
        val test = user.copy(password = null)
        Mockito.`when`(userRepository.findById(test.uuid!!)).thenReturn(Optional.of(user))

        val savedUser = userService.updateUser(test)

        Mockito.verify(userRepository, Mockito.never()).save(test)
        assertEquals(ResultCode.UPDATE_FAILURE, savedUser.code)
        assertNull(savedUser.entity)
        assertNotNull(savedUser.errorMessage)
        assertEquals(UserService.ErrorMessages.NOTHING_TO_UPDATE, savedUser.errorMessage)
    }

    @Test
    fun givenPasswordBlank_whenUpdating_thenReturnFailureResult() {
        val test = user.copy(password = "")
        Mockito.`when`(userRepository.findById(test.uuid!!)).thenReturn(Optional.of(user))

        val savedUser = userService.updateUser(test)

        Mockito.verify(userRepository, Mockito.never()).save(test)
        assertEquals(ResultCode.UPDATE_FAILURE, savedUser.code)
        assertNull(savedUser.entity)
        assertNotNull(savedUser.errorMessage)
        assertEquals(UserService.ErrorMessages.NOTHING_TO_UPDATE, savedUser.errorMessage)
    }

//endregion
//region Exception Tests

    @Test
    fun givenDataAccessExceptionThrown_whenUpdating_thenReturnFailureResult() {
        val test = user.copy(firstName = TestStrings.USER_FIRST_NAME_VALID_UPDATE)
        Mockito.`when`(userRepository.findById(test.uuid!!)).thenReturn(Optional.of(user))
        Mockito.`when`(userRepository.save(test)).thenThrow(DataIntegrityViolationException("Test Exception Triggered"))

        val savedUser = userService.updateUser(test)
        Mockito.verify(userRepository, Mockito.atLeastOnce()).save(test)
        assertEquals(ResultCode.UPDATE_FAILURE, savedUser.code)
        assertNull(savedUser.entity)
        assertNotNull(savedUser.errorMessage)
        assertEquals(savedUser.errorMessage, "Test Exception Triggered")
    }

    @Test
    fun givenOptimisticLockingFailureException_whenUpdating_thenReturnFailureResult() {
        val test = user.copy(firstName = TestStrings.USER_FIRST_NAME_VALID_UPDATE)
        Mockito.`when`(userRepository.findById(test.uuid!!)).thenReturn(Optional.of(user))
        Mockito.`when`(userRepository.save(test)).thenThrow(OptimisticLockingFailureException("Test Exception Triggered"))

        val savedUser = userService.updateUser(test)
        Mockito.verify(userRepository, Mockito.atLeastOnce()).save(test)
        assertEquals(ResultCode.UPDATE_FAILURE, savedUser.code)
        assertNull(savedUser.entity)
        assertNotNull(savedUser.errorMessage)
        assertEquals(savedUser.errorMessage, "Test Exception Triggered")
    }

    @Test
    fun givenStaleStateException_whenUpdating_thenReturnFailureResult() {
        val test = user.copy(firstName = TestStrings.USER_FIRST_NAME_VALID_UPDATE)
        Mockito.`when`(userRepository.findById(test.uuid!!)).thenReturn(Optional.of(user))
        Mockito.`when`(userRepository.save(test)).thenThrow(StaleStateException("Test Exception Triggered"))

        val savedUser = userService.updateUser(test)
        Mockito.verify(userRepository, Mockito.atLeastOnce()).save(test)
        assertEquals(ResultCode.UPDATE_FAILURE, savedUser.code)
        assertNull(savedUser.entity)
        assertNotNull(savedUser.errorMessage)
        assertEquals(savedUser.errorMessage, "Test Exception Triggered")
    }

    @Test
    fun givenPersistenceException_whenUpdating_thenReturnFailureResult() {
        val test = user.copy(firstName = TestStrings.USER_FIRST_NAME_VALID_UPDATE)
        Mockito.`when`(userRepository.findById(test.uuid!!)).thenReturn(Optional.of(user))
        Mockito.`when`(userRepository.save(test)).thenThrow(PersistenceException("Test Exception Triggered"))

        val savedUser = userService.updateUser(test)
        Mockito.verify(userRepository, Mockito.atLeastOnce()).save(test)
        assertEquals(ResultCode.UPDATE_FAILURE, savedUser.code)
        assertNull(savedUser.entity)
        assertNotNull(savedUser.errorMessage)
        assertEquals(savedUser.errorMessage, "Test Exception Triggered")
    }

//endregion
//endregion
//region User Get Method Tests
//region UUID

    @Test
    fun givenValidUserUUID_whenGetting_thenProvideSuccessResultAndObject() {
        Mockito.`when`(userRepository.findById(user.uuid!!)).thenReturn(Optional.of(user))

        val found = userService.getUser(user.uuid!!)

        Mockito.verify(userRepository, Mockito.atLeastOnce()).findById(user.uuid!!)
        assertEquals(found.code, ResultCode.FETCH_SUCCESS)
        assertNull(found.errorMessage)
        assertNotNull(found.entity)
        assertEquals(found.entity?.uuid, user.uuid)
        assertEquals(found.entity?.firstName, user.firstName)
        assertEquals(found.entity?.lastName, user.lastName)
        assertEquals(found.entity?.username, user.username)
        assertEquals(found.entity?.password, user.password)
        assertFalse(found.entity?.isDeleted!!)
    }

    @Test
    fun givenInvalidUserUUID_whenGetting_thenProvideFailureResult() {
        val testUUID = UUID.randomUUID()
        val found = userService.getUser(testUUID)

        Mockito.verify(userRepository, Mockito.atLeastOnce()).findById(testUUID)
        assertEquals(found.code, ResultCode.FETCH_FAILURE)
        assertNull(found.entity)
        assertNotNull(found.errorMessage)
        assertEquals(found.errorMessage, UserService.ErrorMessages.USER_NOT_FOUND_UUID + testUUID)
    }

//region Exception Tests

    @Test
    fun givenDataAccessException_whenGetting_thenProvideFailureResult() {
        Mockito.`when`(userRepository.findById(user.uuid!!)).thenThrow(EmptyResultDataAccessException(1))

        val found = userService.getUser(user.uuid!!)

        Mockito.verify(userRepository, Mockito.atLeastOnce()).findById(user.uuid!!)
        assertEquals(found.code, ResultCode.FETCH_FAILURE)
        assertNull(found.entity)
        assertNotNull(found.errorMessage)
        assertEquals(found.errorMessage, "Incorrect result size: expected 1, actual 0")
    }

//endregion
//endregion
//region Name

    @Test
    fun givenValidUserUsername_whenGetting_thenProvideSuccessResultAndObject() {
        Mockito.`when`(userRepository.getUserByUsername(user.username!!)).thenReturn(Optional.of(user))

        val found = userService.getUser(user.username!!)

        Mockito.verify(userRepository, Mockito.atLeastOnce()).getUserByUsername(user.username!!)
        assertEquals(found.code, ResultCode.FETCH_SUCCESS)
        assertNull(found.errorMessage)
        assertNotNull(found.entity)
        assertEquals(found.entity?.uuid, user.uuid)
        assertEquals(found.entity?.firstName, user.firstName)
        assertEquals(found.entity?.lastName, user.lastName)
        assertEquals(found.entity?.username, user.username)
        assertEquals(found.entity?.password, user.password)
        assertFalse(found.entity?.isDeleted!!)
    }

    @Test
    fun givenInvalidUserUsername_whenGetting_thenProvideFailureResult() {
        val found = userService.getUser(TestStrings.USER_USERNAME_INVALID)

        Mockito.verify(userRepository, Mockito.atLeastOnce()).getUserByUsername(TestStrings.USER_USERNAME_INVALID)
        assertEquals(found.code, ResultCode.FETCH_FAILURE)
        assertNull(found.entity)
        assertNotNull(found.errorMessage)
        assertEquals(found.errorMessage, UserService.ErrorMessages.USER_NOT_FOUND_USERNAME + TestStrings.USER_USERNAME_INVALID)
    }

//region Exception Tests

    @Test
    fun givenEmptyResultDataAccessException_whenGetting_thenProvideFailureResult() {
        Mockito.`when`(userRepository.getUserByUsername(user.username!!)).thenThrow(EmptyResultDataAccessException(1))

        val found = userService.getUser(user.username!!)

        Mockito.verify(userRepository, Mockito.atLeastOnce()).getUserByUsername(user.username!!)
        assertEquals(found.code, ResultCode.FETCH_FAILURE)
        assertNull(found.entity)
        assertNotNull(found.errorMessage)
        assertEquals(found.errorMessage, "Incorrect result size: expected 1, actual 0")
    }

    @Test
    fun givenIncorrectResultSizeDataAccessException_whenGetting_thenProvideFailureResult() {
        Mockito.`when`(userRepository.getUserByUsername(user.username!!)).thenThrow(IncorrectResultSizeDataAccessException(1))

        val found = userService.getUser(user.username!!)

        Mockito.verify(userRepository, Mockito.atLeastOnce()).getUserByUsername(user.username!!)
        assertEquals(found.code, ResultCode.FETCH_FAILURE)
        assertNull(found.entity)
        assertNotNull(found.errorMessage)
        assertEquals(found.errorMessage, "Incorrect result size: expected 1")
    }

    @Test
    fun givenDataRetrievalFailureException_whenGetting_thenProvideFailureResult() {
        Mockito.`when`(userRepository.getUserByUsername(user.username!!)).thenThrow(DataRetrievalFailureException("Test Exception Triggered"))

        val found = userService.getUser(user.username!!)

        Mockito.verify(userRepository, Mockito.atLeastOnce()).getUserByUsername(user.username!!)
        assertEquals(found.code, ResultCode.FETCH_FAILURE)
        assertNull(found.entity)
        assertNotNull(found.errorMessage)
        assertEquals(found.errorMessage, "Test Exception Triggered")
    }

//endregion
//endregion
//endregion
//region Delete Method Tests
//region UUID

    @Test
    fun givenValidUserUUID_whenDeleting_thenProvideSuccessfulResult() {
        Mockito.`when`(userRepository.findById(user.uuid!!)).thenReturn(Optional.of(user))

        val deletedUser = userService.deleteUser(user.uuid!!)

        Mockito.verify(userRepository, Mockito.atLeastOnce()).deleteById(user.uuid!!)
        assertEquals(deletedUser.code, ResultCode.DELETION_SUCCESS)
        assertNotNull(deletedUser.entity)
        assertTrue(deletedUser.entity!!)
        assertNull(deletedUser.errorMessage)
    }

    @Test
    fun givenInvalidUserUUID_whenDeleting_thenProvideFailureResult() {
        val testUUID = UUID.randomUUID()

        val deletedUser = userService.deleteUser(testUUID)

        Mockito.verify(userRepository, Mockito.never()).deleteById(testUUID)
        assertEquals(deletedUser.code, ResultCode.DELETION_FAILURE)
        assertNull(deletedUser.entity)
        assertNotNull(deletedUser.errorMessage)
        assertEquals(deletedUser.errorMessage, UserService.ErrorMessages.USER_NOT_FOUND_UUID + testUUID)
    }

    @Test
    fun givenAdminUserUUID_whenDeleting_thenProvideFailureResult() {
        val deletedUser = userService.deleteUser(adminUser.uuid!!)

        Mockito.verify(userRepository, Mockito.never()).deleteById(adminUser.uuid!!)
        assertEquals(deletedUser.code, ResultCode.DELETION_FAILURE)
        assertNull(deletedUser.entity)
        assertNotNull(deletedUser.errorMessage)
        assertEquals(deletedUser.errorMessage, UserService.ErrorMessages.CANNOT_DELETE_ADMIN_USER)
    }

//endregion
//region Username

    @Test
    fun givenValidUserUsername_whenDeleting_thenProvideSuccessfulResult() {
        Mockito.`when`(userRepository.getUserByUsername(user.username!!)).thenReturn(Optional.of(user))
        Mockito.`when`(userRepository.findById(user.uuid!!)).thenReturn(Optional.of(user))

        val deletedUser = userService.deleteUser(user.username!!)

        Mockito.verify(userRepository, Mockito.atLeastOnce()).deleteById(user.uuid!!)
        assertEquals(deletedUser.code, ResultCode.DELETION_SUCCESS)
        assertNotNull(deletedUser.entity)
        assertTrue(deletedUser.entity!!)
        assertNull(deletedUser.errorMessage)
    }

    @Test
    fun givenInvalidUserUsername_whenDeleting_thenProvideFailureResult() {

        val deletedUser = userService.deleteUser(TestStrings.USER_USERNAME_INVALID)

        Mockito.verify(userRepository, Mockito.never()).deleteById(testUUID)
        assertEquals(deletedUser.code, ResultCode.DELETION_FAILURE)
        assertNull(deletedUser.entity)
        assertNotNull(deletedUser.errorMessage)
        assertEquals(deletedUser.errorMessage, UserService.ErrorMessages.USER_NOT_FOUND_USERNAME + TestStrings.USER_USERNAME_INVALID)
    }

    @Test
    fun givenAdminUserUsername_whenDeleting_thenProvideFailureResult() {
        val deletedUser = userService.deleteUser(adminUser.username!!)

        Mockito.verify(userRepository, Mockito.never()).deleteById(adminUser.uuid!!)
        assertEquals(deletedUser.code, ResultCode.DELETION_FAILURE)
        assertNull(deletedUser.entity)
        assertNotNull(deletedUser.errorMessage)
        assertEquals(deletedUser.errorMessage, UserService.ErrorMessages.CANNOT_DELETE_ADMIN_USER)
    }

//endregion
//endregion
//endregion
//region User Role Tests
    //region Save Method Tests
        //region Single Role Methods

    @Test
    fun givenValidUserRoleObject_whenSavingUserRole_thenProvideSuccessResultAndObject() {
        val role = UserRole.Builder().roleName(TestStrings.TEST_ROLE_NAME).build()
        Mockito.`when`(roleRepository.save(role)).thenReturn(testRole)
        val savedRole = userService.saveUserRole(role)

        Mockito.verify(roleRepository, Mockito.atLeastOnce()).save(role)
        assertEquals(savedRole.code, ResultCode.CREATION_SUCCESS)
        assertNull(savedRole.errorMessage)
        assertNotNull(savedRole.entity)
        assertEquals(savedRole.entity?.id, testRole.id)
        assertEquals(savedRole.entity?.roleName, testRole.roleName)
    }

    @Test
    fun givenNullNameInUserRoleObject_whenSavingUserRole_thenProvideFailureResult() {
        val role = UserRole.Builder().roleName(null).build()
        val savedRole = userService.saveUserRole(role)

        Mockito.verify(roleRepository, Mockito.never()).save(role)
        assertEquals(savedRole.code, ResultCode.CREATION_FAILURE)
        assertNull(savedRole.entity)
        assertNotNull(savedRole.errorMessage)
        assertEquals(savedRole.errorMessage, UserService.ErrorMessages.ROLE_NAME_NULL_OR_BLANK)
    }

    @Test
    fun givenBlankNameInUserRoleObject_whenSavingUserRole_thenProvideFailureResult() {
        val role = UserRole.Builder().roleName("").build()
        val savedRole = userService.saveUserRole(role)

        Mockito.verify(roleRepository, Mockito.never()).save(role)
        assertEquals(savedRole.code, ResultCode.CREATION_FAILURE)
        assertNull(savedRole.entity)
        assertNotNull(savedRole.errorMessage)
        assertEquals(savedRole.errorMessage, UserService.ErrorMessages.ROLE_NAME_NULL_OR_BLANK)
    }

    @Test
    fun givenExistingNameInUserRoleObject_whenSavingUserRole_thenProvideFailureResult() {
        val test = UserRole(roleName = userRoles[0].roleName)
        val savedRole = userService.saveUserRole(test)

        Mockito.verify(roleRepository, Mockito.never()).save(test)
        assertEquals(savedRole.code, ResultCode.CREATION_FAILURE)
        assertNull(savedRole.entity)
        assertNotNull(savedRole.errorMessage)
        assertEquals(savedRole.errorMessage, UserService.ErrorMessages.ROLE_NOT_UNIQUE)
    }

        //region Exception Methods
// -------------------------------------------------------------------------

    @Test
    fun givenDataIntegrityViolationException_whenSavingUserRole_thenProvideFailureResult() {
        val test = UserRole(roleName = TestStrings.TEST_ROLE_NAME)
        Mockito.`when`(roleRepository.save(test)).thenThrow(DataIntegrityViolationException("Test Exception Triggered"))

        val savedRole = userService.saveUserRole(test)

        Mockito.verify(roleRepository,Mockito.atLeastOnce()).save(test)
        assertEquals(savedRole.code, ResultCode.CREATION_FAILURE)
        assertNull(savedRole.entity)
        assertNotNull(savedRole.errorMessage)
        assertEquals(savedRole.errorMessage, "Test Exception Triggered")
    }

    @Test
    fun givenConcurrencyFailureException_whenSavingUserRole_thenProvideFailureResult() {
        val test = UserRole(roleName = TestStrings.TEST_ROLE_NAME)
        Mockito.`when`(roleRepository.save(test)).thenThrow(ConcurrencyFailureException("Test Exception Triggered"))

        val savedRole = userService.saveUserRole(test)

        Mockito.verify(roleRepository,Mockito.atLeastOnce()).save(test)
        assertEquals(savedRole.code, ResultCode.CREATION_FAILURE)
        assertNull(savedRole.entity)
        assertNotNull(savedRole.errorMessage)
        assertEquals(savedRole.errorMessage, "Test Exception Triggered")
    }

    @Test
    fun givenOptimisticLockingFailureException_whenSavingUserRole_thenProvideFailureResult() {
        val test = UserRole(roleName = TestStrings.TEST_ROLE_NAME)
        Mockito.`when`(roleRepository.save(test)).thenThrow(OptimisticLockingFailureException("Test Exception Triggered"))

        val savedRole = userService.saveUserRole(test)

        Mockito.verify(roleRepository,Mockito.atLeastOnce()).save(test)
        assertEquals(savedRole.code, ResultCode.CREATION_FAILURE)
        assertNull(savedRole.entity)
        assertNotNull(savedRole.errorMessage)
        assertEquals(savedRole.errorMessage, "Test Exception Triggered")
    }

        //endregion

        //endregion
        //region Single Role Name Methods
// -------------------------------------------------------------------------

    @Test
    fun givenValidRoleNameInUserRoleObject_whenSavingUserRole_thenProvideSuccessResultAndObject() {
        Mockito.`when`(roleRepository.save(UserRole(roleName = TestStrings.TEST_ROLE_NAME))).thenReturn(testRole)
        val savedRole = userService.saveUserRole(TestStrings.TEST_ROLE_NAME)

        Mockito.verify(roleRepository, Mockito.atLeastOnce()).save(UserRole(roleName = TestStrings.TEST_ROLE_NAME))
        assertEquals(savedRole.code, ResultCode.CREATION_SUCCESS)
        assertNull(savedRole.errorMessage)
        assertNotNull(savedRole.entity)
        assertEquals(savedRole.entity?.id, testRole.id)
        assertEquals(savedRole.entity?.roleName, testRole.roleName)
    }

    @Test
    fun givenNullRoleNameInUserRoleObject_whenSavingUserRole_thenProvideFailureResult() {
        val savedRole = userService.saveUserRole(UserRole(roleName = null))

        Mockito.verify(roleRepository, Mockito.never()).save(UserRole(roleName = null))
        assertEquals(savedRole.code, ResultCode.CREATION_FAILURE)
        assertNull(savedRole.entity)
        assertNotNull(savedRole.errorMessage)
        assertEquals(savedRole.errorMessage, UserService.ErrorMessages.ROLE_NAME_NULL_OR_BLANK)
    }

    @Test
    fun givenBlankRoleNameInUserRoleObject_whenSavingUserRole_thenProvideFailureResult() {
        val savedRole = userService.saveUserRole(UserRole(roleName = ""))

        Mockito.verify(roleRepository, Mockito.never()).save(UserRole(roleName = ""))
        assertEquals(savedRole.code, ResultCode.CREATION_FAILURE)
        assertNull(savedRole.entity)
        assertNotNull(savedRole.errorMessage)
        assertEquals(savedRole.errorMessage, UserService.ErrorMessages.ROLE_NAME_NULL_OR_BLANK)
    }

        //endregion
        //region Multi Role Methods
// -------------------------------------------------------------------------

    @Test
    fun givenMultipleValidUserRoleObjects_whenSavingUserRoles_thenProvideSuccessResultAndObjects() {
        val roles: List<UserRole> = listOf(
            UserRole(roleName = TestStrings.TEST_ROLE_NAME),
            UserRole(roleName = TestStrings.TEST_ROLE_NAME2),
            UserRole(roleName = TestStrings.TEST_ROLE_NAME3),
            UserRole(roleName = TestStrings.TEST_ROLE_NAME4)
        )

        roles.forEachIndexed { index, role ->
            Mockito.`when`(roleRepository.save(role)).thenReturn(UserRole(id = index + 1, role.roleName))
        }

        val savedRoles = userService.saveUserRoles(roles)

        savedRoles.forEachIndexed { index, role ->
            Mockito.verify(roleRepository, Mockito.atLeastOnce()).save(roles[index])
            assertEquals(role.code, ResultCode.CREATION_SUCCESS)
            assertNull(role.errorMessage)
            assertNotNull(role.entity)
            assertEquals(role.entity?.id, index + 1)
            assertEquals(role.entity?.roleName, roles[index].roleName)
        }
    }

    @Test
    fun givenEmptyListOfUserRoles_whenSavingUserRoles_thenProvideFailureResult() {
        val roles: List<UserRole> = listOf()

        val savedRoles = userService.saveUserRoles(roles)

        Mockito.verify(roleRepository, Mockito.never()).save(UserRole(roleName = ""))
        assertEquals(savedRoles[0].code, ResultCode.CREATION_FAILURE)
        assertNull(savedRoles[0].entity)
        assertNotNull(savedRoles[0].errorMessage)
        assertEquals(savedRoles[0].errorMessage, UserService.ErrorMessages.NO_ROLES_TO_SAVE_IN_LIST)
    }

    @Test
    fun givenMultipleNullRoleNamesInUserRoles_whenSavingUserRoles_thenProvideFailureResult() {
        val roles: List<UserRole> = listOf(
            UserRole(roleName = null),
            UserRole(roleName = null),
            UserRole(roleName = null),
            UserRole(roleName = null)
        )

        roles.forEachIndexed { index, role ->
            Mockito.`when`(roleRepository.save(role)).thenReturn(UserRole(id = index + 1, role.roleName))
        }

        val savedRoles = userService.saveUserRoles(roles)

        savedRoles.forEachIndexed { index, role ->
            Mockito.verify(roleRepository, Mockito.never()).save(roles[index])
            assertEquals(role.code, ResultCode.CREATION_FAILURE)
            assertNull(role.entity)
            assertNotNull(role.errorMessage)
            assertEquals(role.errorMessage, UserService.ErrorMessages.ROLE_NAME_NULL_OR_BLANK)
        }
    }

    @Test
    fun givenMultipleBlankRoleNamesInUserRoles_whenSavingUserRoles_thenProvideFailureResult() {
        val roles: List<UserRole> = listOf(
            UserRole(roleName = ""),
            UserRole(roleName = ""),
            UserRole(roleName = ""),
            UserRole(roleName = "")
        )

        roles.forEachIndexed { index, role ->
            Mockito.`when`(roleRepository.save(role)).thenReturn(UserRole(id = index + 1, role.roleName))
        }

        val savedRoles = userService.saveUserRoles(roles)

        savedRoles.forEachIndexed { index, role ->
            Mockito.verify(roleRepository, Mockito.never()).save(roles[index])
            assertEquals(role.code, ResultCode.CREATION_FAILURE)
            assertNull(role.entity)
            assertNotNull(role.errorMessage)
            assertEquals(role.errorMessage, UserService.ErrorMessages.ROLE_NAME_NULL_OR_BLANK)
        }
    }

    @Test
    fun givenTakenUniqueRoleNameInUserRoles_whenSavingUserRoles_thenProvideFailureResult() {
        val roles = listOf(
            UserRole(roleName = TestStrings.TEST_ROLE_NAME),
            UserRole(roleName = userRoles[0].roleName)
        )

        Mockito.`when`(roleRepository.save(roles[0])).thenReturn(testRole)

        val savedRoles = userService.saveUserRoles(roles)

        Mockito.verify(roleRepository, Mockito.atLeastOnce()).save(roles[0])
        assertEquals(savedRoles[0].code, ResultCode.CREATION_SUCCESS)
        assertNull(savedRoles[0].errorMessage)
        assertNotNull(savedRoles[0].entity)
        assertEquals(savedRoles[0].entity?.id, testRole.id)
        assertEquals(savedRoles[0].entity?.roleName, testRole.roleName)

        Mockito.verify(roleRepository, Mockito.never()).save(roles[1])
        assertEquals(savedRoles[1].code, ResultCode.CREATION_FAILURE)
        assertNull(savedRoles[1].entity)
        assertNotNull(savedRoles[1].errorMessage)
        assertEquals(savedRoles[1].errorMessage, UserService.ErrorMessages.ROLE_NOT_UNIQUE)
    }

        //endregion
        //region Multi Role Name Methods
// -------------------------------------------------------------------------

    @Test
    fun givenMultipleValidRoleNames_whenSavingUserRoles_thenProvideSuccessResultAndObjects() {
        val roles: List<String> = listOf(
            TestStrings.TEST_ROLE_NAME,
            TestStrings.TEST_ROLE_NAME2,
            TestStrings.TEST_ROLE_NAME3,
            TestStrings.TEST_ROLE_NAME4
        )

        roles.forEachIndexed { index, role ->
            Mockito.`when`(roleRepository.save(UserRole(roleName = role))).thenReturn(UserRole(id = index + 1, roleName = role))
        }

        val savedRoles = userService.saveUserRolesByName(roles)

        savedRoles.forEachIndexed { index, role ->
            Mockito.verify(roleRepository, Mockito.atLeastOnce()).save(UserRole(roleName = roles[index]))
            assertEquals(role.code, ResultCode.CREATION_SUCCESS)
            assertNull(role.errorMessage)
            assertNotNull(role.entity)
            assertEquals(role.entity?.id, index + 1)
            assertEquals(role.entity?.roleName, roles[index])
        }
    }

    @Test
    fun givenEmptyListOfUserRoleNames_whenSavingUserRoles_thenProvideFailureResult() {
        val roles: List<String> = listOf()

        val savedRoles = userService.saveUserRolesByName(roles)

        Mockito.verify(roleRepository, Mockito.never()).save(UserRole(roleName = ""))
        assertEquals(savedRoles[0].code, ResultCode.CREATION_FAILURE)
        assertNull(savedRoles[0].entity)
        assertNotNull(savedRoles[0].errorMessage)
        assertEquals(savedRoles[0].errorMessage, UserService.ErrorMessages.NO_ROLES_TO_SAVE_IN_LIST)
    }

    @Test
    fun givenMultipleBlankRoleNames_whenSavingUserRoles_thenProvideFailureResult() {
        val roles: List<String> = listOf(
            "",
            "",
            "",
            ""
        )

        roles.forEachIndexed { index, role ->
            Mockito.`when`(roleRepository.save(UserRole(roleName = role))).thenReturn(UserRole(id = index + 1, role))
        }

        val savedRoles = userService.saveUserRolesByName(roles)

        savedRoles.forEachIndexed { index, role ->
            Mockito.verify(roleRepository, Mockito.never()).save(UserRole(roleName = roles[index]))
            assertEquals(role.code, ResultCode.CREATION_FAILURE)
            assertNull(role.entity)
            assertNotNull(role.errorMessage)
            assertEquals(role.errorMessage, UserService.ErrorMessages.ROLE_NAME_NULL_OR_BLANK)
        }
    }

    @Test
    fun givenTakenUniqueRoleName_whenSavingUserRoles_thenProvideFailureResult() {
        val roles = listOf(
            TestStrings.TEST_ROLE_NAME,
            userRoles[0].roleName!!
        )

        Mockito.`when`(roleRepository.save(UserRole(roleName = roles[0]))).thenReturn(testRole)

        val savedRoles = userService.saveUserRolesByName(roles = roles)

        Mockito.verify(roleRepository, Mockito.atLeastOnce()).save(UserRole(roleName = roles[0]))
        assertEquals(savedRoles[0].code, ResultCode.CREATION_SUCCESS)
        assertNull(savedRoles[0].errorMessage)
        assertNotNull(savedRoles[0].entity)
        assertEquals(savedRoles[0].entity?.id, testRole.id)
        assertEquals(savedRoles[0].entity?.roleName, testRole.roleName)

        Mockito.verify(roleRepository, Mockito.never()).save(UserRole(roleName = roles[1]))
        assertEquals(savedRoles[1].code, ResultCode.CREATION_FAILURE)
        assertNull(savedRoles[1].entity)
        assertNotNull(savedRoles[1].errorMessage)
        assertEquals(savedRoles[1].errorMessage, UserService.ErrorMessages.ROLE_NOT_UNIQUE)
    }

    //endregion
    //endregion
//region Get Method Tests
// -------------------------------------------------------------------------


//endregion
//endregion
}