package com.sycosoft.hireout.services.user

import com.sycosoft.hireout.database.entities.User
import com.sycosoft.hireout.database.repository.UserRepository
import com.sycosoft.hireout.database.result.DatabaseResult
import com.sycosoft.hireout.database.result.ResultCode
import org.springframework.dao.ConcurrencyFailureException
import org.springframework.dao.DataAccessException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserServiceImpl(
    private val userRepository: UserRepository
) : UserService {
    override fun saveUser(user: User): DatabaseResult<User> {
        return try {
            // Let's check if the first name field is blank or null. If either, we then need to
            // return with a failure result and the reason why.
            if(user.firstName.isNullOrBlank()) {
                return DatabaseResult(
                    code = ResultCode.CREATION_FAILURE,
                    errorMessage = UserService.ErrorMessages.FIRST_NAME_NULL_OR_BLANK
                )
            }

            // Next up, is doing the same checks for last name as the first name. Same result.
            if(user.lastName.isNullOrBlank()) {
                return DatabaseResult(
                    code = ResultCode.CREATION_FAILURE,
                    errorMessage = UserService.ErrorMessages.LAST_NAME_NULL_OR_BLANK
                )
            }

            // We also need to do the same for username.
            if(user.username.isNullOrBlank()) {
                return DatabaseResult(
                    code = ResultCode.CREATION_FAILURE,
                    errorMessage = UserService.ErrorMessages.USERNAME_NULL_OR_BLANK
                )
            }

            // We now need to check if we already have a username that exists in the database.
            // If we do, we need to let the caller know we cannot save this user due to
            // this violation.
            if(userRepository.getUserByUsername(user.username).isPresent) {
                return DatabaseResult(
                    code = ResultCode.CREATION_FAILURE,
                    errorMessage = UserService.ErrorMessages.USERNAME_NOT_UNIQUE
                )
            }

            // We have to also check to make sure the password is not null or blank. We need to make sure
            // the user can log in after all.
            if(user.password.isNullOrBlank()) {
                return DatabaseResult(
                    code = ResultCode.CREATION_FAILURE,
                    errorMessage = UserService.ErrorMessages.PASSWORD_NULL_OR_BLANK
                )
            }

            // All going well, we should be able to save the user object as expected.
            DatabaseResult(
                code = ResultCode.CREATION_SUCCESS,
                entity = userRepository.save(user)
            )
        } catch(exception: DataAccessException) {
            DatabaseResult(
                code = ResultCode.CREATION_FAILURE,
                errorMessage = exception.message.toString()
            )
        } catch(exception: DataIntegrityViolationException) {
            DatabaseResult(
                code = ResultCode.CREATION_FAILURE,
                errorMessage = exception.message.toString()
            )
        } catch(exception: ConcurrencyFailureException) {
            DatabaseResult(
                code = ResultCode.CREATION_FAILURE,
                errorMessage = exception.message.toString()
            )
        } catch(exception: OptimisticLockingFailureException) {
            DatabaseResult(
                code = ResultCode.CREATION_FAILURE,
                errorMessage = exception.message.toString()
            )
        } catch(exception: Exception) {
            DatabaseResult(
                code = ResultCode.CREATION_FAILURE,
                errorMessage = exception.message.toString()
            )
        }
    }

    override fun updateUser(user: User): DatabaseResult<User> {
        return try {
            // First we need to have a flag to let us know if an update is needed.
            var updateNeeded = false

            // We then need to check if the UUID of the user we need to update is null or not.
            if(user.uuid == null) {
                // If it is, we cannot use this to find the user. Let the caller know this.
                return DatabaseResult(
                    code = ResultCode.UPDATE_FAILURE,
                    errorMessage = UserService.ErrorMessages.USER_UUID_NULL_OR_BLANK
                )
            }

            // Attempt to find the user we need to update.
            val found = getUser(user.uuid)

            // If we're not successful, let the user know the reason by passing on the
            // failure message.
            if(found.code == ResultCode.FETCH_FAILURE) {
                return DatabaseResult(
                    code = ResultCode.UPDATE_FAILURE,
                    errorMessage = found.errorMessage
                )
            }

            // if we are successful, we need to check to make sure that our entity is no null either.
            // If it is, let the caller know.
            if(found.entity != null) {
                // If it's not null, we can now start processing if we need to make an update to any
                // of the fields within the found user.

                // First off, let's check the first name to see if it's null or blank and also if
                // the fields are different at all.
                if(!user.firstName.isNullOrBlank() && user.firstName != found.entity.firstName) {
                    // If an update is needed, change the found user entity.
                    found.entity.firstName = user.firstName
                    // Then enable the flag to let us know we need to update the database entity.
                    updateNeeded = true
                }

                if(!user.lastName.isNullOrBlank() && user.lastName != found.entity.lastName) {
                    found.entity.lastName = user.lastName
                    updateNeeded = true
                }

                // If the user tries to change the username, throw an update failure
                if(!user.username.isNullOrBlank() && user.username != found.entity.username) {
                    return DatabaseResult(
                        code = ResultCode.UPDATE_FAILURE,
                        errorMessage = UserService.ErrorMessages.USER_USERNAME_CANNOT_BE_CHANGED
                    )
                }

                if(!user.password.isNullOrBlank() && user.password != found.entity.password) {
                    found.entity.password = user.password
                    updateNeeded= true
                }
            }

            // Let's now check to see if we need to update the database entity.
            return if(updateNeeded) {
                // If we do, we can let the caller know that we've done this and provide the saved
                // entity.
                DatabaseResult(
                    code = ResultCode.UPDATE_SUCCESS,
                    entity = userRepository.save(found.entity!!)
                )
            } else {
                // If there's nothing to update, also let the caller know by sending a failure result
                // and the reason why we've failed.
                DatabaseResult(
                    code = ResultCode.UPDATE_FAILURE,
                    errorMessage = UserService.ErrorMessages.NOTHING_TO_UPDATE
                )
            }

        } catch(exception: Exception) {
            DatabaseResult(
                code = ResultCode.UPDATE_FAILURE,
                errorMessage = exception.message.toString()
            )
        }
    }

    override fun getUser(uuid: UUID): DatabaseResult<User> {
        // First off, let's try and find the object.
        return try {
            val found = userRepository.findById(uuid)

            if(!found.isPresent) {
                return DatabaseResult(
                    code = ResultCode.FETCH_FAILURE,
                    errorMessage = UserService.ErrorMessages.USER_NOT_FOUND_UUID + uuid
                )
            }

            DatabaseResult(
                code = ResultCode.FETCH_SUCCESS,
                entity = found.get()
            )
        } catch(exception: Exception) {
            DatabaseResult(
                code = ResultCode.FETCH_FAILURE,
                errorMessage = exception.message.toString()
            )
        }
    }
}