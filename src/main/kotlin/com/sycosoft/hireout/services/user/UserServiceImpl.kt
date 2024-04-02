package com.sycosoft.hireout.services.user

import com.sycosoft.hireout.database.entities.User
import com.sycosoft.hireout.database.entities.UserRole
import com.sycosoft.hireout.database.repositories.UserRepository
import com.sycosoft.hireout.database.repositories.UserRoleRepository
import com.sycosoft.hireout.database.result.DatabaseResult
import com.sycosoft.hireout.database.result.ResultCode
import jakarta.persistence.PersistenceException
import org.hibernate.StaleStateException
import org.hibernate.exception.ConstraintViolationException
import org.hibernate.exception.LockAcquisitionException
import org.springframework.dao.*
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val roleRepository: UserRoleRepository,
) : UserService {
//region User Methods

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

        } catch(exception: DataAccessException) {
            DatabaseResult(
                code = ResultCode.UPDATE_FAILURE,
                errorMessage = exception.message.toString()
            )
        } catch (exception: OptimisticLockingFailureException) {
            DatabaseResult(
                code = ResultCode.UPDATE_FAILURE,
                errorMessage = exception.message.toString()
            )
        } catch (exception: StaleStateException) {
            DatabaseResult(
                code = ResultCode.UPDATE_FAILURE,
                errorMessage = exception.message.toString()
            )
        } catch (exception: PersistenceException) {
            DatabaseResult(
                code = ResultCode.UPDATE_FAILURE,
                errorMessage = exception.message.toString()
            )
        } catch (exception: LockAcquisitionException) {
            DatabaseResult(
                code = ResultCode.UPDATE_FAILURE,
                errorMessage = exception.message.toString()
            )
        } catch (exception: ConstraintViolationException) {
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
        } catch(exception: DataAccessException) {
            DatabaseResult(
                code = ResultCode.FETCH_FAILURE,
                errorMessage = exception.message.toString()
            )
        }
    }

    override fun getUser(username: String): DatabaseResult<User> {
        // First off, let's try and find the object.
        return try {
            val found = userRepository.getUserByUsername(username)

            if(!found.isPresent) {
                return DatabaseResult(
                    code = ResultCode.FETCH_FAILURE,
                    errorMessage = UserService.ErrorMessages.USER_NOT_FOUND_USERNAME + username
                )
            }

            DatabaseResult(
                code = ResultCode.FETCH_SUCCESS,
                entity = found.get()
            )
        } catch(exception: DataAccessException) {
            DatabaseResult(
                code = ResultCode.FETCH_FAILURE,
                errorMessage = exception.message.toString()
            )
        } catch(exception: DataRetrievalFailureException) {
            DatabaseResult(
                code = ResultCode.FETCH_FAILURE,
                errorMessage = exception.message.toString()
            )
        }
    }

    override fun getAllUsers(): List<User> = userRepository.findAll()

    override fun deleteUser(uuid: UUID): DatabaseResult<Boolean> {
        return try {
            val found = getUser(uuid)

            if(found.code != ResultCode.FETCH_SUCCESS) {
                return DatabaseResult(
                    code = ResultCode.DELETION_FAILURE,
                    errorMessage = found.errorMessage
                )
            }

            val adminUser = getUser("admin.user")

            if(adminUser.code != ResultCode.FETCH_SUCCESS) {
                return DatabaseResult(
                    code = ResultCode.DELETION_FAILURE,
                    errorMessage = UserService.ErrorMessages.ADMIN_USER_NOT_FOUND + adminUser.errorMessage
                )
            }

            if(found.entity?.uuid?.equals(adminUser.entity?.uuid) == true) {
                return DatabaseResult(
                    code = ResultCode.DELETION_FAILURE,
                    errorMessage = UserService.ErrorMessages.CANNOT_DELETE_ADMIN_USER
                )
            }

            found.entity?.isDeleted = true

            updateUser(found.entity!!)

            // TODO: Transfer all jobs and data to admin user.

            userRepository.deleteById(found.entity.uuid!!)

            DatabaseResult(
                code = ResultCode.DELETION_SUCCESS,
                entity = true
            )
        } catch(exception: Exception) {
            DatabaseResult(
                code = ResultCode.DELETION_FAILURE,
                errorMessage = exception.message.toString()
            )
        }
    }

    override fun deleteUser(username: String): DatabaseResult<Boolean> {
        val found = getUser(username)

        if(found.code != ResultCode.FETCH_SUCCESS) {
            return DatabaseResult(
                code = ResultCode.DELETION_FAILURE,
                errorMessage = found.errorMessage
            )
        }

        return deleteUser(found.entity?.uuid!!)
    }

//endregion

    override fun saveUserRole(role: UserRole): DatabaseResult<UserRole> {
        return try {
            if(role.roleName.isNullOrBlank()) {
                return DatabaseResult(
                    code = ResultCode.CREATION_FAILURE,
                    errorMessage = UserService.ErrorMessages.ROLE_NAME_NULL_OR_BLANK
                )
            }

            if(getAllUserRoles().isNotEmpty()) {
                val found = getUserRole(role.roleName)

                if(found.code != ResultCode.FETCH_FAILURE) {
                    return DatabaseResult(
                        code = ResultCode.CREATION_FAILURE,
                        errorMessage = UserService.ErrorMessages.ROLE_NOT_UNIQUE
                    )
                }
            }

            val saved = roleRepository.save(role)

            DatabaseResult(
                code = ResultCode.CREATION_SUCCESS,
                entity = saved
            )
        } catch(exception: DataAccessException) {
            DatabaseResult(
                code = ResultCode.CREATION_FAILURE,
                errorMessage = exception.message.toString()
            )
        }  catch(exception: ConcurrencyFailureException) {
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

    override fun saveUserRole(roleName: String): DatabaseResult<UserRole> = saveUserRole(UserRole(roleName = roleName))

    override fun saveUserRoles(roles: List<UserRole>): List<DatabaseResult<UserRole>> {
        if(roles.isEmpty()) {
            return listOf(
                DatabaseResult(
                    code = ResultCode.CREATION_FAILURE,
                    errorMessage = UserService.ErrorMessages.NO_ROLES_TO_SAVE_IN_LIST
                )
            )
        }

        val savedRoles: MutableList<DatabaseResult<UserRole>> = mutableListOf()

        roles.forEach { role ->
            savedRoles.add(saveUserRole(role))
        }

        return savedRoles
    }

    override fun saveUserRolesByName(roles: List<String>): List<DatabaseResult<UserRole>> {

        if(roles.isEmpty()) {
            return listOf(
                DatabaseResult(
                    code = ResultCode.CREATION_FAILURE,
                    errorMessage = UserService.ErrorMessages.NO_ROLES_TO_SAVE_IN_LIST
                )
            )
        }

        val savedRoles: MutableList<DatabaseResult<UserRole>> = mutableListOf()

        roles.forEach { role ->
            savedRoles.add(saveUserRole(role))
        }

        return savedRoles
    }

    override fun getUserRole(id: Int): DatabaseResult<UserRole> {
        return try {
            val found = roleRepository.findById(id)

            if(!found.isPresent) {
                return DatabaseResult(
                    code = ResultCode.FETCH_FAILURE,
                    errorMessage = UserService.ErrorMessages.ROLE_NOT_FOUND_ID + id
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

    override fun getUserRole(roleName: String): DatabaseResult<UserRole> {
        return try {
            val found = roleRepository.getRoleByName(roleName)

            if(!found.isPresent) {
                return DatabaseResult(
                    code = ResultCode.FETCH_FAILURE,
                    errorMessage = UserService.ErrorMessages.ROLE_NOT_FOUND_NAME + roleName
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

    override fun getAllUserRoles(): List<UserRole> = roleRepository.findAll()

    override fun addRoleToUser(username: String, roleName: String): DatabaseResult<User> {
        TODO("Not yet implemented")
    }
}