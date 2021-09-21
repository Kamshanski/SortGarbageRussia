package edu.kamshanski.sortgarbagerussia.ui.main.saved

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.contracts.ExperimentalContracts

@ExperimentalCoroutinesApi
@ExperimentalContracts
class SavedPagerAdapter(val vm: SavedViewModel, parentFragment: Fragment) : FragmentStateAdapter(parentFragment) {
    val tabTitles = arrayOf("Bookmarks", "Offers")

    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        if (position == 0) {
            return BookmarksFragment(vm)
        } else {
            return OffersFragment(vm)
        }
    }
}