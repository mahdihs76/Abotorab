package ir.nilva.abotorab.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "delivery")
data class DeliveryEntity(
    val nickname: String,
    val country: String,
    val phone: String,
    @PrimaryKey
    val hashId: String,
    val exitedAt: String
)