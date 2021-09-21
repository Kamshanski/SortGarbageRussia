package edu.kamshanski.sortgarbagerussia.ui.utils

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import edu.kamshanski.sortgarbagerussia.R

inline fun Fragment.getDrawable(@DrawableRes id: Int) : Drawable? {
    return ResourcesCompat.getDrawable(resources, id, requireContext().theme)
}

inline fun Fragment.getDrawableColor(@ColorRes id: Int) : Drawable? {
    return ColorDrawable(getColor(id))
}

@ColorInt
inline fun Fragment.getColor(@ColorRes id: Int) : Int {
    return resources.getColor(id, context?.theme)
}