package edu.kamshanski.sortgarbagerussia.model.utils

import io.objectbox.Box
import io.objectbox.Property
import io.objectbox.query.Query

public fun <T> Query<T>.isEmpty() = count() == 0L

public fun <T> Query<T>.isNotEmpty() = count() > 0L

public fun <T> Box<T>.greatest(property: Property<T>) : T? {
    return this.query().orderDesc(property).build().find(0, 1).firstOrNull()
}
