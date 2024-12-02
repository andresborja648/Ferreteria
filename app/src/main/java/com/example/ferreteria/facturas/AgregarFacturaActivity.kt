package com.example.ferreteria.facturas

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ContentValues
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ferreteria.FerreteriaDBHelper
import com.example.ferreteria.R
import java.util.Calendar

class AgregarFacturaActivity : AppCompatActivity() {

    private lateinit var edtFactura: EditText
    private lateinit var edtFecha: EditText
    private lateinit var edtTotal: EditText
    private lateinit var edtEncabezado: TextView
    private lateinit var btnGuardar: Button
    private lateinit var dbHelper: FerreteriaDBHelper

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_factura)

        edtFactura = findViewById(R.id.edtNoFactura)
        edtFecha = findViewById(R.id.edtFecha)
        edtTotal = findViewById(R.id.edtTotal)
        edtEncabezado = findViewById(R.id.edtEncabezado)
        btnGuardar = findViewById(R.id.btnGuardar)
        dbHelper = FerreteriaDBHelper(this)

        edtEncabezado.text = "Agregar Factura"

        val FacturaId = intent.getIntExtra("id", -1)
        if (FacturaId != -1) {
            edtFactura.setText(intent.getIntExtra("numero_factura", 0).toString())
            edtFecha.setText(intent.getStringExtra("fecha"))
            edtTotal.setText(intent.getDoubleExtra("total", 0.0).toString())
            edtEncabezado.text = "Editar Factura"
        }

        btnGuardar.setOnClickListener {
            val factura = edtFactura.text.toString()
            val fecha = edtFecha.text.toString()
            val total = edtTotal.text.toString()

            if (factura.isNotEmpty() && fecha.isNotEmpty() && total.isNotEmpty()) {
                val Factura = FacturasActivity.Factura(
                    FacturaId, factura.toInt(), fecha, total.toDouble()
                )
                if (FacturaId == -1) {
                    guardarFactura(Factura)
                } else {
                    actualizarFactura(Factura)
                }
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            }
        }

        edtFecha.setOnClickListener {
            mostrarDatePicker()
        }
    }

    private fun guardarFactura(Factura: FacturasActivity.Factura) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(FacturasActivity.COLUMN_NUMERO_FACTURA, Factura.factura)
            put(FacturasActivity.COLUMN_FECHA_FACTURA, Factura.fecha)
            put(FacturasActivity.COLUMN_TOTAL_FACTURA, Factura.total)
        }

        val newRowId = db.insert("Facturas", null, values)
        if (newRowId != -1L) {
            Toast.makeText(this, "Factura agregado correctamente.", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        } else {
            Toast.makeText(this, "Error al agregar el Factura.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun actualizarFactura(Factura: FacturasActivity.Factura) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(FacturasActivity.COLUMN_NUMERO_FACTURA, Factura.factura)
            put(FacturasActivity.COLUMN_FECHA_FACTURA, Factura.fecha)
            put(FacturasActivity.COLUMN_TOTAL_FACTURA, Factura.total)
        }

        val rowsUpdated = db.update(
            "facturas",
            values,
            "id = ?",
            arrayOf(Factura.id.toString())
        )
        if (rowsUpdated > 0) {
            Toast.makeText(this, "Factura actualizado correctamente.", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        } else {
            Toast.makeText(this, "Error al actualizar el Factura.", Toast.LENGTH_SHORT).show()
        }
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
}
