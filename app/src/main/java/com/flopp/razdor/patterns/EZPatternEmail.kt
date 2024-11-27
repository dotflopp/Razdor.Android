package com.flopp.razdor.patterns

import android.util.Patterns

class EZPatternEmail
: EZPattern {
    override fun matchesPattern(
        input: String
    ) = Patterns.EMAIL_ADDRESS
        .matcher(input)
        .matches()
}