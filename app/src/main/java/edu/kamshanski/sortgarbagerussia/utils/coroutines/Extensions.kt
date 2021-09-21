package edu.kamshanski.sortgarbagerussia.utils.coroutines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

public fun CoroutineScope.oneShot(block: suspend () -> Unit) : Job {
    return launch {
        block()
        cancel()
    }
}

public fun ViewModel.launchOneShot(block: suspend () -> Unit) : Job {
    return viewModelScope.oneShot(block)
}

public suspend fun <T> CoroutineScope.launchForResult(block: suspend CoroutineScope.() -> T) : T {
    return async(block = block).await()
}
