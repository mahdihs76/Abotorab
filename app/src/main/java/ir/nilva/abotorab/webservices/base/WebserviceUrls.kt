package ir.nilva.abotorab.webservices.base

import ir.nilva.abotorab.webservices.cabinet.CabinetResponse
import ir.nilva.abotorab.webservices.cabinet.ChangeStatusResponse
import ir.nilva.abotorab.webservices.delivery.DeliveryResponse
import ir.nilva.abotorab.webservices.report.ReportResponse
import ir.nilva.abotorab.webservices.signin.SigninResponse
import ir.nilva.abotorab.webservices.take.GiveResponse
import ir.nilva.abotorab.webservices.take.TakeResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface WebserviceUrls {

    @FormUrlEncoded
    @POST(WebserviceAdresses.SIGNIN)
    fun signin(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<SigninResponse>

    @FormUrlEncoded
    @POST(WebserviceAdresses.ACCOUNTING)
    fun accounting(
        @Field("first_name") firstName: String,
        @Field("last_name") lastName: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<BaseResponse>

    @FormUrlEncoded
    @POST(WebserviceAdresses.TAKE)
    fun take(
        @Field("first_name") firstName: String,
        @Field("last_name") lastName: String,
        @Field("phone") phoneNumber: String,
        @Field("country") country: String,
        @Field("passport_id") passportId: String,
        @Field("bag_count") bagCount: Int,
        @Field("suitcase_count") suitcaseCount: Int,
        @Field("pram_count") pramCount: Int
    ): Call<TakeResponse>


    @FormUrlEncoded
    @POST(WebserviceAdresses.GIVE)
    suspend fun give(@Field("hash_id") hashId: String): Response<GiveResponse>

    @FormUrlEncoded
    @POST(WebserviceAdresses.CABINET)
    suspend fun cabinet(
        @Field("code") code: String,
        @Field("num_of_rows") rows: Int,
        @Field("num_of_cols") columns: Int,
        @Field("size") size: Int,
        @Field("first_row_size") firstRowSize: Int
    ): Response<CabinetResponse>

    @FormUrlEncoded
    @POST(WebserviceAdresses.CHANGE_STATUS)
    suspend fun changeStatus(
        @Field("code") code: String,
        @Field("is_healthy") isHealthy: Boolean
    ): Response<ChangeStatusResponse>

    @GET(WebserviceAdresses.REPORT)
    fun report(): Call<ReportResponse>


    @GET(WebserviceAdresses.CABINET_LIST)
    suspend fun cabinetList(): Response<List<CabinetResponse>>

    @POST("cabinet/{code}/print")
    suspend fun printCabinet(@Path("code") code: Int): Response<BaseResponse>

    @GET("delivery/")
    suspend fun deliveryList(@Query("first_name") firstName: String,
                             @Query("last_name") lastName: String,
                             @Query("country") country: String,
                             @Query("phone") phone: String,
                             @Query("passport_id") passportId: String): Response<List<DeliveryResponse>>
}
