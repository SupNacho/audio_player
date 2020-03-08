package ru.supnacho.audioplayer.utils

import android.content.Context
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import ru.supnacho.audioplayer.BuildConfig
import java.io.File


fun safeLog(tag: String, message: String) {
    if (BuildConfig.DEBUG) Log.d(tag, message)
}

fun <T> Single<T>.subscribeAndTrack(
    subscriptionsHolder: CompositeDisposable,
    onError: (Throwable) -> Unit = {},
    onSuccess: (T) -> Unit
) {
    subscriptionsHolder.add(this.subscribe(onSuccess, onError))
}
fun <T> Observable<T>.subscribeAndTrack(
    subscriptionsHolder: CompositeDisposable,
    onError: (Throwable) -> Unit = {},
    onSuccess: (T) -> Unit
) {
    subscriptionsHolder.add(this.subscribe(onSuccess, onError))
}

fun View.setVisibility(isVisible: Boolean) {
    this.visibility = if (isVisible) View.VISIBLE else View.GONE
}

fun String.toFile() = File(this)

fun Context?.showTwoButtonDialog(
    message: String,
    isCancellable: Boolean = true,
    positiveButtonText: String,
    negativeButtonText: String,
    onPositiveClickListener: (() -> Unit)? = null,
    onNegativeClickListener: (() -> Unit)? = null,
    title: String? = null
) {
    this?.let {
        AlertDialog.Builder(it).apply {
            setMessage(message)
            title?.let { setTitle(title) }
            setPositiveButton(positiveButtonText) { dialog, _ ->
                onPositiveClickListener?.invoke() ?: dialog.dismiss()
            }
            setNegativeButton(negativeButtonText) { dialog, _ ->
                onNegativeClickListener?.invoke() ?: dialog.dismiss()
            }
            setCancelable(isCancellable)
        }.create().show()
    }
}

