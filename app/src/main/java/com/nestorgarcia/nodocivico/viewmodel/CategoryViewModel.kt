package com.nestorgarcia.nodocivico.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nestorgarcia.nodocivico.model.Category
import com.nestorgarcia.nodocivico.repository.CategoryRepository
import kotlinx.coroutines.launch

class CategoryViewModel(private val categoryRepository: CategoryRepository) : ViewModel() {

    val allCategories: LiveData<List<Category>> = categoryRepository.getAll()

    fun insert(category: Category) {
        viewModelScope.launch {
            categoryRepository.insert(category)
        }
    }

    fun update(category: Category) {
        viewModelScope.launch {
            categoryRepository.update(category)
        }
    }

    fun delete(category: Category) {
        viewModelScope.launch {
            categoryRepository.delete(category)
        }
    }
}