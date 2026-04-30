package com.coffeeshop.common.model.products

enum class ModifierCategory {
    SYRUP,
    MARSHMALLOW,
    ALT_MILK,
    VITAMIN_SHOT
}

fun ModifierCategory.display(): String = when (this) {
    ModifierCategory.SYRUP -> "Сироп"
    ModifierCategory.MARSHMALLOW -> "Маршмэллоу"
    ModifierCategory.ALT_MILK -> "Альтернативное молоко"
    ModifierCategory.VITAMIN_SHOT -> "Витаминный шот"
}