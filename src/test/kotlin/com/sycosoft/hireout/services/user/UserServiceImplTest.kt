package com.sycosoft.hireout.services.user

import com.sycosoft.hireout.TestStrings
import com.sycosoft.hireout.database.entities.User
import com.sycosoft.hireout.database.repository.UserRepository
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
    private lateinit var  repository: UserRepository

    private val testUUID = UUID.randomUUID()
    private val adminUUID = UUID.randomUUID()
    private lateinit var user: User
    private lateinit var adminUser: User

    @BeforeEach
    fun setUp() {
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

        Mockito.`when`(repository.findById(adminUUID)).thenReturn(Optional.of(adminUser))
        Mockito.`when`(repository.getUserByUsername(adminUser.username!!)).thenReturn(Optional.of(adminUser))
    }

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
        Mockito.`when`(repository.save(test)).thenReturn(user)

        val savedTest = userService.saveUser(test)

        Mockito.verify(repository, Mockito.atLeastOnce()).save(test)
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

        Mockito.verify(repository, Mockito.never()).save(test)
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

        Mockito.verify(repository, Mockito.never()).save(test)
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

        Mockito.verify(repository, Mockito.never()).save(test)
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

        Mockito.verify(repository, Mockito.never()).save(test)
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

        Mockito.verify(repository, Mockito.never()).save(test)
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

        Mockito.verify(repository, Mockito.never()).save(test)
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
        Mockito.`when`(repository.getUserByUsername(test.username!!)).thenReturn(Optional.of(user))

        // When
        val savedTest = userService.saveUser(test)

        // Then
        Mockito.verify(repository, Mockito.never()).save(test)  // Verify that the save method is never called again
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

        Mockito.verify(repository, Mockito.never()).save(test)
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

        Mockito.verify(repository, Mockito.never()).save(test)
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

        Mockito.verify(repository, Mockito.never()).save(test)
        assertEquals(ResultCode.UPDATE_FAILURE, savedUser.code)
        assertNull(savedUser.entity)
        assertNotNull(savedUser.errorMessage)
        assertEquals(UserService.ErrorMessages.USER_UUID_NULL_OR_BLANK, savedUser.errorMessage)
    }

    @Test
    fun givenInvalidUUID_whenUpdating_thenReturnFailureResult() {
        val test = user.copy(uuid = UUID.randomUUID())

        val savedUser = userService.updateUser(test)
        Mockito.verify(repository, Mockito.never()).save(test)
        assertEquals(ResultCode.UPDATE_FAILURE, savedUser.code)
        assertNull(savedUser.entity)
        assertNotNull(savedUser.errorMessage)
        assertEquals(UserService.ErrorMessages.USER_NOT_FOUND_UUID + test.uuid, savedUser.errorMessage)
    }

//region First Name Tests

    @Test
    fun givenValidFirstNameUpdate_whenUpdating_thenReturnSuccessResult() {
        val test = user.copy(firstName = TestStrings.USER_FIRST_NAME_VALID_UPDATE)
        Mockito.`when`(repository.findById(test.uuid!!)).thenReturn(Optional.of(user))
        Mockito.`when`(repository.save(test)).thenReturn(test)

        val savedUser = userService.updateUser(test)

        Mockito.verify(repository, Mockito.atLeastOnce()).save(test)
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
        Mockito.`when`(repository.findById(test.uuid!!)).thenReturn(Optional.of(user))

        val savedUser = userService.updateUser(test)

        Mockito.verify(repository, Mockito.never()).save(test)
        assertEquals(ResultCode.UPDATE_FAILURE, savedUser.code)
        assertNull(savedUser.entity)
        assertNotNull(savedUser.errorMessage)
        assertEquals(UserService.ErrorMessages.NOTHING_TO_UPDATE, savedUser.errorMessage)
    }

    @Test
    fun givenFirstNameNull_whenUpdating_thenReturnFailureResult() {
        val test = user.copy(firstName = null)
        Mockito.`when`(repository.findById(test.uuid!!)).thenReturn(Optional.of(user))

        val savedUser = userService.updateUser(test)

        Mockito.verify(repository, Mockito.never()).save(test)
        assertEquals(ResultCode.UPDATE_FAILURE, savedUser.code)
        assertNull(savedUser.entity)
        assertNotNull(savedUser.errorMessage)
        assertEquals(UserService.ErrorMessages.NOTHING_TO_UPDATE, savedUser.errorMessage)
    }

    @Test
    fun givenFirstNameBlank_whenUpdating_thenReturnFailureResult() {
        val test = user.copy(firstName = "")
        Mockito.`when`(repository.findById(test.uuid!!)).thenReturn(Optional.of(user))

        val savedUser = userService.updateUser(test)

        Mockito.verify(repository, Mockito.never()).save(test)
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
        Mockito.`when`(repository.findById(test.uuid!!)).thenReturn(Optional.of(user))
        Mockito.`when`(repository.save(test)).thenReturn(test)

        val savedUser = userService.updateUser(test)

        Mockito.verify(repository, Mockito.atLeastOnce()).save(test)
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
        Mockito.`when`(repository.findById(test.uuid!!)).thenReturn(Optional.of(user))

        val savedUser = userService.updateUser(test)

        Mockito.verify(repository, Mockito.never()).save(test)
        assertEquals(ResultCode.UPDATE_FAILURE, savedUser.code)
        assertNull(savedUser.entity)
        assertNotNull(savedUser.errorMessage)
        assertEquals(UserService.ErrorMessages.NOTHING_TO_UPDATE, savedUser.errorMessage)
    }

    @Test
    fun givenLastNameNull_whenUpdating_thenReturnFailureResult() {
        val test = user.copy(lastName = null)
        Mockito.`when`(repository.findById(test.uuid!!)).thenReturn(Optional.of(user))

        val savedUser = userService.updateUser(test)

        Mockito.verify(repository, Mockito.never()).save(test)
        assertEquals(ResultCode.UPDATE_FAILURE, savedUser.code)
        assertNull(savedUser.entity)
        assertNotNull(savedUser.errorMessage)
        assertEquals(UserService.ErrorMessages.NOTHING_TO_UPDATE, savedUser.errorMessage)
    }

    @Test
    fun givenLastNameBlank_whenUpdating_thenReturnFailureResult() {
        val test = user.copy(lastName = "")
        Mockito.`when`(repository.findById(test.uuid!!)).thenReturn(Optional.of(user))

        val savedUser = userService.updateUser(test)

        Mockito.verify(repository, Mockito.never()).save(test)
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
        Mockito.`when`(repository.findById(test.uuid!!)).thenReturn(Optional.of(user))

        val savedUser = userService.updateUser(test)

        Mockito.verify(repository, Mockito.never()).save(test)
        assertEquals(ResultCode.UPDATE_FAILURE, savedUser.code)
        assertNull(savedUser.entity)
        assertNotNull(savedUser.errorMessage)
        assertEquals(UserService.ErrorMessages.USER_USERNAME_CANNOT_BE_CHANGED, savedUser.errorMessage)
    }

    @Test
    fun givenUsernameNothingUpdated_whenUpdating_thenReturnFailureResult() {
        val test = user.copy()
        Mockito.`when`(repository.findById(test.uuid!!)).thenReturn(Optional.of(user))

        val savedUser = userService.updateUser(test)

        Mockito.verify(repository, Mockito.never()).save(test)
        assertEquals(ResultCode.UPDATE_FAILURE, savedUser.code)
        assertNull(savedUser.entity)
        assertNotNull(savedUser.errorMessage)
        assertEquals(UserService.ErrorMessages.NOTHING_TO_UPDATE, savedUser.errorMessage)
    }

    @Test
    fun givenUsernameNull_whenUpdating_thenReturnFailureResult() {
        val test = user.copy(username = null)
        Mockito.`when`(repository.findById(test.uuid!!)).thenReturn(Optional.of(user))

        val savedUser = userService.updateUser(test)

        Mockito.verify(repository, Mockito.never()).save(test)
        assertEquals(ResultCode.UPDATE_FAILURE, savedUser.code)
        assertNull(savedUser.entity)
        assertNotNull(savedUser.errorMessage)
        assertEquals(UserService.ErrorMessages.NOTHING_TO_UPDATE, savedUser.errorMessage)
    }

    @Test
    fun givenUsernameBlank_whenUpdating_thenReturnFailureResult() {
        val test = user.copy(username = "")
        Mockito.`when`(repository.findById(test.uuid!!)).thenReturn(Optional.of(user))

        val savedUser = userService.updateUser(test)

        Mockito.verify(repository, Mockito.never()).save(test)
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
        Mockito.`when`(repository.findById(test.uuid!!)).thenReturn(Optional.of(user))
        Mockito.`when`(repository.save(test)).thenReturn(test)

        val savedUser = userService.updateUser(test)

        Mockito.verify(repository, Mockito.atLeastOnce()).save(test)
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
        Mockito.`when`(repository.findById(test.uuid!!)).thenReturn(Optional.of(user))

        val savedUser = userService.updateUser(test)

        Mockito.verify(repository, Mockito.never()).save(test)
        assertEquals(ResultCode.UPDATE_FAILURE, savedUser.code)
        assertNull(savedUser.entity)
        assertNotNull(savedUser.errorMessage)
        assertEquals(UserService.ErrorMessages.NOTHING_TO_UPDATE, savedUser.errorMessage)
    }

    @Test
    fun givenPasswordNull_whenUpdating_thenReturnFailureResult() {
        val test = user.copy(password = null)
        Mockito.`when`(repository.findById(test.uuid!!)).thenReturn(Optional.of(user))

        val savedUser = userService.updateUser(test)

        Mockito.verify(repository, Mockito.never()).save(test)
        assertEquals(ResultCode.UPDATE_FAILURE, savedUser.code)
        assertNull(savedUser.entity)
        assertNotNull(savedUser.errorMessage)
        assertEquals(UserService.ErrorMessages.NOTHING_TO_UPDATE, savedUser.errorMessage)
    }

    @Test
    fun givenPasswordBlank_whenUpdating_thenReturnFailureResult() {
        val test = user.copy(password = "")
        Mockito.`when`(repository.findById(test.uuid!!)).thenReturn(Optional.of(user))

        val savedUser = userService.updateUser(test)

        Mockito.verify(repository, Mockito.never()).save(test)
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
        Mockito.`when`(repository.findById(test.uuid!!)).thenReturn(Optional.of(user))
        Mockito.`when`(repository.save(test)).thenThrow(DataIntegrityViolationException("Test Exception Triggered"))

        val savedUser = userService.updateUser(test)
        Mockito.verify(repository, Mockito.atLeastOnce()).save(test)
        assertEquals(ResultCode.UPDATE_FAILURE, savedUser.code)
        assertNull(savedUser.entity)
        assertNotNull(savedUser.errorMessage)
        assertEquals(savedUser.errorMessage, "Test Exception Triggered")
    }

    @Test
    fun givenOptimisticLockingFailureException_whenUpdating_thenReturnFailureResult() {
        val test = user.copy(firstName = TestStrings.USER_FIRST_NAME_VALID_UPDATE)
        Mockito.`when`(repository.findById(test.uuid!!)).thenReturn(Optional.of(user))
        Mockito.`when`(repository.save(test)).thenThrow(OptimisticLockingFailureException("Test Exception Triggered"))

        val savedUser = userService.updateUser(test)
        Mockito.verify(repository, Mockito.atLeastOnce()).save(test)
        assertEquals(ResultCode.UPDATE_FAILURE, savedUser.code)
        assertNull(savedUser.entity)
        assertNotNull(savedUser.errorMessage)
        assertEquals(savedUser.errorMessage, "Test Exception Triggered")
    }

    @Test
    fun givenStaleStateException_whenUpdating_thenReturnFailureResult() {
        val test = user.copy(firstName = TestStrings.USER_FIRST_NAME_VALID_UPDATE)
        Mockito.`when`(repository.findById(test.uuid!!)).thenReturn(Optional.of(user))
        Mockito.`when`(repository.save(test)).thenThrow(StaleStateException("Test Exception Triggered"))

        val savedUser = userService.updateUser(test)
        Mockito.verify(repository, Mockito.atLeastOnce()).save(test)
        assertEquals(ResultCode.UPDATE_FAILURE, savedUser.code)
        assertNull(savedUser.entity)
        assertNotNull(savedUser.errorMessage)
        assertEquals(savedUser.errorMessage, "Test Exception Triggered")
    }

    @Test
    fun givenPersistenceException_whenUpdating_thenReturnFailureResult() {
        val test = user.copy(firstName = TestStrings.USER_FIRST_NAME_VALID_UPDATE)
        Mockito.`when`(repository.findById(test.uuid!!)).thenReturn(Optional.of(user))
        Mockito.`when`(repository.save(test)).thenThrow(PersistenceException("Test Exception Triggered"))

        val savedUser = userService.updateUser(test)
        Mockito.verify(repository, Mockito.atLeastOnce()).save(test)
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
        Mockito.`when`(repository.findById(user.uuid!!)).thenReturn(Optional.of(user))

        val found = userService.getUser(user.uuid!!)

        Mockito.verify(repository, Mockito.atLeastOnce()).findById(user.uuid!!)
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

        Mockito.verify(repository, Mockito.atLeastOnce()).findById(testUUID)
        assertEquals(found.code, ResultCode.FETCH_FAILURE)
        assertNull(found.entity)
        assertNotNull(found.errorMessage)
        assertEquals(found.errorMessage, UserService.ErrorMessages.USER_NOT_FOUND_UUID + testUUID)
    }

//region Exception Tests

    @Test
    fun givenDataAccessException_whenGetting_thenProvideFailureResult() {
        Mockito.`when`(repository.findById(user.uuid!!)).thenThrow(EmptyResultDataAccessException(1))

        val found = userService.getUser(user.uuid!!)

        Mockito.verify(repository, Mockito.atLeastOnce()).findById(user.uuid!!)
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
        Mockito.`when`(repository.getUserByUsername(user.username!!)).thenReturn(Optional.of(user))

        val found = userService.getUser(user.username!!)

        Mockito.verify(repository, Mockito.atLeastOnce()).getUserByUsername(user.username!!)
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

        Mockito.verify(repository, Mockito.atLeastOnce()).getUserByUsername(TestStrings.USER_USERNAME_INVALID)
        assertEquals(found.code, ResultCode.FETCH_FAILURE)
        assertNull(found.entity)
        assertNotNull(found.errorMessage)
        assertEquals(found.errorMessage, UserService.ErrorMessages.USER_NOT_FOUND_USERNAME + TestStrings.USER_USERNAME_INVALID)
    }

//region Exception Tests

    @Test
    fun givenEmptyResultDataAccessException_whenGetting_thenProvideFailureResult() {
        Mockito.`when`(repository.getUserByUsername(user.username!!)).thenThrow(EmptyResultDataAccessException(1))

        val found = userService.getUser(user.username!!)

        Mockito.verify(repository, Mockito.atLeastOnce()).getUserByUsername(user.username!!)
        assertEquals(found.code, ResultCode.FETCH_FAILURE)
        assertNull(found.entity)
        assertNotNull(found.errorMessage)
        assertEquals(found.errorMessage, "Incorrect result size: expected 1, actual 0")
    }

    @Test
    fun givenIncorrectResultSizeDataAccessException_whenGetting_thenProvideFailureResult() {
        Mockito.`when`(repository.getUserByUsername(user.username!!)).thenThrow(IncorrectResultSizeDataAccessException(1))

        val found = userService.getUser(user.username!!)

        Mockito.verify(repository, Mockito.atLeastOnce()).getUserByUsername(user.username!!)
        assertEquals(found.code, ResultCode.FETCH_FAILURE)
        assertNull(found.entity)
        assertNotNull(found.errorMessage)
        assertEquals(found.errorMessage, "Incorrect result size: expected 1")
    }

    @Test
    fun givenDataRetrievalFailureException_whenGetting_thenProvideFailureResult() {
        Mockito.`when`(repository.getUserByUsername(user.username!!)).thenThrow(DataRetrievalFailureException("Test Exception Triggered"))

        val found = userService.getUser(user.username!!)

        Mockito.verify(repository, Mockito.atLeastOnce()).getUserByUsername(user.username!!)
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
        Mockito.`when`(repository.findById(user.uuid!!)).thenReturn(Optional.of(user))

        val deletedUser = userService.deleteUser(user.uuid!!)

        Mockito.verify(repository, Mockito.atLeastOnce()).deleteById(user.uuid!!)
        assertEquals(deletedUser.code, ResultCode.DELETION_SUCCESS)
        assertNotNull(deletedUser.entity)
        assertTrue(deletedUser.entity!!)
        assertNull(deletedUser.errorMessage)
    }

    @Test
    fun givenInvalidUserUUID_whenDeleting_thenProvideFailureResult() {
        val testUUID = UUID.randomUUID()

        val deletedUser = userService.deleteUser(testUUID)

        Mockito.verify(repository, Mockito.never()).deleteById(testUUID)
        assertEquals(deletedUser.code, ResultCode.DELETION_FAILURE)
        assertNull(deletedUser.entity)
        assertNotNull(deletedUser.errorMessage)
        assertEquals(deletedUser.errorMessage, UserService.ErrorMessages.USER_NOT_FOUND_UUID + testUUID)
    }

    @Test
    fun givenAdminUserUUID_whenDeleting_thenProvideFailureResult() {
        val deletedUser = userService.deleteUser(adminUser.uuid!!)

        Mockito.verify(repository, Mockito.never()).deleteById(adminUser.uuid!!)
        assertEquals(deletedUser.code, ResultCode.DELETION_FAILURE)
        assertNull(deletedUser.entity)
        assertNotNull(deletedUser.errorMessage)
        assertEquals(deletedUser.errorMessage, UserService.ErrorMessages.CANNOT_DELETE_ADMIN_USER)
    }

//endregion
//region Username

    @Test
    fun givenValidUserUsername_whenDeleting_thenProvideSuccessfulResult() {
        Mockito.`when`(repository.getUserByUsername(user.username!!)).thenReturn(Optional.of(user))
        Mockito.`when`(repository.findById(user.uuid!!)).thenReturn(Optional.of(user))

        val deletedUser = userService.deleteUser(user.username!!)

        Mockito.verify(repository, Mockito.atLeastOnce()).deleteById(user.uuid!!)
        assertEquals(deletedUser.code, ResultCode.DELETION_SUCCESS)
        assertNotNull(deletedUser.entity)
        assertTrue(deletedUser.entity!!)
        assertNull(deletedUser.errorMessage)
    }

    @Test
    fun givenInvalidUserUsername_whenDeleting_thenProvideFailureResult() {

        val deletedUser = userService.deleteUser(TestStrings.USER_USERNAME_INVALID)

        Mockito.verify(repository, Mockito.never()).deleteById(testUUID)
        assertEquals(deletedUser.code, ResultCode.DELETION_FAILURE)
        assertNull(deletedUser.entity)
        assertNotNull(deletedUser.errorMessage)
        assertEquals(deletedUser.errorMessage, UserService.ErrorMessages.USER_NOT_FOUND_USERNAME + TestStrings.USER_USERNAME_INVALID)
    }

    @Test
    fun givenAdminUserUsername_whenDeleting_thenProvideFailureResult() {
        val deletedUser = userService.deleteUser(adminUser.username!!)

        Mockito.verify(repository, Mockito.never()).deleteById(adminUser.uuid!!)
        assertEquals(deletedUser.code, ResultCode.DELETION_FAILURE)
        assertNull(deletedUser.entity)
        assertNotNull(deletedUser.errorMessage)
        assertEquals(deletedUser.errorMessage, UserService.ErrorMessages.CANNOT_DELETE_ADMIN_USER)
    }

//endregion
//endregion
}