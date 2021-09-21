package edu.kamshanski.sortgarbagerussia.model.apiRecycle.entities

class User(val login: String, val userId: Int) {
    val isAuthenticated: Boolean
        get() = userId > 0
}