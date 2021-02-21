package ir.nilva.abotorab.webservices

import ir.nilva.abotorab.ApplicationContext
import ir.nilva.abotorab.helper.toastError
import okhttp3.ResponseBody
import org.jetbrains.anko.runOnUiThread
import org.json.JSONObject
import retrofit2.Response
import java.net.UnknownHostException

fun getServices(): WebserviceUrls = MyRetrofit.getInstance().webserviceUrls

suspend fun <T> callWebserviceWithFailure(
    caller: suspend () -> Response<T>,
    failure: (response: String, code: Int?) -> Unit
): T? {
    return try {
        val response = caller()
        if (response.isSuccessful) {
            response.body()
        } else {
//            var errorResponse: Failure? = gson.fromJson(response.errorBody()!!.charStream(), type)
            val jsonErr = response.errorBody()?.string()
            val errorMessage =
                JSONObject(jsonErr!!).getJSONArray("non_field_errors").get(0)?.toString()
                    ?: "مشکلی پیش آمده است"

            if (response.code() == 400) {
                ApplicationContext.context.toastError(
                    errorMessage
                )
            }
            failure(errorMessage, response.code())
            null
        }
    } catch (e: Exception) {
        failure("متاسفانه ارتباط برقرار نشد", -1)
        null
    }
}

suspend fun <T> callWebservice(
    caller: suspend () -> Response<T>
): T? {
    return try {
        val response = caller()
        if (response.isSuccessful) {
            response.body()
        } else {
            handleError(response.code(), response.errorBody())
            null
        }
    } catch (e: Exception) {
        handleFailure(e)
        null
    }
}

private fun handleError(
    code: Int,
    errorBody: ResponseBody?
) {
    onFailed(WebServiceError(), errorBody?.string() ?: "", code)
}

private fun handleFailure(t: Throwable) {
    if (t is UnknownHostException) {
        onFailed(t, "اتصال خود را بررسی کنید")
    } else {
        onFailed(t, t.message.toString())
    }
}

private fun handleException(e: java.lang.Exception) {
    onFailed(e, e.message.toString())
}

fun onFailed(throwable: Throwable, errorBody: String, errorCode: Int = -1) {
    val context = ApplicationContext.context
    when (throwable) {
        is UnknownHostException -> context.runOnUiThread {
            context.toastError("لطفا اتصال خود را بررسی کنید")
        }
        is WebServiceError -> {
            if (errorCode == 403) {
                context.toastError("شما دسترسی لازم را ندارید")
            } else {
                context.toastError(errorBody)
            }
        }
    }
}