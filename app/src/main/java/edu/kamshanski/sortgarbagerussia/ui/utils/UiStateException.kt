package edu.kamshanski.sortgarbagerussia.ui.utils

import edu.kamshanski.tpuclassschedule.utils.string.toStringOrEmpty

sealed class UiStateException private constructor(val message: String?) {
    val reason: String get() {
        val sb = StringBuilder()
        sb.append("UI ${this::class.simpleName} Error: " + message.toStringOrEmpty())
        return sb.toString()
    }

    class PERMISSION(val requiredPermissions: Array<String>, message: String? = null) : UiStateException(message)

    class LOADING(message: String?) : UiStateException(message)

    class CONNECTION(message: String?) : UiStateException(message)

    class UNPREDICTED(message: String?) : UiStateException(message)

}