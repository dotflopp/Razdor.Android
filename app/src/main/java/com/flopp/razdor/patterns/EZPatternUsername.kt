package com.flopp.razdor.patterns

import android.util.Patterns

class EZPatternUsername: EZPattern {
    override fun matchesPattern(
        input: String
    ) = input.length >= 2

}