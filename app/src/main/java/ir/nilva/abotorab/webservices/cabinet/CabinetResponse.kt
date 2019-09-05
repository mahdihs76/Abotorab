package ir.nilva.abotorab.webservices.cabinet

import com.google.gson.annotations.SerializedName
import ir.nilva.abotorab.webservices.base.BaseResponse
import ir.nilva.abotorab.webservices.model.Row
import java.io.Serializable

class CabinetResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("rows") val rows: ArrayList<Row>
    ) : BaseResponse(), Serializable