package edu.kamshanski.sortgarbagerussia.ui.main.saved

import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import edu.kamshanski.sortgarbagerussia.databinding.FragmentSavedBinding
import edu.kamshanski.tpuclassschedule.activities._abstract.BaseFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.contracts.ExperimentalContracts

@ExperimentalCoroutinesApi
@ExperimentalContracts
class SavedFragment: BaseFragment() {
    lateinit var binding: FragmentSavedBinding
    private val vm: SavedViewModel by viewModels()

    lateinit var pagerAdapter: SavedPagerAdapter
    lateinit var tabLayoutMediator: TabLayoutMediator

    override fun initViews() {
        pagerAdapter = SavedPagerAdapter(vm, this)
        binding.pager.adapter = pagerAdapter
    }

    override fun initListeners() {
        tabLayoutMediator = TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            tab.text = pagerAdapter.tabTitles[position]
        }
        tabLayoutMediator.attach()
    }


}