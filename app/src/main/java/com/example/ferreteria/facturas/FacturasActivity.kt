package com.example.ferreteria.facturas

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ferreteria.FerreteriaDBHelper
import com.example.ferreteria.R

class FacturasActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var FacturasAdapter: facturasAdapter
    private lateinit var dbHelper: FerreteriaDBHelper
    private lateinit var btnAgregarFactura: Button
    private lateinit var btnActualizarFactura: Button
    private lateinit var btnEliminarFactura: Button
    private lateinit var txtSinFacturas: TextView
    private var facturaSeleccionado: Factura? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_facturas)

        recyclerView = findViewById(R.id.recyclerViewFacturas)
        recyclerView.layoutManager = LinearLayoutManager(this)

        dbHelper = FerreteriaDBHelper(this)

        btnAgregarFactura = findViewById(R.id.btnAgregarFactura)
        btnActualizarFactura = findViewById(R.id.btnActualizarFactura)
        btnEliminarFactura = findViewById(R.id.btnEliminarFactura)
        txtSinFacturas = findViewById(R.id.txtNoFacturas)

        btnActualizarFactura.visibility = View.GONE
        btnEliminarFactura.visibility = View.GONE

        btnAgregarFactura.setOnClickListener {
            val intent = Intent(this, AgregarFacturaActivity::class.java)
            startActivity(intent)
        }

        btnActualizarFactura.setOnClickListener {
            facturaSeleccionado?.let {
                val intent = Intent(this, AgregarFacturaActivity::class.java)
                intent.putExtra("id", it.id)
                intent.putExtra("numero_factura", it.factura)
                intent.putExtra("fecha", it.fecha)
                intent.putExtra("total", it.total)
                startActivity(intent)
            }
        }

        btnEliminarFactura.setOnClickListener {
            facturaSeleccionado?.let {
                eliminarFactura(it)
            }
        }

        actualizarListaFacturas()
    }

    override fun onResume() {
        super.onResume()
        actualizarListaFacturas()
    }

    @SuppressLint("Range")
    private fun obtenerFacturas(): List<Factura> {
        val db = dbHelper.readableDatabase
        val cursor = dbHelper.getFacturas(db)

        val listaFacturas = mutableListOf<Factura>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
            val factura = cursor.getInt(cursor.getColumnIndex(COLUMN_NUMERO_FACTURA))
            val fecha = cursor.getString(cursor.getColumnIndex(COLUMN_FECHA_FACTURA))
            val total = cursor.getDouble(cursor.getColumnIndex(COLUMN_TOTAL_FACTURA))

            listaFacturas.add(Factura(id, factura, fecha, total))
        }
        cursor.close()
        return listaFacturas
    }

    private fun actualizarListaFacturas() {
        val Facturas = obtenerFacturas()
        if (Facturas.isEmpty()) {
            txtSinFacturas.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            txtSinFacturas.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            FacturasAdapter = facturasAdapter(Facturas)
            recyclerView.adapter = FacturasAdapter
        }
        btnActualizarFactura.visibility = View.GONE
        btnEliminarFactura.visibility = View.GONE
        facturaSeleccionado = null
    }

    private fun eliminarFactura(Factura: Factura) {
        val db = dbHelper.writableDatabase
        val rowsDeleted = db.delete("Facturas", "id = ?", arrayOf(Factura.id.toString()))
        if (rowsDeleted > 0) {
            Toast.makeText(this, "Factura eliminado", Toast.LENGTH_SHORT).show()
            actualizarListaFacturas()
        } else {
            Toast.makeText(this, "Error al eliminar Factura", Toast.LENGTH_SHORT).show()
        }
    }

    inner class facturasAdapter(private val Facturas: List<Factura>) :
        RecyclerView.Adapter<facturasAdapter.FacturaViewHolder>() {

        private var selectedPosition: Int = RecyclerView.NO_POSITION

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FacturaViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_factura, parent, false)
            return FacturaViewHolder(view)
        }

        override fun onBindViewHolder(holder: FacturaViewHolder, position: Int) {
            val Factura = Facturas[position]
            holder.bind(Factura)

            val currentPosition = holder.adapterPosition
            if (currentPosition == selectedPosition) {
                holder.itemView.setBackgroundColor(resources.getColor(R.color.selectedList))
            } else {
                holder.itemView.setBackgroundColor(resources.getColor(android.R.color.transparent))
            }

            holder.itemView.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = currentPosition
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)

                facturaSeleccionado = Factura
                btnActualizarFactura.visibility = View.VISIBLE
                btnEliminarFactura.visibility = View.VISIBLE
            }
        }

        override fun getItemCount(): Int {
            return Facturas.size
        }

        inner class FacturaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val txtNoFacturas: TextView = itemView.findViewById(R.id.txtNoFacturas)
            private val txtFecha: TextView = itemView.findViewById(R.id.txtFecha)
            private val txtTotal: TextView = itemView.findViewById(R.id.txtTotal)

            @SuppressLint("SetTextI18n")
            fun bind(Factura: Factura) {
                txtNoFacturas.text = "Factura N°: ${Factura.factura}"
                txtFecha.text = "Fecha: ${Factura.fecha}"
                txtTotal.text = "Valor: ${Factura.total}"
            }
        }
    }

    data class Factura(
        val id: Int,
        val factura: Int,
        val fecha: String,
        val total: Double
    )

    companion object {
        const val COLUMN_ID = "id"
        const val COLUMN_NUMERO_FACTURA = "numero_factura"
        const val COLUMN_FECHA_FACTURA = "fecha"
        const val COLUMN_TOTAL_FACTURA = "total"
    }
}
