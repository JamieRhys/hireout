package com.sycosoft.hireout.services.user

import com.sycosoft.hireout.TestStrings
import com.sycosoft.hireout.database.entities.User
import com.sycosoft.hireout.database.repository.UserRepository
import com.sycosoft.hireout.database.result.ResultCode
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.dao.DataIntegrityViolationException
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
    private lateinit var user: User

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
//region Update Method Tests

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
//endregion
}