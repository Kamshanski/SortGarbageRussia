package edu.kamshanski.sortgarbagerussia.ui.main.saved

import android.view.ViewGroup
import edu.kamshanski.sortgarbagerussia.databinding.ItemBookmarksBinding
import edu.kamshanski.sortgarbagerussia.databinding.ItemOfferBinding
import edu.kamshanski.sortgarbagerussia.ui._abstract.CommonRecyclerViewFragment
import kotlin.contracts.ExperimentalContracts

@ExperimentalContracts
class BookmarksFragment(vm: SavedViewModel) : CommonRecyclerViewFragment<ItemBookmarksBinding>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder<ItemBookmarksBinding> {
        val binding = ItemBookmarksBinding.inflate(layoutInflater, parent, false)
        val holder = CommonViewHolder(binding)
        return holder
    }

    override fun onBindViewHolder(holder: CommonViewHolder<ItemBookmarksBinding>, position: Int) {

    }

    override fun getItemCount(): Int {
        return 0
    }

}