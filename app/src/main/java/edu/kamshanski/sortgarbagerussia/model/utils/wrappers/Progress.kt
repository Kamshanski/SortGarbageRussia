package edu.kamshanski.sortgarbagerussia.model.utils.wrappers

sealed class Progress<T>(val value: T? = null, val error: Throwable? = null)
    class InProgress<T>(value: T? = null) : Progress<T>(value, null)
    class Success<T>(value: T) : Progress<T>(value, null)
    class Failure<T>(error: Throwable) : Progress<T>(null, error)

