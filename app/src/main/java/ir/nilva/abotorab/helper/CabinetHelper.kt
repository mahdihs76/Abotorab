package ir.nilva.abotorab.helper

import ir.nilva.abotorab.webservices.cabinet.CabinetResponse
import ir.nilva.abotorab.webservices.model.Cell

fun CabinetResponse.getCell(index: Int): Cell {
    val rowIndex = index / getColumnsNumber()
    val columnIndex = index % getColumnsNumber()
    return rows[rowIndex].cells[columnIndex]
}

fun CabinetResponse.getRowsNumber() = rows.size

fun CabinetResponse.getColumnsNumber() = rows[0].cells.size