package com.example.movimientos_bancarios

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.*

class MovimientosDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "movimientos.db"
        const val TABLE_NAME = "movimientos"
        const val COLUMN_ID = "id"
        const val COLUMN_CANTIDAD = "cantidad"
        const val COLUMN_FECHA = "fecha"
        const val COLUMN_TIPO = "tipo"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_CANTIDAD REAL, " +
                "$COLUMN_FECHA TEXT, " +
                "$COLUMN_TIPO TEXT)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    //GuARDAMOS MOVIMIENTO
    fun insertMovimiento(cantidad: Double, fecha: String, tipo: String): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_CANTIDAD, cantidad)
        contentValues.put(COLUMN_FECHA, fecha)
        contentValues.put(COLUMN_TIPO, tipo)
        return db.insert(TABLE_NAME, null, contentValues)
    }

    //CONSULTO MOVIMIENTOS
    fun getAllMovimientos(): MutableList<Movimiento> {
        val movimientosList: MutableList<Movimiento> = mutableListOf()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME ORDER BY $COLUMN_FECHA DESC"
        val cursor = db.rawQuery(query, null)

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndex(COLUMN_ID))
                val cantidad = it.getDouble(it.getColumnIndex(COLUMN_CANTIDAD))
                val fecha = it.getString(it.getColumnIndex(COLUMN_FECHA))
                val tipo = it.getString(it.getColumnIndex(COLUMN_TIPO))
                movimientosList.add(Movimiento(id, cantidad, fecha, tipo))
            }
        }

        cursor?.close()
        db.close()
        return movimientosList
    }

    //BALANCE
    fun getBalance(): Double {
        val db = this.readableDatabase
        var balance = 0.0
        val query = "SELECT SUM($COLUMN_CANTIDAD) FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            balance = cursor.getDouble(0)
        }
        cursor.close()
        return balance
    }
}
