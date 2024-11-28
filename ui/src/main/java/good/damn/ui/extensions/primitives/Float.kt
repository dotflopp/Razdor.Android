package good.damn.ui.extensions.primitives

fun Float.interpolate(
    end: Float,
    t: Float
) = this + (end - this) * t