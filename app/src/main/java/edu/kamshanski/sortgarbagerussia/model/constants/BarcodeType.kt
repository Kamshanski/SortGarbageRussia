package edu.kamshanski.sortgarbagerussia.model.constants

import com.google.mlkit.vision.barcode.Barcode
import com.google.zxing.BarcodeFormat

/** Resolves barcode types naming for ML Kit, ZXing and this app */
enum class BarcodeType(
    @Barcode.BarcodeFormat val mlKitFormat: Int,
    val zxingFormat: BarcodeFormat?,
    val serverFormat: String
) {
    UNKNOWN     (0                          , null                      , "UNKNOWN    ".trim()),//    Reserve for incorrect barcode scanning
    CODE_128    (Barcode.FORMAT_CODE_128    , BarcodeFormat.CODE_128    , "CODE_128   ".trim()),//    Code 128 (FORMAT_CODE_128)
    CODE_39     (Barcode.FORMAT_CODE_39     , BarcodeFormat.CODE_39     , "CODE_39    ".trim()),//    Code 39  (FORMAT_CODE_39)
    CODE_93     (Barcode.FORMAT_CODE_93     , BarcodeFormat.CODE_93     , "CODE_93    ".trim()),//    Code 93  (FORMAT_CODE_93)
    CODABAR     (Barcode.FORMAT_CODABAR     , BarcodeFormat.CODABAR     , "CODABAR    ".trim()),//    Codabar  (FORMAT_CODABAR)
    EAN_13      (Barcode.FORMAT_EAN_13      , BarcodeFormat.EAN_13      , "EAN_13     ".trim()),//    EAN-13   (FORMAT_EAN_13)
    EAN_8       (Barcode.FORMAT_EAN_8       , BarcodeFormat.EAN_8       , "EAN_8      ".trim()),//    EAN-8    (FORMAT_EAN_8)
    ITF         (Barcode.FORMAT_ITF         , BarcodeFormat.ITF         , "ITF        ".trim()),//    ITF      (FORMAT_ITF)
    UPC_A       (Barcode.FORMAT_UPC_A       , BarcodeFormat.UPC_A       , "UPC_A      ".trim()),//    UPC-A    (FORMAT_UPC_A)
    UPC_E       (Barcode.FORMAT_UPC_E       , BarcodeFormat.UPC_E       , "UPC_E      ".trim()),//    UPC-E    (FORMAT_UPC_E)
    QR_CODE     (Barcode.FORMAT_QR_CODE     , BarcodeFormat.QR_CODE     , "QR_CODE    ".trim()),//    QR Code  (FORMAT_QR_CODE)
    PDF417      (Barcode.FORMAT_PDF417      , BarcodeFormat.PDF_417     , "PDF_417    ".trim()),//    PDF417   (FORMAT_PDF417)
    AZTEC       (Barcode.FORMAT_AZTEC       , BarcodeFormat.AZTEC       , "AZTEC      ".trim()),//    Aztec    (FORMAT_AZTEC)
    DATA_MATRIX (Barcode.FORMAT_DATA_MATRIX , BarcodeFormat.DATA_MATRIX , "DATA_MATRIX".trim()),//    Data Matrix (FORMAT_DATA_MATRIX)
    ;

    companion object {
        fun byMlKit(@Barcode.BarcodeFormat format: Int) : BarcodeType = values().find { it.mlKitFormat == format } ?: UNKNOWN
        fun byZxing(format: BarcodeFormat) : BarcodeType = values().find { it.zxingFormat == format } ?: UNKNOWN
        fun byServerId(serverId: String) : BarcodeType = values().find { it.serverFormat == serverId } ?: UNKNOWN
    }
}