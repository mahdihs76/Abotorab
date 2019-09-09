package ir.nilva.abotorab.helper

import android.app.Activity
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.irozon.sneaker.Sneaker
import ir.nilva.abotorab.R

fun Activity.toastError(text:String) = Sneaker.with(this)
        .setTitle(getString(R.string.error))
        .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
        .setMessage(text)
        .setTypeface(getAppTypeface()).sneakWarning()

fun Fragment.toastError(text: String) = activity?.toastError(text)

fun Fragment.toastSuccess(text: String) = activity?.toastSuccess(text)

fun Activity.toastSuccess(text:String) = Sneaker.with(this)
        .setTitle(getString(R.string.successful))
        .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
        .setMessage(text)
        .setTypeface(getAppTypeface()).sneakSuccess()
