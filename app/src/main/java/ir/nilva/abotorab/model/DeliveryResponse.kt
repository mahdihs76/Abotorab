package ir.nilva.abotorab.model

import com.google.gson.annotations.SerializedName

data class DeliveryResponse(
    @SerializedName("pilgrim") val pilgrim: String,
    @SerializedName("taker") val taker: Int,
    @SerializedName("giver") val giver: Int,
    @SerializedName("hash_id") val hashId: String,
    @SerializedName("entered_at") val enteredAt: String,
    @SerializedName("exited_at") val exitAt: String,
    @SerializedName("exit_type") val exitType: Int,
    @SerializedName("pack") val pack: ArrayList<Pack>
)

data class Pack(
    @SerializedName("id") val id: Int,
    @SerializedName("bag_count") val bagCount: Int,
    @SerializedName("suitcase_count") val suitcaseCount: Int,
    @SerializedName("pram_count") val pramCount: Int,
    @SerializedName("delivery") val delivery: Int,
    @SerializedName("cell") val cell: Int
)