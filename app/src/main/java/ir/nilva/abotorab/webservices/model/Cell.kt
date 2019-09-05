package ir.nilva.abotorab.webservices.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Cell(
    @SerializedName("id") val id: Int,
    @SerializedName("code") val code: String,
    @SerializedName("age") val age: Int,
    @SerializedName("is_healthy") var isHealthy: Boolean,
    @SerializedName("size") val size: Int,
    @SerializedName("row") val row: Int
) : Serializable