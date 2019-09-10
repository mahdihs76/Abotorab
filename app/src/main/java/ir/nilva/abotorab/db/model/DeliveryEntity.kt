package ir.nilva.abotorab.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "delivery")
data class DeliveryEntity(
    val nickname: String,
    @PrimaryKey
    val hashId: String,
    val exitedAt: String
)