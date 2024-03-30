package com.sycosoft.hireout.database.result

/** The result of a query to the database.
 * @param code The [ResultCode] of whether the operation was successful or not.
 * @param errorMessage If the operation was not successful, this will hold what caused the error. Otherwise, this is null.
 * @param entity If successful, this will hold the entity which has been requested.
 */
data class DatabaseResult<T>(
    val code: ResultCode,
    val errorMessage: String? = null,
    val entity: T? = null
)
