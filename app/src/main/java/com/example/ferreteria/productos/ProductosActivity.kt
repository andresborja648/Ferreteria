package com.example.ferreteria.productos

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

class ProductosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productosAdapter: ProductosAdapter
    private lateinit var dbHelper: FerreteriaDBHelper
    private lateinit var btnAgregarProducto: Button
    private lateinit var btnActualizarProducto: Button
    private lateinit var btnEliminarProducto: Button
    private lateinit var txtSinProductos: TextView
    private var productoSeleccionado: Producto? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productos)

        recyclerView = findViewById(R.id.recyclerViewProductos)
        recyclerView.layoutManager = LinearLayoutManager(this)

        dbHelper = FerreteriaDBHelper(this)

        btnAgregarProducto = findViewById(R.id.btnAgregarProducto)
        btnActualizarProducto = findViewById(R.id.btnActualizarProducto)
        btnEliminarProducto = findViewById(R.id.btnEliminarProducto)
        txtSinProductos = findViewById(R.id.txtNoProducto)

        btnActualizarProducto.visibility = View.GONE
        btnEliminarProducto.visibility = View.GONE

        btnAgregarProducto.setOnClickListener {
            val intent = Intent(this, AgregarProductoActivity::class.java)
            startActivity(intent)
        }

        btnActualizarProducto.setOnClickListener {
            productoSeleccionado?.let {
                val intent = Intent(this, AgregarProductoActivity::class.java)
                intent.putExtra("producto_id", it.id)
                intent.putExtra("producto_codigo", it.codigo)
                intent.putExtra("producto_descripcion", it.descripcion)
                intent.putExtra("producto_valor", it.valor)
                startActivity(intent)
            }
        }

        btnEliminarProducto.setOnClickListener {
            productoSeleccionado?.let {
                eliminarProducto(it)
            }
        }

        actualizarListaProductos()
    }

    override fun onResume() {
        super.onResume()
        actualizarListaProductos()
    }

    @SuppressLint("Range")
    private fun obtenerProductos(): List<Producto> {
        val db = dbHelper.readableDatabase
        val cursor = dbHelper.getProductos(db)

        val listaProductos = mutableListOf<Producto>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
            val codigo = cursor.getString(cursor.getColumnIndex(COLUMN_CODIGO_PRODUCTO))
            val descripcion = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPCION_PRODUCTO))
            val valor = cursor.getDouble(cursor.getColumnIndex(COLUMN_VALOR_PRODUCTO))

            listaProductos.add(Producto(id, codigo, descripcion, valor))
        }
        cursor.close()
        return listaProductos
    }

    private fun actualizarListaProductos() {
        val productos = obtenerProductos()
        if (productos.isEmpty()) {
            txtSinProductos.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            txtSinProductos.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            productosAdapter = ProductosAdapter(productos)
            recyclerView.adapter = productosAdapter
        }
        btnActualizarProducto.visibility = View.GONE
        btnEliminarProducto.visibility = View.GONE
        productoSeleccionado = null
    }

    private fun eliminarProducto(producto: Producto) {
        val db = dbHelper.writableDatabase
        val rowsDeleted = db.delete("productos", "id = ?", arrayOf(producto.id.toString()))
        if (rowsDeleted > 0) {
            Toast.makeText(this, "Producto eliminado", Toast.LENGTH_SHORT).show()
            actualizarListaProductos()
        } else {
            Toast.makeText(this, "Error al eliminar producto", Toast.LENGTH_SHORT).show()
        }
    }

    inner class ProductosAdapter(private val productos: List<Producto>) :
        RecyclerView.Adapter<ProductosAdapter.ProductoViewHolder>() {

        private var selectedPosition: Int = RecyclerView.NO_POSITION

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_producto, parent, false)
            return ProductoViewHolder(view)
        }

        override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
            val producto = productos[position]
            holder.bind(producto)

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

                productoSeleccionado = producto
                btnActualizarProducto.visibility = View.VISIBLE
                btnEliminarProducto.visibility = View.VISIBLE
            }
        }

        override fun getItemCount(): Int {
            return productos.size
        }

        inner class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val txtDescripcion: TextView = itemView.findViewById(R.id.txtDescripcionProducto)
            private val txtValor: TextView = itemView.findViewById(R.id.txtValorProducto)

            @SuppressLint("SetTextI18n")
            fun bind(producto: Producto) {
                txtDescripcion.text = "Producto: ${producto.descripcion}"
                txtValor.text = "Valor: $${producto.valor}"
            }
        }
    }

    data class Producto(
        val id: Int,
        val codigo: String,
        val descripcion: String,
        val valor: Double
    )

    companion object {
        const val COLUMN_ID = "id"
        const val COLUMN_CODIGO_PRODUCTO = "codigo"
        const val COLUMN_DESCRIPCION_PRODUCTO = "descripcion"
        const val COLUMN_VALOR_PRODUCTO = "valor"
    }
}
