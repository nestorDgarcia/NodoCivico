package com.nestorgarcia.nodocivico.repository

import androidx.lifecycle.LiveData
import com.nestorgarcia.nodocivico.data.local.dao.UserDao
import com.nestorgarcia.nodocivico.model.User
import java.security.MessageDigest

class UserRepository(private val userDao: UserDao) {

    fun getCurrentUser(): LiveData<User?> = userDao.getCurrentUser()

    suspend fun getCurrentUserOnce(): User? = userDao.getCurrentUserOnce()

    suspend fun getByUsername(username: String): User? = userDao.getByUsername(username)

    suspend fun register(username: String, password: String, zone: String): Long {
        val hashed = hashPassword(password)
        val user = User(username = username, password = hashed, zone = zone)
        return userDao.insert(user)
    }

    suspend fun login(username: String, password: String): User? {
        val user = userDao.getByUsername(username) ?: return null
        val hashed = hashPassword(password)
        return if (user.password == hashed) user else null
    }

    suspend fun update(user: User) = userDao.update(user)

    suspend fun delete(user: User) = userDao.delete(user)

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}