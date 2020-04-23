package ir.nilva.abotorab.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ir.nilva.abotorab.model.CabinetResponse

@Dao
interface CabinetDao {

    @Query("SELECT * FROM cabinetresponse")
    fun getAll(): LiveData<List<CabinetResponse>>

    @Query("SELECT * FROM cabinetresponse")
    suspend fun getCabinets(): List<CabinetResponse>

    @Query("SELECT * FROM cabinetresponse WHERE code=:code")
    fun getLiveData(code: Int): LiveData<CabinetResponse>

    @Query("SELECT * FROM cabinetresponse WHERE code=:code")
    suspend fun get(code: Int): CabinetResponse

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(items: List<CabinetResponse>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CabinetResponse)

    @Query("DELETE FROM cabinetresponse WHERE code=:code")
    suspend fun delete(code: Int)

    @Query("DELETE FROM cabinetresponse")
    suspend fun clear()

    @Update
    suspend fun update(item: CabinetResponse)
}