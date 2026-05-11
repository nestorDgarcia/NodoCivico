package com.nestorgarcia.nodocivico.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nestorgarcia.nodocivico.model.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User): Long

    @Update
    suspend fun update(user: User)

    @Delete
    suspend fun delete(user: User)

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getById(id: Int): User?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getByUsername(username: String): User?

    @Query("SELECT * FROM users LIMIT 1")
    fun getCurrentUser(): LiveData<User?>

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getCurrentUserOnce(): User?
}