package se.umu.cs.phjo0015.mapapplication.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

/**
 * Only observe once, here we can read more:
 * https://stackoverflow.com/questions/47854598/livedata-remove-observer-after-first-callback
 */
fun <T> LiveData<T>.observeOnce(
    lifecycleOwner: LifecycleOwner,
    observer: Observer<T>
) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(value: T) {
            observer.onChanged(value)
            removeObserver(this)
        }
    })
}