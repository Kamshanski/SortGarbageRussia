package edu.kamshanski.sortgarbagerussia.model

import kotlin.contracts.ExperimentalContracts

@ExperimentalContracts
interface RepositoryProvider {
    public val repository: RecycleRepository
}