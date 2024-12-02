package com.example.ferreteria.pedidos

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

class PedidosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var pedidosAdapter: PedidosAdapter
    private lateinit var dbHelper: FerreteriaDBHelper
    private lateinit var btnAgregarPedido: Button
    private lateinit var btnActualizarPedido: Button
    private lateinit var btnEliminarPedido: Button
    private lateinit var txtSinPedidos: TextView
    private var pedidoSeleccionado: Pedido? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedidos)

        recyclerView = findViewById(R.id.recyclerViewPedidos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        dbHelper = FerreteriaDBHelper(this)

        btnAgregarPedido = findViewById(R.id.btnAgregarPedido)
        btnActualizarPedido = findViewById(R.id.btnActualizarPedido)
        btnEliminarPedido = findViewById(R.id.btnEliminarPedido)
        txtSinPedidos = findViewById(R.id.txtNoPedido)

        btnActualizarPedido.visibility = View.GONE
        btnEliminarPedido.visibility = View.GONE

        btnAgregarPedido.setOnClickListener {
            val intent = Intent(this, AgregarPedidoActivity::class.java)
            startActivity(intent)
        }

        btnActualizarPedido.setOnClickListener {
            pedidoSeleccionado?.let {
                val intent = Intent(this, AgregarPedidoActivity::class.java)
                intent.putExtra("pedido_id", it.id)
                intent.putExtra("pedido_cliente", it.idCliente)
                intent.putExtra("pedido_producto", it.idProducto)
                intent.putExtra("pedido_fecha", it.fecha)
                intent.putExtra("pedido_cantidad", it.cantidad)
                intent.putExtra("pedido_valor_total", it.valorTotal)
                startActivity(intent)
            }
        }

        btnEliminarPedido.setOnClickListener {
            pedidoSeleccionado?.let {
                eliminarPedido(it)
            }
        }

        actualizarListaPedidos()
    }

    override fun onResume() {
        super.onResume()
        actualizarListaPedidos()
    }

    @SuppressLint("Range")
    private fun obtenerPedidos(): List<Pedido> {
        val db = dbHelper.readableDatabase
        val cursor = dbHelper.getPedidos(db)

        val listaPedidos = mutableListOf<Pedido>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val idCliente = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_CLIENTE))
            val idProducto = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID_PRODUCTO))
            val fecha = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FECHA_PEDIDO))
            val cantidad = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CANTIDAD_PEDIDO))
            val valorTotal = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_VALOR_TOTAL))

            listaPedidos.add(Pedido(id, idCliente, idProducto, fecha, cantidad, valorTotal))
        }
        cursor.close()
        return listaPedidos
    }


    private fun actualizarListaPedidos() {
        val pedidos = obtenerPedidos()
        if (pedidos.isEmpty()) {
            txtSinPedidos.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            txtSinPedidos.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            pedidosAdapter = PedidosAdapter(pedidos)
            recyclerView.adapter = pedidosAdapter
        }
        btnActualizarPedido.visibility = View.GONE
        btnEliminarPedido.visibility = View.GONE
        pedidoSeleccionado = null
    }

    private fun eliminarPedido(pedido: Pedido) {
        val db = dbHelper.writableDatabase
        val rowsDeleted = db.delete("pedidos", "id = ?", arrayOf(pedido.id.toString()))
        if (rowsDeleted > 0) {
            Toast.makeText(this, "Pedido eliminado", Toast.LENGTH_SHORT).show()
            actualizarListaPedidos()
        } else {
            Toast.makeText(this, "Error al eliminar pedido", Toast.LENGTH_SHORT).show()
        }
    }

    inner class PedidosAdapter(private val pedidos: List<Pedido>) :
        RecyclerView.Adapter<PedidosAdapter.PedidoViewHolder>() {

        private var selectedPosition: Int = RecyclerView.NO_POSITION

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_pedido, parent, false)
            return PedidoViewHolder(view)
        }

        override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
            val pedido = pedidos[position]
            holder.bind(pedido)

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

                pedidoSeleccionado = pedido
                btnActualizarPedido.visibility = View.VISIBLE
                btnEliminarPedido.visibility = View.VISIBLE
            }
        }

        override fun getItemCount(): Int {
            return pedidos.size
        }

        inner class PedidoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val txtProducto:  TextView = itemView.findViewById(R.id.txtProducto)
            private val txtCliente: TextView = itemView.findViewById(R.id.txtCliente)
            private val txtFecha: TextView = itemView.findViewById(R.id.txtFecha)
            private val txtValorTotal: TextView = itemView.findViewById(R.id.txtTotal)

            @SuppressLint("SetTextI18n")
            fun bind(pedido: Pedido) {
                txtProducto.text = "Producto:  ${obtenerNombreProducto(pedido.idProducto)}"
                txtCliente.text = "Cliente: ${obtenerNombreCliente(pedido.idCliente)}"
                txtFecha.text = "Fecha: ${pedido.fecha}"
                txtValorTotal.text = "Total: $${pedido.valorTotal}"
            }
        }

        private fun obtenerNombreProducto(idProducto: Int): String {
            val db = dbHelper.readableDatabase
            val cursor = db.query("productos", arrayOf("descripcion"), "id = ?", arrayOf(idProducto.toString()), null, null, null)
            cursor.use {
                if (it.moveToFirst()) {
                    return it.getString(it.getColumnIndexOrThrow("descripcion"))
                }
            }
            return ""
        }

        private fun obtenerNombreCliente(idCliente: Int): String {
            val db = dbHelper.readableDatabase
            val cursor = db.query("clientes", arrayOf("nombre"), "id = ?", arrayOf(idCliente.toString()), null, null, null)
            cursor.use {
                if (it.moveToFirst()) {
                    return it.getString(it.getColumnIndexOrThrow("nombre"))
                }
            }
            return ""
        }
    }

    data class Pedido(
        val id: Int,
        val idCliente: Int,
        val idProducto: Int,
        val fecha: String,
        val cantidad: Int,
        val valorTotal: Double
    )

    companion object {
        const val COLUMN_ID = "id"
        const val COLUMN_ID_CLIENTE = "id_cliente"
        const val COLUMN_ID_PRODUCTO = "id_producto"
        const val COLUMN_FECHA_PEDIDO = "fecha"
        const val COLUMN_CANTIDAD_PEDIDO = "cantidad"
        const val COLUMN_VALOR_TOTAL = "valor_total"
    }
}
