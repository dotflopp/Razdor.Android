package com.flopp.razdor.patterns

class EZPatternPassword
: EZPattern {
    override fun matchesPattern(
        input: String
    ) = input.length >= 8 /*&& input.contains(
        "[a-z0-9]".toRegex()
    )*/
}