package edu.kamshanski.sortgarbagerussia.ui.main

import androidx.activity.viewModels
import androidx.navigation.findNavController
import edu.kamshanski.sortgarbagerussia.R
import edu.kamshanski.sortgarbagerussia.databinding.ActivityMainBinding
import edu.kamshanski.tpuclassschedule.activities._abstract.BaseAppCompatActivity

class MainActivity : BaseAppCompatActivity() {
    val vm: MainViewModel by viewModels()
    public lateinit var binding: ActivityMainBinding

    override fun initViews() {
        binding.navigationBar.selectedItemId = R.id.menuRecycle

        binding.navigationBar.apply {
            itemIconSize = (itemIconSize * 5) / 3
            setOnItemSelectedListener { item ->
                val navDest = when(item.itemId) {
                    R.id.menuRecycle -> R.id.recycleScanFragment
                    R.id.menuBookmark -> R.id.bookmarksFragment
                    R.id.menuArticles -> R.id.readArticleMenuFragment
                    R.id.menuMap -> R.id.mapFragment
                    R.id.menuSettings -> R.id.settingsFragment
                    else -> return@setOnItemSelectedListener false
                }
                val mainNavController = findNavController(R.id.navFragment)
                val navigatedSuccessfully = mainNavController.popBackStack(navDest, false)
                if (!navigatedSuccessfully) {
                    mainNavController.navigate(navDest)
                }
                return@setOnItemSelectedListener true
            }

        }
    }
}