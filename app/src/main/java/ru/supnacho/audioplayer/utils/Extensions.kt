package ru.supnacho.audioplayer.utils

import android.util.Log
import android.view.View
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