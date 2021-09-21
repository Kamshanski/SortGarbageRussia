package edu.kamshanski.sortgarbagerussia.model.utils

class Result<T> private constructor(val value: T? = null,
                val state: LoadingProgress = LoadingProgress.LOADING,
                val error: Throwable? = null
) {
//    val isLoading get() = state == LoadingProgress.LOADING
//    val isFinished get() = state == LoadingProgress.SUCCESS || state == LoadingProgress.FAIL
    val requireValue get() = value!!
    val requireError get() = error!!
    companion object {
        fun <T> Loading() = Result<T>()

        fun <T> Success(value: T) = Result(value, LoadingProgress.SUCCESS)

        fun <T> Fail(error: Throwable) = Result<T>(state = LoadingProgress.FAIL, error = error)
    }
}

enum class LoadingProgress {
    LOADING, SUCCESS, FAIL
}