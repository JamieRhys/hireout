package com.sycosoft.hireout.services.user

import com.sycosoft.hireout.database.entities.User
import com.sycosoft.hireout.database.result.DatabaseResult
import com.sycosoft.hireout.database.result.ResultCode
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import java.util.*

interface UserService {
    /** Saves the [User] to the database.
     * @param user The [User] object to be saved.
     * @return A [DatabaseResult] which, if successful, will hold a success [ResultCode] and the Database Entity. If unsuccessful, the [ResultCode] will hold failure
     * and the entity will be null.
     */
    fun saveUser(@NotNull @Valid user: User): DatabaseResult<User>

    /** Updates changed [User] fields and persists those changes to our database.
     * @param user The [User] object to be updated.
     * @return A [DatabaseResult] which, if successful, will hold a success [ResultCode] and the Database Entity. If unsuccessful, the [ResultCode] will hold failure
     *       and the entity will be null.
     */
    fun updateUser(user: User): DatabaseResult<User>

    /** Attempts to get [User] from database with the provided [UUID].
     * @param uuid The [UUID] of the user we want to retrieve from the database.
     * @return [DatabaseResult] either with a [ResultCode.FETCH_FAILURE] or [ResultCode.FETCH_SUCCESS] and the entity if the latter.
     */
    fun getUser(uuid: UUID): DatabaseResult<User>

    /** Attempts to get [User] from database with the provided [String] username.
     * @param username The [String] object that is the [User] username.
     * @return [DatabaseResult] either with a [ResultCode.FETCH_FAILURE] or [ResultCode.FETCH_SUCCESS] and the entity if the latter.
     */
    fun getUser(username: String): DatabaseResult<User>

    class ErrorMessages {
        companion object {
            const val FIRST_NAME_NULL_OR_BLANK: String = "Please provide a first name."
            const val LAST_NAME_NULL_OR_BLANK: String = "Please provide a last name."
            const val USERNAME_NULL_OR_BLANK: String = "Please provide a username."
            const val PASSWORD_NULL_OR_BLANK: String = "Please provide a password."
            const val USERNAME_NOT_UNIQUE: String = "Username already being used. Please choose another."
            const val NOTHING_TO_UPDATE: String = "Nothing to update for provided user."

            const val USER_NOT_FOUND_UUID: String = "User not found with UUID of "
            const val USER_NOT_FOUND_USERNAME: String = "User not found with username of "
            const val USER_UUID_NULL_OR_BLANK: String = "User UUID is null or blank."
            const val USER_USERNAME_CANNOT_BE_CHANGED: String = "Username cannot be changed."
        }
    }
}