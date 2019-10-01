package ir.nilva.abotorab.webservices

import ir.nilva.abotorab.ApplicationContext
import ir.nilva.abotorab.helper.toastError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.UnknownHostException

fun getServices(): WebserviceUrls = MyRetrofit.getInstance().webserviceUrls

suspend fun <T> callWebserviceWithFailure(
    caller: suspend () -> Response<T>,
    failure: () -> Unit
): T? {
    return try {
        val response = caller()
        if (response.isSuccessful) {
            response.body()
        } else {
            failure()
            null
        }
    } catch (e: Exception) {
        handleException(e)
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

fun <T> Call<T>.call(success: ((T) -> Unit)? = null): Callback<T>? {
    try {
        val callback = object : Callback<T> {
            override fun onFailure(call: Call<T>, t: Throwable) {
                handleFailure(t)
            }

            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    CoroutineScope(Dispatchers.IO).launch {
                        success?.invoke(response.body()!!)
                    }
                } else {
                    handleError(response.code(), response.errorBody())
                }
            }
        }
        enqueue(callback)
        return callback
    } catch (e: java.lang.Exception) {
        handleException(e)
    }
    return null
}

private fun handleError(
    code: Int,
    errorBody: ResponseBody?
) {
    onFailed(WebServiceError(), errorBody?.string() ?: "")
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

fun onFailed(throwable: Throwable, errorBody: String) {
    val context = ApplicationContext.context
    when (throwable) {
        is UnknownHostException -> context.toastError("لطفا اتصال خود را بررسی کنید")
        is WebServiceError -> {
            context.toastError(errorBody)
        }
    }
}