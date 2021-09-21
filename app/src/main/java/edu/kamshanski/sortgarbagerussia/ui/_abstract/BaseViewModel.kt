package edu.kamshanski.tpuclassschedule.activities._abstract

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

open class BaseViewModel (app: Application): AndroidViewModel(app) {
    // for simple IO operations
    fun <P> inIO(doOnAsyncBlock: suspend CoroutineScope.() -> P) {
        executeCoroutine(doOnAsyncBlock, viewModelScope, Dispatchers.IO)
    }

    // for easiest operations or ui operations
    fun <P> inMain(doOnAsyncBlock: suspend CoroutineScope.() -> P) {
        executeCoroutine(doOnAsyncBlock, viewModelScope, Dispatchers.Main)
    }

    // for complex computations
    fun <P> inDefault(doOnAsyncBlock: suspend CoroutineScope.() -> P) {
        executeCoroutine(doOnAsyncBlock, viewModelScope, Dispatchers.Default)
    }

    private inline fun <P> executeCoroutine(
        crossinline doOnAsyncBlock: suspend CoroutineScope.() -> P,
        coroutineScope: CoroutineScope,
        context: CoroutineContext
    ) {
        coroutineScope.launch {
            withContext(context) {
                doOnAsyncBlock.invoke(this)
            }
        }
    }
}