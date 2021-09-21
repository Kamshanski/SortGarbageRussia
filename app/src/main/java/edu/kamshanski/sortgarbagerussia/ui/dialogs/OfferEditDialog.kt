package edu.kamshanski.sortgarbagerussia.ui.dialogs

import android.app.Application
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import edu.kamshanski.sortgarbagerussia.databinding.DialogFragmentOfferEditBinding
import edu.kamshanski.sortgarbagerussia.model.RecycleRepository
import edu.kamshanski.sortgarbagerussia.model.coreentities.RecycleOffer
import edu.kamshanski.sortgarbagerussia.ui.App
import edu.kamshanski.tpuclassschedule.utils.lg
import edu.kamshanski.tpuclassschedule.utils.string.isNotNullOrBlank
import edu.kamshanski.tpuclassschedule.utils.string.toStringOrEmpty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlin.contracts.ExperimentalContracts

@ExperimentalCoroutinesApi
@ExperimentalContracts
class OfferEditDialog(
//        dialogTitle: String,
//        val offerSample: RecycleInfoOffer,
//        val onCompletionListener: (RecycleInfoOffer) -> Unit
) : DialogFragment(), DialogInterface.OnClickListener{
    lateinit var binding: DialogFragmentOfferEditBinding
    val vm: OfferDialogViewModel by viewModels()
    val args by navArgs<OfferEditDialogArgs>()

    val inputFilter = object : InputFilter {
        val utf8_check = Charsets.UTF_8.newEncoder()
        override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence? {
            if (source.length == 0)
                return null // 0-length replacements, as this is what happens when you delete text

            if (utf8_check.canEncode(source.substring(start, end))) {
                return null // accept the original replacement
            }
            else {
                return ""
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val ctx = requireContext()
        val offerSample = args.offer

        if (offerSample == null) {
            lg("offerSample in OfferEditDialog was null. Dialog was dismissed")
            dismiss()
            return super.onCreateDialog(savedInstanceState)
        }

        binding = DialogFragmentOfferEditBinding.inflate(layoutInflater)

        // allow to input only utf8 chars. So no emoji
        val filters = arrayOf(inputFilter)
        arrayOf(binding.edtProductInfo, binding.edtUtilizeInfo, binding.edtProductName).forEach {
            it.filters = filters
        }

        with(binding) {
            if (offerSample.name.isNotNullOrBlank())
                edtProductName.setText(offerSample.name)
            txBarcode.text = offerSample.barcode
            txBarcodeFromat.text = offerSample.barcodeType.name
            if (offerSample.name.isNotNullOrBlank())
                edtProductInfo.setText(offerSample.productInfo)
            if (offerSample.name.isNotNullOrBlank())
                edtUtilizeInfo.setText(offerSample.utilizeInfo)
        }

        return AlertDialog.Builder(ctx).apply {
            setView(binding.root)
            setTitle(args.dialogTitle)
            setPositiveButton("Отправить", this@OfferEditDialog)
            setNegativeButton("Отмена", this@OfferEditDialog)
        }.create()
    }

    private fun collectInputOffer() = with(args.offer!!) {
        RecycleOffer(
            globalId = globalId,
            name = binding.edtProductName.text.toStringOrEmpty(),
            barcode = barcode,
            barcodeType = barcodeType,
            barcodeInfo = barcodeInfo,
            productInfo = binding.edtProductInfo.text.toStringOrEmpty(),
            utilizeInfo = binding.edtUtilizeInfo.text.toStringOrEmpty()
        )
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        when (which) {
            Dialog.BUTTON_POSITIVE -> vm.offer(collectInputOffer())
            else -> dismiss()
        }
    }



    class OfferDialogViewModel(app: Application) : AndroidViewModel(app) {
        private val repo = (app as App).repository

        fun offer(offer: RecycleOffer) {
            CoroutineScope(Dispatchers.Main.immediate).launch {
                repo.offerRecycleInfo(offer)
            }
        }
    }
}