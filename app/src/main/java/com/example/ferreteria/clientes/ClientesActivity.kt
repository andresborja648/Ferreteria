package com.example.ferreteria.clientes

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

class ClientesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var clientesAdapter: ClientesAdapter
    private lateinit var dbHelper: FerreteriaDBHelper
    private lateinit var btnAgregarCliente: Button
    private lateinit var btnActualizarCliente: Button
    private lateinit var btnEliminarCliente: Button
    private lateinit var txtSinClientes: TextView
    private var clienteSeleccionado: Cliente? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clientes)

        recyclerView = findViewById(R.id.recyclerViewClientes)
        recyclerView.layoutManager = LinearLayoutManager(this)

        dbHelper = FerreteriaDBHelper(this)

        btnAgregarCliente = findViewById(R.id.btnAgregarCliente)
        btnActualizarCliente = findViewById(R.id.btnActualizarCliente)
        btnEliminarCliente = findViewById(R.id.btnEliminarCliente)
        txtSinClientes = findViewById(R.id.txtNoClientes)

        btnActualizarCliente.visibility = View.GONE
        btnEliminarCliente.visibility = View.GONE

        btnAgregarCliente.setOnClickListener {
            val intent = Intent(this, AgregarClienteActivity::class.java)
            startActivity(intent)
        }

        btnActualizarCliente.setOnClickListener {
            clienteSeleccionado?.let {
                val intent = Intent(this, AgregarClienteActivity::class.java)
                intent.putExtra("cliente_id", it.id)
                intent.putExtra("cliente_id", it.id)
                intent.putExtra("cliente_cedula", it.cedula)
                intent.putExtra("cliente_nombre", it.nombre)
                intent.putExtra("cliente_direccion", it.direccion)
                intent.putExtra("cliente_telefono", it.telefono)
                startActivity(intent)
            }
        }

        btnEliminarCliente.setOnClickListener {
            clienteSeleccionado?.let {
                eliminarCliente(it)
            }
        }
        actualizarListaClientes()
    }

    override fun onResume() {
        super.onResume()
        actualizarListaClientes()
    }

    @SuppressLint("Range")
    private fun obtenerClientes(): List<Cliente> {
        val db = dbHelper.readableDatabase
        val cursor = dbHelper.getClientes(db)

        val listaClientes = mutableListOf<Cliente>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
            val cedula = cursor.getInt(cursor.getColumnIndex(COLUMN_CEDULA))
            val nombre = cursor.getString(cursor.getColumnIndex(COLUMN_NOMBRE))
            val direccion = cursor.getString(cursor.getColumnIndex(COLUMN_DIRECCION))
            val telefono = cursor.getString(cursor.getColumnIndex(COLUMN_TELEFONO))

            listaClientes.add(Cliente(id, cedula, nombre, direccion, telefono))
        }
        cursor.close()
        return listaClientes
    }

    private fun actualizarListaClientes() {
        val clientes = obtenerClientes()
        if (clientes.isEmpty()) {
            txtSinClientes.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            txtSinClientes.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            clientesAdapter = ClientesAdapter(clientes)
            recyclerView.adapter = clientesAdapter
        }
        btnActualizarCliente.visibility = View.GONE
        btnEliminarCliente.visibility = View.GONE
        clienteSeleccionado = null
    }

    private fun eliminarCliente(cliente: Cliente) {
        val db = dbHelper.writableDatabase
        val rowsDeleted = db.delete("clientes", "id = ?", arrayOf(cliente.id.toString()))
        if (rowsDeleted > 0) {
            Toast.makeText(this, "Cliente eliminado", Toast.LENGTH_SHORT).show()
            actualizarListaClientes()
        } else {
            Toast.makeText(this, "Error al eliminar cliente", Toast.LENGTH_SHORT).show()
        }
    }

    inner class ClientesAdapter(private val clientes: List<Cliente>) :
        RecyclerView.Adapter<ClientesAdapter.ClienteViewHolder>() {

        private var selectedPosition: Int = RecyclerView.NO_POSITION

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClienteViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_cliente, parent, false)
            return ClienteViewHolder(view)
        }

        override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
            val cliente = clientes[position]
            holder.bind(cliente)

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

                clienteSeleccionado = cliente
                btnActualizarCliente.visibility = View.VISIBLE
                btnEliminarCliente.visibility = View.VISIBLE
            }
        }

        override fun getItemCount(): Int {
            return clientes.size
        }

        inner class ClienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val txtNombre: TextView = itemView.findViewById(R.id.txtNombre)
            private val txtTelefono: TextView = itemView.findViewById(R.id.txtTelefono)

            @SuppressLint("SetTextI18n")
            fun bind(cliente: Cliente) {
                txtNombre.text = "Nombre: ${cliente.nombre}"
                txtTelefono.text = "Telefono: ${cliente.telefono}"
            }
        }
    }

    data class Cliente(
        val id: Int,
        val cedula: Int,
        val nombre: String,
        val direccion: String,
        val telefono: String
    )

    companion object {
        const val COLUMN_ID = "id"
        const val COLUMN_CEDULA = "cedula"
        const val COLUMN_NOMBRE = "nombre"
        const val COLUMN_DIRECCION = "direccion"
        const val COLUMN_TELEFONO = "telefono"
    }
}
