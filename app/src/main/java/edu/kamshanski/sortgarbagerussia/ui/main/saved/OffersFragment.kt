package edu.kamshanski.sortgarbagerussia.ui.main.saved

import android.net.Uri
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import edu.kamshanski.sortgarbagerussia.R
import edu.kamshanski.sortgarbagerussia.databinding.ItemOfferBinding
import edu.kamshanski.sortgarbagerussia.model.apiRecycle.responses.GetLocalOffersResponse
import edu.kamshanski.sortgarbagerussia.model.constants.OfferStatus.UNSTUDIED
import edu.kamshanski.sortgarbagerussia.model.utils.wrappers.Failure
import edu.kamshanski.sortgarbagerussia.model.utils.wrappers.InProgress
import edu.kamshanski.sortgarbagerussia.model.utils.wrappers.Progress
import edu.kamshanski.sortgarbagerussia.model.utils.wrappers.Success
import edu.kamshanski.sortgarbagerussia.ui._abstract.CommonRecyclerViewFragment
import edu.kamshanski.sortgarbagerussia.ui.utils.getDrawable
import edu.kamshanski.sortgarbagerussia.utils.nice_classes.letEvery
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlin.contracts.ExperimentalContracts

@ExperimentalCoroutinesApi
@ExperimentalContracts
class OffersFragment(val vm: SavedViewModel) : CommonRecyclerViewFragment<ItemOfferBinding>() {
    lateinit var offerFlow: StateFlow<Progress<GetLocalOffersResponse>>
    override fun initViewModel() {
        repeat {
            offerFlow = vm.getOffers()
            offerFlow.collect {
                when (it) {
                    is Failure -> throw it.error ?: UnknownError("initviewmodel repeat failure")
                    is InProgress -> {
                        emptyViewOn("Loading...")
                    }
                    is Success-> {
                        notifyDataSetChanged()
                        if (getItemCount() == 0)
                            emptyViewOn()
                        else
                            emptyViewOff()
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder<ItemOfferBinding> {
        val binding = ItemOfferBinding.inflate(layoutInflater, parent, false)
        val holder = CommonViewHolder(binding)
        return holder
    }

    override fun onBindViewHolder(holder: CommonViewHolder<ItemOfferBinding>, position: Int) {
        with(holder.binding) {
            val offer = offerFlow.value.value!!.offers[position]
            txName.text = offer.name
            txBarcode.text = "Barcode ${offer.barcode} (${offer.barcodeType.serverFormat})"

            if (offer.productInfo.isNotBlank()) {
                txProductInfo.text = offer.productInfo
                txProductInfo.visibility = VISIBLE
                txProductInfoLabel.visibility = VISIBLE
            }

            if (offer.utilizeInfo.isNotBlank()) {
                txUtilizeInfo.text = offer.productInfo
                txUtilizeInfo.visibility = VISIBLE
                txUtilizeInfoLabel.visibility = VISIBLE
            }

            when {
                offer.progressStatus != UNSTUDIED -> "Considered" to R.drawable.ic_check
                else ->                              "Sent"       to R.drawable.ic_sent
            }.letEvery { text, drawable ->
                txProcessStatus.text = text
                imgProcessStatus.setImageDrawable(getDrawable(drawable))
            }

            if (offer.imagePath.isEmpty()) {
                imgOffer.visibility = GONE
            } else {
                imgOffer.visibility = VISIBLE
                imgOffer.setImageURI(Uri.parse(offer.imagePath))
            }

            imbDelete.setOnClickListener {
                vm.deleteOffer(offer)
            }
        }
    }

    override fun getItemCount(): Int {
        if (this::offerFlow.isInitialized) {
            return offerFlow.value.value?.offers?.size ?: 0
        } else {
            return 0
        }
    }
}