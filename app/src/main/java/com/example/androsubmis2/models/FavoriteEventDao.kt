package com.example.androsubmis2.models

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FavoriteEventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(event: FavoriteEventEntity)

    @Delete
    suspend fun deleteFavorite(event: FavoriteEventEntity)

    @Query("SELECT * FROM favorite_events WHERE id = :id")
    fun getFavoriteEventById(id: String): LiveData<FavoriteEventEntity?>

    @Query("SELECT * FROM favorite_events")
    fun getAllFavorites(): LiveData<List<FavoriteEventEntity>>
}
