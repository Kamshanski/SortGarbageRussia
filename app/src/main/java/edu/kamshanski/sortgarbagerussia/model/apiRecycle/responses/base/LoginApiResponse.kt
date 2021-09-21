package edu.kamshanski.sortgarbagerussia.model.apiRecycle.responses.base

open class LoginApiResponse(val login: String, error: String?) : BaseRecycleApiResponse(error)