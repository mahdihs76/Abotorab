package ir.nilva.abotorab.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import ir.nilva.abotorab.db.model.DeliveryEntity

@Dao
interface DeliveryDao {

    @Query("SELECT * FROM delivery")
    fun getAll(): LiveData<List<DeliveryEntity>>

    @Insert
    suspend fun insert(items: List<DeliveryEntity>)

    @Delete
    suspend fun delete(item: DeliveryEntity)

    @Query("DELETE FROM delivery")
    suspend fun clear()

}