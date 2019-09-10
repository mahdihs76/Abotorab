package ir.nilva.abotorab.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ir.nilva.abotorab.model.CabinetResponse

@Dao
interface CabinetDao {

    @Query("SELECT * FROM cabinetresponse")
    fun getAll(): LiveData<List<CabinetResponse>>

    @Query("SELECT * FROM cabinetresponse WHERE code=:code")
    fun get(code: Int): LiveData<CabinetResponse>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(items: List<CabinetResponse>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CabinetResponse)

    @Query("DELETE FROM delivery")
    suspend fun clear()
}