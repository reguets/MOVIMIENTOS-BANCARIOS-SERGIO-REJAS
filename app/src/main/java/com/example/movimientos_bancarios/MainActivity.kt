package com.example.movimientos_bancarios

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

data class Movimiento(val id: Int, val cantidad: Double, val fecha: String, val tipo: String)

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var movimientosAdapter: MovimientosAdapter
    private lateinit var movimientosList: MutableList<Movimiento>
    private lateinit var balanceTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // INICIALIZO MOVIMIENTOS CON DB
        val dbHelper = MovimientosDatabaseHelper(this)
        movimientosList = dbHelper.getAllMovimientos()

        // CONFIGURO RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        movimientosAdapter = MovimientosAdapter(movimientosList)
        recyclerView.adapter = movimientosAdapter

        // MUESTRO TOAST SI LISTA ESTA VACIA
        if (movimientosList.isEmpty()) {
            Toast.makeText(this, "No existen registros", Toast.LENGTH_SHORT).show()
        }

        // BOTON PARA AGREGAR MOVIMIENTO
        val addMovimientoButton: Button = findViewById(R.id.addMovimientoButton)
        addMovimientoButton.setOnClickListener {
            val intent = Intent(this, NuevoMovimientoActivity::class.java)
            startActivity(intent)
        }

        // Inicializar TextView para mostrar el balance
        balanceTextView = findViewById(R.id.balanceTextView)

        // Obtener el balance total de la base de datos
        val balance = calcularBalanceTotal(movimientosList)

        // Mostrar el balance en el TextView
        balanceTextView.text = getString(R.string.balance_text, balance)
    }

    private fun calcularBalanceTotal(movimientos: List<Movimiento>): Double {
        var balance = 0.0
        for (movimiento in movimientos) {
            if (movimiento.tipo == "Ingreso") {
                balance += movimiento.cantidad
            } else if (movimiento.tipo == "Egreso") {
                balance -= movimiento.cantidad
            }
        }
        return balance
    }

    //ACTUALIZO RECICLERVIEW
    override fun onResume() {
        super.onResume()

        // RECARGO LISTA
        val dbHelper = MovimientosDatabaseHelper(this)
        movimientosList.clear()
        movimientosList.addAll(dbHelper.getAllMovimientos())
        movimientosAdapter.notifyDataSetChanged()
    }

    // MovimientosAdapter
    inner class MovimientosAdapter(private val movimientosList: List<Movimiento>) :
        RecyclerView.Adapter<MovimientosAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movimiento, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val movimiento = movimientosList[position]
            holder.bind(movimiento)
        }

        override fun getItemCount(): Int {
            return movimientosList.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val cantidadTextView: TextView = itemView.findViewById(R.id.cantidadTextView)
            private val fechaTextView: TextView = itemView.findViewById(R.id.fechaTextView)

            fun bind(movimiento: Movimiento) {
                val formattedCantidad = if (movimiento.tipo == "Ingreso") "+${movimiento.cantidad} Bs." else "-${movimiento.cantidad} Bs."
                cantidadTextView.text = formattedCantidad

                val colorResId = if (movimiento.tipo == "Ingreso") R.color.green else R.color.red
                cantidadTextView.setTextColor(ContextCompat.getColor(itemView.context, colorResId))

                fechaTextView.text = movimiento.fecha
            }
        }
    }
}
