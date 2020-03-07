package ru.supnacho.audioplayer.utils

import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.core.view.ViewCompat
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import ru.supnacho.audioplayer.BuildConfig
import java.math.BigDecimal
import java.math.RoundingMode


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