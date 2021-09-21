package edu.kamshanski.tpuclassschedule.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/*

@OptIn(ExperimentalTypeInference::class)
public fun <T> liveData(
    context: CoroutineContext = EmptyCoroutineContext,
    timeoutInMs: Long = DEFAULT_TIMEOUT,
    @BuilderInference block: suspend LiveDataScope<T>.() -> Unit
): LiveData<T> = CoroutineLiveData(context, timeoutInMs, block)
* */
//fun <T> resourceLiveData(
//    context: CoroutineContext = Dispatchers.IO,
//    timeoutInMs: Long = 20000L,
//    block: suspend () -> T) : LiveData<Resource<T>> = liveData(context, timeoutInMs) {
//        emit(Resource.loading(data = null))
//        try {
//            emit(Resource.success(data = block()))
//        } catch (ex: Exception) {
//            emit(Resource.error(null, ex.message ?: "Error: $ex", ex))
//        }
//}


// Вспомогательные методы для LiveData

/**
 * Изменяет значение внутри массива или объекта из [LiveData], после чего оповещает всех подписчиков
 * @param usePostNotifier - false - синхронное [LiveData.setValue]. true - асинхронное [LiveData.postValue]
 * @param execute - выражение, которое позволяет произвести действия над [LiveData.getValue] перед оповещением
 */
fun <T> MutableLiveData<T>.notify(execute: (T?.(T) -> Unit)? = null) {
    if (execute != null) {
        val v = value!!
        execute(v, v)
    }
    this.setValue(value)
}

fun <T> MutableLiveData<T>.notifyAsync(execute: (T.(T) -> Unit)? = null) {
    if (execute != null) {
        val v = value!!
        execute(v, v)
    }
    this.postValue(value)
}

/**
 * Изменяет значение из [LiveData] с помощью [change], заносит результат в [LiveData], оповещая всех подписчиков
 * @param usePostNotifier - false - синхронное [LiveData.setValue]. true - асинхронное [LiveData.postValue]
 * @param change - выражение, которое позволяет произвести действия над [LiveData.getValue] и
 * возвращает результат, который заносится в [LiveData]
 */
fun <T> MutableLiveData<T?>.notifyOnChange(change: (T?.() -> T)?) {
    val newValue = if (change != null) change(value) else value
    this.setValue(newValue)
}

fun <T> MutableLiveData<T>.notifyOnChangeAsync(change: (T?.(T?) -> T)?) {
    val newValue = if (change != null) change(value, value) else value!!
    this.postValue(newValue)
}

inline fun <T> MutableLiveData<T>.isNull() : Boolean = value == null
inline fun <T> MutableLiveData<T>.isNotNull() : Boolean = value != null