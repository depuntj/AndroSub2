package com.example.androsubmis2.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androsubmis2.models.FavoriteEventDao
import com.example.androsubmis2.models.FavoriteEventEntity
import kotlinx.coroutines.launch

class ViewFavoriteModel(private val favoriteEventDao: FavoriteEventDao) : ViewModel() {

    private val _errorMessage = MutableLiveData<String?>()

    fun addFavorite(event: FavoriteEventEntity) {
        viewModelScope.launch {
            try {
                favoriteEventDao.insertFavorite(event)
                _errorMessage.postValue(null)
            } catch (e: Exception) {
                _errorMessage.postValue("Failed to add to favorites: ${e.message}")
            }
        }
    }

    fun removeFavorite(event: FavoriteEventEntity) {
        viewModelScope.launch {
            try {
                favoriteEventDao.deleteFavorite(event)
                _errorMessage.postValue(null)
            } catch (e: Exception) {
                _errorMessage.postValue("Failed to remove from favorites: ${e.message}")
            }
        }
    }

    fun getFavoriteEventById(id: String): LiveData<FavoriteEventEntity?> {
        return favoriteEventDao.getFavoriteEventById(id)
    }

    fun getAllFavorites(): LiveData<List<FavoriteEventEntity>> {
        return favoriteEventDao.getAllFavorites()
    }
}
