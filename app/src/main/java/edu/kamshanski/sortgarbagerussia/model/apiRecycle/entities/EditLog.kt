package edu.kamshanski.sortgarbagerussia.model.apiRecycle.entities

import java.util.*

class EditLog : TreeMap<GregorianCalendar, String>() {
    val last: EditRecord?
        get() = this.lastEntry()?.let { EditRecord(it.key, it.value!!) }

    class EditRecord(val time: GregorianCalendar, val payload: String)
}