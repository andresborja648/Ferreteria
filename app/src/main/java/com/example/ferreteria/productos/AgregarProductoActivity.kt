package com.example.ferreteria.productos

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ferreteria.FerreteriaDBHelper
import com.example.ferreteria.R

class AgregarProductoActivity : AppCompatActivity() {

    private lateinit var edtCodigo: EditText
    private lateinit var edtDescripcion: EditText
    private lateinit var edtValor: EditText
    private lateinit var edtEncabezado: TextView
    private lateinit var btnGuardar: Button
    private lateinit var dbHelper: FerreteriaDBHelper

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_producto)

        edtCodigo = findViewById(R.id.edtCodigo)
        edtDescripcion = findViewById(R.id.edtDescripcion)
        edtValor = findViewById(R.id.edtValor)
        edtEncabezado = findViewById(R.id.edtEncabezado)
        btnGuardar = findViewById(R.id.btnGuardar)

        edtEncabezado.text = "Agregar Producto"

        dbHelper = FerreteriaDBHelper(this)

        val productoId = intent.getIntExtra("producto_id", -1)
        if (productoId != -1) {
            edtCodigo.setText(intent.getStringExtra("producto_codigo"))
            edtDescripcion.setText(intent.getStringExtra("producto_descripcion"))
            edtValor.setText(intent.getDoubleExtra("producto_valor", 0.0).toString())
            edtEncabezado.text = "Editar Producto"
        }

        btnGuardar.setOnClickListener {
            val codigo = edtCodigo.text.toString()
            val descripcion = edtDescripcion.text.toString()
            val valor = edtValor.text.toString()

            if (codigo.isNotEmpty() && descripcion.isNotEmpty() && valor.isNotEmpty()) {
                val producto = Producto(
                    productoId, codigo, descripcion, valor.toDouble()
                )
                if (productoId == -1) {
                    guardarProducto(producto)
                } else {
                    actualizarProducto(producto)
                }
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun guardarProducto(producto: Producto) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CODIGO_PRODUCTO, producto.codigo)
            put(COLUMN_DESCRIPCION_PRODUCTO, producto.descripcion)
            put(COLUMN_VALOR_PRODUCTO, producto.valor)
        }

        val newRowId = db.insert("productos", null, values)
        if (newRowId != -1L) {
            Toast.makeText(this, "Producto agregado correctamente.", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        } else {
            Toast.makeText(this, "Error al agregar el producto.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun actualizarProducto(producto: Producto) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CODIGO_PRODUCTO, producto.codigo)
            put(COLUMN_DESCRIPCION_PRODUCTO, producto.descripcion)
            put(COLUMN_VALOR_PRODUCTO, producto.valor)
        }

        val rowsUpdated = db.update(
            "productos",
            values,
            "id = ?",
            arrayOf(producto.id.toString())
        )
        if (rowsUpdated > 0) {
            Toast.makeText(this, "Producto actualizado correctamente.", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        } else {
            Toast.makeText(this, "Error al actualizar el producto.", Toast.LENGTH_SHORT).show()
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
