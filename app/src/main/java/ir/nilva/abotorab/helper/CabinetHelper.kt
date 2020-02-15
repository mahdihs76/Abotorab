package ir.nilva.abotorab.helper

import ir.nilva.abotorab.R
import ir.nilva.abotorab.model.CabinetResponse
import ir.nilva.abotorab.model.Cell

fun CabinetResponse.getCell(index: Int): Cell {
    val rowIndex = index / getColumnsNumber()
    val columnIndex = index % getColumnsNumber()
    return rows[rowIndex].cells[columnIndex]
}

fun Cell.getIndex() = ((id % 100) / 10) * size +  (id % 10)

fun CabinetResponse.getRowsNumber() = rows.size

fun CabinetResponse.getColumnsNumber() = rows[0].cells.size

fun Cell.getImageResource() = when{
    !isHealthy -> R.mipmap.broken
    age == -1 && !isFavorite -> R.mipmap.cabinet_empty
    age == -1 && isFavorite -> R.mipmap.cabinet_empty_fav
    age == 0 && !isFavorite -> R.mipmap.cabinet_fill_1
    age == 0 && isFavorite -> R.mipmap.cabinet_fill_1_fav
    !isFavorite -> R.mipmap.cabinet_fill_3
    else -> R.mipmap.cabinet_fill_3_fav
}