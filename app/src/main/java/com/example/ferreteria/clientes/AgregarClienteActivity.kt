package com.example.ferreteria.clientes

import android.content.ContentValues
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ferreteria.FerreteriaDBHelper
import com.example.ferreteria.R

class AgregarClienteActivity : AppCompatActivity() {

    private lateinit var edtCedula: EditText
    private lateinit var edtNombre: EditText
    private lateinit var edtDireccion: EditText
    private lateinit var edtTelefono: EditText
    private lateinit var edtEncabezado: TextView
    private lateinit var btnGuardar: Button
    private lateinit var dbHelper: FerreteriaDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_cliente)

        edtCedula = findViewById(R.id.edtCedula)
        edtNombre = findViewById(R.id.edtNombre)
        edtDireccion = findViewById(R.id.edtDireccion)
        edtTelefono = findViewById(R.id.edtTelefono)
        edtEncabezado = findViewById(R.id.edtEncabezado)
        btnGuardar = findViewById(R.id.btnGuardar)

        dbHelper = FerreteriaDBHelper(this)

        edtEncabezado.text = "Agregar Cliente"

        val clienteId = intent.getIntExtra("cliente_id", -1)
        if (clienteId != -1) {
            edtCedula.setText(intent.getIntExtra("cliente_cedula", 0).toString())
            edtNombre.setText(intent.getStringExtra("cliente_nombre"))
            edtDireccion.setText(intent.getStringExtra("cliente_direccion"))
            edtTelefono.setText(intent.getStringExtra("cliente_telefono"))
            edtEncabezado.text = "Actualizar Cliente"
        }

        btnGuardar.setOnClickListener {
            val cedula = edtCedula.text.toString()
            val nombre = edtNombre.text.toString()
            val direccion = edtDireccion.text.toString()
            val telefono = edtTelefono.text.toString()

            if (cedula.isNotEmpty() && nombre.isNotEmpty() && direccion.isNotEmpty() && telefono.isNotEmpty()) {
                val cliente = ClientesActivity.Cliente(
                    clienteId, cedula.toInt(), nombre, direccion, telefono
                )
                if (clienteId == -1) {
                    guardarCliente(cliente)
                } else {
                    actualizarCliente(cliente)
                }
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun guardarCliente(cliente: ClientesActivity.Cliente) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(ClientesActivity.COLUMN_CEDULA, cliente.cedula)
            put(ClientesActivity.COLUMN_NOMBRE, cliente.nombre)
            put(ClientesActivity.COLUMN_DIRECCION, cliente.direccion)
            put(ClientesActivity.COLUMN_TELEFONO, cliente.telefono)
        }

        val newRowId = db.insert("clientes", null, values)
        if (newRowId != -1L) {
            Toast.makeText(this, "Cliente agregado correctamente.", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        } else {
            Toast.makeText(this, "Error al agregar el cliente.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun actualizarCliente(cliente: ClientesActivity.Cliente) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(ClientesActivity.COLUMN_CEDULA, cliente.cedula)
            put(ClientesActivity.COLUMN_NOMBRE, cliente.nombre)
            put(ClientesActivity.COLUMN_DIRECCION, cliente.direccion)
            put(ClientesActivity.COLUMN_TELEFONO, cliente.telefono)
        }

        val rowsUpdated = db.update(
            "clientes",
            values,
            "id = ?",
            arrayOf(cliente.id.toString())
        )
        if (rowsUpdated > 0) {
            Toast.makeText(this, "Cliente actualizado correctamente.", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        } else {
            Toast.makeText(this, "Error al actualizar el cliente.", Toast.LENGTH_SHORT).show()
        }
    }
}
