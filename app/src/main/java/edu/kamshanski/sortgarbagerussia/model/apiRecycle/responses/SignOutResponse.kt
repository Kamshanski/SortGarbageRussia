package edu.kamshanski.sortgarbagerussia.model.apiRecycle.responses

import edu.kamshanski.sortgarbagerussia.model.apiRecycle.responses.base.LoginApiResponse

class SignOutResponse(
        login: String,
        val userId: Int,
        error: String? = null
) : LoginApiResponse (login, error){
}