package ir.nilva.abotorab.helper

import ir.nilva.abotorab.R
import ir.nilva.abotorab.model.CabinetResponse
import ir.nilva.abotorab.model.Cell

fun CabinetResponse.getCell(index: Int, dir: Int): Cell {
    var rowIndex = index / getColumnsNumber()
    var columnIndex = index % getColumnsNumber()
//    if (dir == 1) {
//         rowIndex = getRowsNumber() - index / getColumnsNumber() - 1
//         columnIndex = index % getColumnsNumber()
//    } else if (dir == 2) {
//         rowIndex = getRowsNumber() - index / getColumnsNumber() - 1
//         columnIndex = getColumnsNumber() - index % getColumnsNumber() - 1
//    } else if (dir == 3) {
//         rowIndex = index / getColumnsNumber()
//         columnIndex = getColumnsNumber() - index % getColumnsNumber() - 1
//    }
    return rows[rowIndex].cells[columnIndex]
}

fun CabinetResponse.rotate(dir: Int = 0) {
    if (dir % 2 == 0)
        rows.reverse()
    else
        for (row in rows) {
            row.cells.reverse()
        }
}

fun Cell.getIndex() = ((id % 100) / 10) * size + (id % 10)

fun CabinetResponse.getRowsNumber() = rows.size

fun CabinetResponse.getColumnsNumber() = rows[0].cells.size

fun Cell.getImageResource() = when {
    !isHealthy -> R.mipmap.broken
    age == -1 && !isFavorite -> R.mipmap.cabinet_empty
    age == -1 && isFavorite -> R.mipmap.cabinet_empty_fav
    age == 0 && !isFavorite -> R.mipmap.cabinet_fill_1
    age == 0 && isFavorite -> R.mipmap.cabinet_fill_1_fav
    !isFavorite -> R.mipmap.cabinet_fill_3
    else -> R.mipmap.cabinet_fill_3_fav
}