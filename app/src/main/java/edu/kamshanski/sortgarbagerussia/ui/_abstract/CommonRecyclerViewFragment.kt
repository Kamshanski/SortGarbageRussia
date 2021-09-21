package edu.kamshanski.sortgarbagerussia.ui._abstract

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import edu.kamshanski.sortgarbagerussia.databinding.CommonFragmentRecyclerViewBinding
import edu.kamshanski.tpuclassschedule.activities._abstract.BaseFragment

abstract class CommonRecyclerViewFragment<VB: ViewBinding>(
) : BaseFragment() {

    public abstract fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : CommonViewHolder<VB>
    public abstract fun onBindViewHolder(holder: CommonViewHolder<VB>, position: Int)
    public abstract fun getItemCount() : Int
    public fun notifyDataSetChanged() = binding.recyclerView.adapter!!.notifyDataSetChanged()
    lateinit var binding: CommonFragmentRecyclerViewBinding

    override fun initViews() {
        binding.recyclerView.adapter = CommonRecyclerViewAdapter()
    }

    fun emptyViewOn(text: String = "Empty Recycler View") {
        binding.txEmptyRecycleViewNotification.visibility = View.VISIBLE
        binding.txEmptyRecycleViewNotification.text = text
        binding.recyclerView.visibility = View.GONE
    }

    fun emptyViewOff() {
        binding.txEmptyRecycleViewNotification.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
    }

    inner class CommonRecyclerViewAdapter : RecyclerView.Adapter<CommonViewHolder<VB>>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder<VB> =
                this@CommonRecyclerViewFragment.onCreateViewHolder(parent, viewType)

        override fun onBindViewHolder(holder: CommonViewHolder<VB>, position: Int) =
                this@CommonRecyclerViewFragment.onBindViewHolder(holder, position)


        override fun getItemCount() =
                this@CommonRecyclerViewFragment.getItemCount()

    }

    open class CommonViewHolder<VB: ViewBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root)




}