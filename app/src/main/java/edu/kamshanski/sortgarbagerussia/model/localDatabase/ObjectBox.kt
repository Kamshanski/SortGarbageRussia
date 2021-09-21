package edu.kamshanski.sortgarbagerussia.model.localDatabase

import android.content.Context
import edu.kamshanski.sortgarbagerussia.model.localDatabase.entities.MyObjectBox
import io.objectbox.BoxStore
import io.objectbox.exception.DbException

object ObjectBox {
    private lateinit var _mainStore: BoxStore
    private lateinit var _tempWebStore: BoxStore

    val tempWebStore: BoxStore
        get() = if (this::_tempWebStore.isInitialized) _tempWebStore
                else throw UninitializedPropertyAccessException(
                        "${this::_tempWebStore.name} is not initialized")

    val mainStore: BoxStore
        get() = if (this::_mainStore.isInitialized) _mainStore
                else throw UninitializedPropertyAccessException(
                        "${this::_mainStore.name} is not initialized")

    fun init(context: Context) {
        try {
            _mainStore = MyObjectBox.builder()
                .name("mainStore")
                .androidContext(context.applicationContext)
                .build()
            _tempWebStore = MyObjectBox.builder()
                    .name("tempWebStore")
                    .androidContext(context.applicationContext)
                    .build()
        } catch (e: DbException) {
            throw e
        }

    }
}