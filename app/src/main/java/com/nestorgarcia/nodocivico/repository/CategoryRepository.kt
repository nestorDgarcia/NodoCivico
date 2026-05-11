package com.nestorgarcia.nodocivico.repository

import androidx.lifecycle.LiveData
import com.nestorgarcia.nodocivico.data.local.dao.CategoryDao
import com.nestorgarcia.nodocivico.model.Category

class CategoryRepository(private val categoryDao: CategoryDao) {

    fun getAll(): LiveData<List<Category>> = categoryDao.getAll()

    suspend fun getAllOnce(): List<Category> = categoryDao.getAllOnce()

    suspend fun getById(id: Int): Category? = categoryDao.getById(id)

    suspend fun insert(category: Category): Long = categoryDao.insert(category)

    suspend fun insertAll(categories: List<Category>) = categoryDao.insertAll(categories)

    suspend fun update(category: Category) = categoryDao.update(category)

    suspend fun delete(category: Category) = categoryDao.delete(category)
}