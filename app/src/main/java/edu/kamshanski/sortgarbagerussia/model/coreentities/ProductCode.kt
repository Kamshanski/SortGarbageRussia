package edu.kamshanski.sortgarbagerussia.model.coreentities

import edu.kamshanski.sortgarbagerussia.model.constants.BarcodeType

/**
 * Product code
 *
 * @property barcode - decrypted barcode
 * @property marker - type of barcode (QR_CODE, EAN_13, etc) to distinguish possible collisions of the same barcode
 * @constructor Create empty Product code
 */
class ProductCode(val barcode: String, val barcodeType: BarcodeType)