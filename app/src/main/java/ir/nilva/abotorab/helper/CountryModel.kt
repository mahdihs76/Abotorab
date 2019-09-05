package ir.nilva.abotorab.helper

import com.google.gson.annotations.SerializedName

data class CountryModel(@SerializedName("num_code") val code: String,
                        @SerializedName("alpha_2_code") val alpha2Code: String,
                        @SerializedName("alpha_3_code") val alpha3Code: String,
                        @SerializedName("en_short_name") val shortName: String,
                        @SerializedName("nationality") val nationality: String)
