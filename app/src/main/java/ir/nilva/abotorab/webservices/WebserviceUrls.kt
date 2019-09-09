package ir.nilva.abotorab.webservices

import ir.nilva.abotorab.model.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface WebserviceUrls {

    @FormUrlEncoded
    @POST("signin/")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<SigninResponse>

    @FormUrlEncoded
    @POST("accounting/")
    fun accounting(
        @Field("first_name") firstName: String,
        @Field("last_name") lastName: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<BaseResponse>

    @FormUrlEncoded
    @POST("reception/take/")
    suspend fun take(
        @Field("first_name") firstName: String,
        @Field("last_name") lastName: String,
        @Field("phone") phoneNumber: String,
        @Field("country") country: String,
        @Field("passport_id") passportId: String,
        @Field("bag_count") bagCount: Int,
        @Field("suitcase_count") suitcaseCount: Int,
        @Field("pram_count") pramCount: Int
    ): Response<TakeResponse>

    @FormUrlEncoded
    @POST("reception/give/")
    suspend fun give(
        @Field("hash_id") hashId: String
    ): Response<GiveResponse>

    @FormUrlEncoded
    @POST("cabinet/")
    suspend fun cabinet(
        @Field("code") code: String,
        @Field("num_of_rows") rows: Int,
        @Field("num_of_cols") columns: Int,
        @Field("size") size: Int,
        @Field("first_row_size") firstRowSize: Int
    ): Response<CabinetResponse>

    @FormUrlEncoded
    @POST("cell/change_status/")
    suspend fun changeStatus(
        @Field("code") code: String,
        @Field("is_healthy") isHealthy: Boolean
    ): Response<ChangeStatusResponse>

    @GET("report/")
    suspend fun report(): Response<ReportResponse>

    @GET("cabinet/")
    suspend fun cabinetList(): Response<List<CabinetResponse>>

    @POST("cabinet/{code}/print/")
    suspend fun printCabinet(@Path("code") code: Int): Response<BaseResponse>

    @GET("delivery/")
    suspend fun deliveryList(
        @Query("first_name") firstName: String,
        @Query("last_name") lastName: String,
        @Query("country") country: String,
        @Query("phone") phone: String,
        @Query("passport_id") passportId: String
    ): Response<List<DeliveryResponse>>
}
