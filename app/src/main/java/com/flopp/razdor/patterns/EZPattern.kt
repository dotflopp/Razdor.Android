package com.flopp.razdor.patterns

interface EZPattern {
    fun matchesPattern(
        input: String
    ): Boolean
}