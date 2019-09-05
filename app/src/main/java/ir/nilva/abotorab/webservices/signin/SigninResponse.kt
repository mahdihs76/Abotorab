package ir.nilva.abotorab.webservices.signin

import com.google.gson.annotations.SerializedName
import ir.nilva.abotorab.webservices.base.BaseResponse

class SigninResponse(@SerializedName("token") val token: String) : BaseResponse()