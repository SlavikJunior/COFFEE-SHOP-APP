package com.coffeshop.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.NavKey

class Navigator<T : NavKey>(
    backStack: MutableList<T>
) {

    private val _backStack: SnapshotStateList<T> = SnapshotStateList()

    init {
        _backStack.addAll(backStack)
    }

    fun navigateTo(destination: T): Boolean = _backStack.add(destination)

    fun popUp(): Boolean = _backStack.removeLastOrNull() != null

    fun popUpTo(destination: T, inclusive: Boolean = false): Boolean {
        val index = _backStack.indexOf(destination)

        if(index < 0) return false

        if (index == _backStack.size - 1 && !inclusive) return false

        if (inclusive && index == 1) return false

        _backStack.removeRange(fromIndex = index, toIndex = _backStack.size - 1)
        if (inclusive) _backStack.removeLastOrNull()

        return true
    }
}