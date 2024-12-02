package com.example.ferreteria.usuarios

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

class AgregarUsuarioActivity : AppCompatActivity() {

    private lateinit var edtUsuario: EditText
    private lateinit var edtContrasena: EditText
    private lateinit var edtEncabezado: TextView
    private lateinit var btnGuardar: Button
    private lateinit var dbHelper: FerreteriaDBHelper

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_usuario)

        edtUsuario = findViewById(R.id.edtUsuario)
        edtContrasena = findViewById(R.id.edtContrasena)
        edtEncabezado = findViewById(R.id.edtEncabezado)
        btnGuardar = findViewById(R.id.btnGuardar)
        dbHelper = FerreteriaDBHelper(this)

        edtEncabezado.text = "Agregar Usuario"

        val UsuarioId = intent.getIntExtra("id", -1)
        if (UsuarioId != -1) {
            edtUsuario.setText(intent.getStringExtra("usuario"))
            edtContrasena.setText(intent.getStringExtra("contrasena"))
            edtEncabezado.text = "Editar Usuario"
        }

        btnGuardar.setOnClickListener {
            val Usuario = edtUsuario.text.toString()
            val Contrasena = edtContrasena.text.toString()

            if (Usuario.isNotEmpty() && Contrasena.isNotEmpty()) {
                val Usuario = UsuariosActivity.Usuario(
                    UsuarioId, Usuario, Contrasena
                )
                if (UsuarioId == -1) {
                    guardarUsuario(Usuario)
                } else {
                    actualizarUsuario(Usuario)
                }
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun guardarUsuario(Usuario: UsuariosActivity.Usuario) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(UsuariosActivity.COLUMN_USUARIO, Usuario.usuario)
            put(UsuariosActivity.COLUMN_CONTRASENA, Usuario.contrasena)
        }

        val newRowId = db.insert("Usuarios", null, values)
        if (newRowId != -1L) {
            Toast.makeText(this, "Usuario agregado correctamente.", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        } else {
            Toast.makeText(this, "Error al agregar el Usuario.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun actualizarUsuario(Usuario: UsuariosActivity.Usuario) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(UsuariosActivity.COLUMN_USUARIO, Usuario.usuario)
            put(UsuariosActivity.COLUMN_CONTRASENA, Usuario.contrasena)
        }

        val rowsUpdated = db.update(
            "Usuarios",
            values,
            "id = ?",
            arrayOf(Usuario.id.toString())
        )
        if (rowsUpdated > 0) {
            Toast.makeText(this, "Usuario actualizado correctamente.", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        } else {
            Toast.makeText(this, "Error al actualizar el Usuario.", Toast.LENGTH_SHORT).show()
        }
    }
}
