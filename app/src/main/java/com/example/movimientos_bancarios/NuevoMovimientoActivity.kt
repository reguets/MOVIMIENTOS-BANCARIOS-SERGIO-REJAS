package com.example.movimientos_bancarios

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class NuevoMovimientoActivity : AppCompatActivity() {

    private lateinit var cantidadEditText: EditText
    private lateinit var fechaEditText: EditText
    private lateinit var tipoSpinner: androidx.appcompat.widget.AppCompatSpinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_movimiento)

        cantidadEditText = findViewById(R.id.cantidadEditText)
        fechaEditText = findViewById(R.id.fechaEditText)
        tipoSpinner = findViewById(R.id.tipoSpinner)

        // configuro Spinner con opciones de tipo de movimiento
        val tiposMovimiento = arrayOf("Ingreso", "Egreso")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tiposMovimiento)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        tipoSpinner.adapter = adapter

        // configuro EditText para DatePicker
        fechaEditText.setOnClickListener {
            mostrarDatePicker()
        }

        // configuro el botón de guardar
        val guardarButton: Button = findViewById(R.id.guardarButton)
        guardarButton.setOnClickListener {
            guardarMovimiento()
        }
    }

    private fun mostrarDatePicker() {
        // obtengo la fecha actual
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Crea el DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)

                // formateo la fecha seleccionada y defino EditText
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formattedDate = sdf.format(selectedDate.time)
                fechaEditText.setText(formattedDate)
            },
            year,
            month,
            day
        )

        // muestro el DatePickerDialog
        datePickerDialog.show()
    }

    private fun guardarMovimiento() {
        // obtengo los valores ingresados por el usuario
        val cantidad = cantidadEditText.text.toString().toDoubleOrNull()
        val fecha = fechaEditText.text.toString()
        val tipo = tipoSpinner.selectedItem.toString()

        // Verifico si se ingresaron todos los campos
        if (cantidad != null && fecha.isNotEmpty() && tipo.isNotEmpty()) {
            //  guardamos el movimiento en la base de datos
            val dbHelper = MovimientosDatabaseHelper(this)
            val id = dbHelper.insertMovimiento(cantidad, fecha, tipo)

            // Verifica si la inserción fue exitosa
            if (id != -1L) {
                // Muestra mensaje
                Toast.makeText(this, "Movimiento guardado correctamente", Toast.LENGTH_SHORT).show()
                // Lleva al usuario a MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()

            } else {
                // mensaje de error insert falla
                Toast.makeText(this, "Error al guardar el movimiento", Toast.LENGTH_SHORT).show()
            }
        } else {
            // mensaje de error campos vacios
            Toast.makeText(this, "Debes completar todos los campos", Toast.LENGTH_SHORT).show()
        }
    }
}
