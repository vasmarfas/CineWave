package ru.vasmarfas.cinewave.ui.serviceItems

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpacesItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        //добавить переданное кол-во пикселей отступа снизу
        outRect.top = space
        outRect.bottom = space
        outRect.left = space
        outRect.right = space
    }
}