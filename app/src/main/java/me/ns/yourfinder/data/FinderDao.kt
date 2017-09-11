package me.ns.yourfinder.data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

/**
 * App Dao
 * Created by shintaro.nosaka on 2017/09/11.
 */
@Dao
interface FinderDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(value: Finder)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(value: Finder)

    @Update
    fun update(value: Finder)

    @Query("SELECT * FROM Finder")
    fun allLiveData(): LiveData<List<Finder>>

    @Query("SELECT * FROM Finder")
    fun all(): List<Finder>

    @Query("SELECT * FROM Finder WHERE id = :id LIMIT 1")
    fun findLiveData(id: Int): LiveData<Finder>?

    @Query("SELECT * FROM Finder WHERE id = :id LIMIT 1")
    fun find(id: Int): Finder?


}