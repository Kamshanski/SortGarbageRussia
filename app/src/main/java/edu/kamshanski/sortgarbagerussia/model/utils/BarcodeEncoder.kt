package edu.kamshanski.sortgarbagerussia.model.utils

import android.graphics.Bitmap
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import com.google.zxing.MultiFormatWriter
import edu.kamshanski.sortgarbagerussia.model.constants.BarcodeType

object BarcodeEncoder {
    // https://stackoverflow.com/a/46072757/11103179
    @Suppress("DEPRECATION") // It's cheaper to have each pixel stored on 2 bytes than on 4
    @JvmStatic
    fun encode(text: String, format: BarcodeType, width: Int, height: Int) : Bitmap {
        val bitMatrix = MultiFormatWriter().encode(text, format.zxingFormat, width, height)
        val bitMatrixWidth = bitMatrix.width
        val bitMatrixHeight = bitMatrix.height
        val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)

        for (y in 0 until bitMatrixHeight) {
            val offset = y * bitMatrixWidth
            for (x in 0 until bitMatrixWidth) {
                pixels[offset + x] = if (bitMatrix[x, y]) BLACK else WHITE
            }
        }

        val bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444)

        bitmap.setPixels(pixels, 0, width, 0, 0, bitMatrixWidth, bitMatrixHeight)

        return bitmap
    }
}