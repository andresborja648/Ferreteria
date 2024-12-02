package com.example.ferreteria.pedidos

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ContentValues
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.ferreteria.FerreteriaDBHelper
import com.example.ferreteria.R
import java.util.*

class AgregarPedidoActivity : AppCompatActivity() {

    private lateinit var spinnerClientes: Spinner
    private lateinit var spinnerProductos: Spinner
    private lateinit var edtCantidad: EditText
    private lateinit var edtFecha: EditText
    private lateinit var txtTotal: TextView
    private lateinit var edtEncabezado: TextView
    private lateinit var btnGuardar: Button
    private lateinit var dbHelper: FerreteriaDBHelper

    private var precioProducto: Double = 0.0
    private lateinit var clientes: List<Pair<Int, String>>
    private lateinit var productos: List<Pair<Int, String>>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_pedido)

        spinnerClientes = findViewById(R.id.spinnerClientes)
        spinnerProductos = findViewById(R.id.spinnerProductos)
        edtCantidad = findViewById(R.id.edtCantidad)
        edtFecha = findViewById(R.id.edtFecha)
        txtTotal = findViewById(R.id.edtTotal)
        edtEncabezado = findViewById(R.id.edtEncabezado)
        btnGuardar = findViewById(R.id.btnGuardar)
        dbHelper = FerreteriaDBHelper(this)

        edtEncabezado.text = "Agregar Pedido"
        cargarDatos()
        configurarEventos()
        configurarEdicionPedido()
    }

    private fun cargarDatos() {
        clientes = obtenerClientesDesdeDB()
        productos = obtenerProductosDesdeDB()

        val clienteAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, clientes.map { it.second })
        clienteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerClientes.adapter = clienteAdapter

        val productoAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, productos.map { it.second })
        productoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerProductos.adapter = productoAdapter
    }

    private fun configurarEventos() {
        spinnerProductos.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                precioProducto = obtenerPrecioProducto(productos[position].first)
                calcularTotal()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        edtCantidad.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                calcularTotal()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        edtFecha.setOnClickListener {
            mostrarDatePicker()
        }

        btnGuardar.setOnClickListener {
            guardarPedido()
        }
    }

    private fun configurarEdicionPedido() {
        val pedidoId = intent.getIntExtra("pedido_id", -1)
        if (pedidoId != -1) {
            val clienteSeleccionado = intent.getStringExtra("pedido_cliente")
            val productoSeleccionado = intent.getStringExtra("pedido_producto")

            clienteSeleccionado?.let { nombreCliente ->
                val posicion = clientes.indexOfFirst { it.second == nombreCliente }
                if (posicion != -1) spinnerClientes.setSelection(posicion)
            }

            productoSeleccionado?.let { descripcionProducto ->
                val posicion = productos.indexOfFirst { it.second == descripcionProducto }
                if (posicion != -1) spinnerProductos.setSelection(posicion)
            }

            edtCantidad.setText(intent.getIntExtra("pedido_cantidad", 0).toString())
            edtFecha.setText(intent.getStringExtra("pedido_fecha"))
            edtEncabezado.text = "Editar Pedido"
        }
    }

    private fun obtenerClientesDesdeDB(): List<Pair<Int, String>> {
        val db = dbHelper.readableDatabase
        val clientes = mutableListOf<Pair<Int, String>>()
        val cursor = db.query("clientes", arrayOf("id", "nombre"), null, null, null, null, null)
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndexOrThrow("id"))
                val nombre = it.getString(it.getColumnIndexOrThrow("nombre"))
                clientes.add(Pair(id, nombre))
            }
        }
        return clientes
    }

    private fun obtenerProductosDesdeDB(): List<Pair<Int, String>> {
        val db = dbHelper.readableDatabase
        val productos = mutableListOf<Pair<Int, String>>()
        val cursor = db.query("productos", arrayOf("id", "descripcion"), null, null, null, null, null)
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getInt(it.getColumnIndexOrThrow("id"))
                val descripcion = it.getString(it.getColumnIndexOrThrow("descripcion"))
                productos.add(Pair(id, descripcion))
            }
        }
        return productos
    }

    private fun obtenerPrecioProducto(idProducto: Int): Double {
        val db = dbHelper.readableDatabase
        val cursor = db.query("productos", arrayOf("valor"), "id = ?", arrayOf(idProducto.toString()), null, null, null)
        cursor.use {
            if (it.moveToFirst()) {
                return it.getDouble(it.getColumnIndexOrThrow("valor"))
            }
        }
        return 0.0
    }

    private fun calcularTotal() {
        val cantidad = edtCantidad.text.toString().toIntOrNull() ?: 0
        val total = precioProducto * cantidad
        txtTotal.text =  total.toString()
    }

    private fun mostrarDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                edtFecha.setText("$dayOfMonth/${month + 1}/$year")
            },
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun guardarPedido() {
        val clienteId = clientes[spinnerClientes.selectedItemPosition].first
        val productoId = productos[spinnerProductos.selectedItemPosition].first
        val cantidad = edtCantidad.text.toString().toIntOrNull() ?: 0
        val fecha = edtFecha.text.toString()
        val total = txtTotal.text.toString().toDoubleOrNull() ?: 0.0

        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("id_cliente", clienteId)
            put("id_producto", productoId)
            put("cantidad", cantidad)
            put("fecha", fecha)
            put("valor_total", total)
        }

        val pedidoId = intent.getIntExtra("pedido_id", -1)
        if (pedidoId == -1) {
            val newRowId = db.insert("pedidos", null, values)
            if (newRowId != -1L) {
                Toast.makeText(this, "Pedido guardado correctamente.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error al guardar el pedido.", Toast.LENGTH_SHORT).show()
            }
        } else {
            val rowsUpdated = db.update("pedidos", values, "id = ?", arrayOf(pedidoId.toString()))
            if (rowsUpdated > 0) {
                Toast.makeText(this, "Pedido actualizado correctamente.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error al actualizar el pedido.", Toast.LENGTH_SHORT).show()
            }
        }
        setResult(RESULT_OK)
        finish()
    }
}
