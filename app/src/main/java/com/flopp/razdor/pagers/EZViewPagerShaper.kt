package com.flopp.razdor.pagers

import androidx.viewpager2.widget.ViewPager2
import good.damn.editor.importer.VEViewAVS
import good.damn.ui.UIViewShaper

class EZViewPagerShaper(
    private val viewPager: ViewPager2,
    private val shaper: UIViewShaper
) {

    var currentItem: Int
        get() = viewPager.currentItem
        set(v) {
            viewPager.currentItem = v
            shaper.animateChange()
        }

    fun pathAnimationDefault() {
        shaper.setupPathAnimation(
            0.0f,
            1.0f
        )
    }

    fun pathAnimationReverse() {
        shaper.setupPathAnimation(
            1.0f,
            0.0f
        )
    }

    fun prepareAnimation(
        anims: Array<Pair<Any, Any>>
    ) {
        shaper.shapes?.forEachIndexed {
            i, it ->
            it.prepareAnimation(
                anims[i].first,
                anims[i].second
            )
        }
    }


}