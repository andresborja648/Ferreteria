package com.example.ferreteria.usuarios

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

class UsuariosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var usuariosAdapter: UsuariosAdapter
    private lateinit var dbHelper: FerreteriaDBHelper
    private lateinit var btnAgregarUsuario: Button
    private lateinit var btnActualizarUsuario: Button
    private lateinit var btnEliminarUsuario: Button
    private lateinit var txtSinUsuarios: TextView
    private var UsuarioSeleccionado: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usuarios)

        recyclerView = findViewById(R.id.recyclerViewUsuarios)
        recyclerView.layoutManager = LinearLayoutManager(this)

        dbHelper = FerreteriaDBHelper(this)

        btnAgregarUsuario = findViewById(R.id.btnAgregarUsuario)
        btnActualizarUsuario = findViewById(R.id.btnActualizarUsuario)
        btnEliminarUsuario = findViewById(R.id.btnEliminarUsuario)
        txtSinUsuarios = findViewById(R.id.txtUsuarios)

        btnActualizarUsuario.visibility = View.GONE
        btnEliminarUsuario.visibility = View.GONE

        btnAgregarUsuario.setOnClickListener {
            val intent = Intent(this, AgregarUsuarioActivity::class.java)
            startActivity(intent)
        }

        btnActualizarUsuario.setOnClickListener {
            UsuarioSeleccionado?.let {
                val intent = Intent(this, AgregarUsuarioActivity::class.java)
                intent.putExtra("id", it.id)
                intent.putExtra("usuario", it.usuario)
                intent.putExtra("contrasena", it.contrasena)
                startActivity(intent)
            }
        }

        btnEliminarUsuario.setOnClickListener {
            UsuarioSeleccionado?.let {
                eliminarUsuario(it)
            }
        }

        actualizarListaUsuarios()
    }

    override fun onResume() {
        super.onResume()
        actualizarListaUsuarios()
    }

    @SuppressLint("Range")
    private fun obtenerUsuarios(): List<Usuario> {
        val db = dbHelper.readableDatabase
        val cursor = dbHelper.getUsuarios(db)

        val listaUsuarios = mutableListOf<Usuario>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
            val Usuario = cursor.getString(cursor.getColumnIndex(COLUMN_USUARIO))
            val Contrasena = cursor.getString(cursor.getColumnIndex(COLUMN_CONTRASENA))

            listaUsuarios.add(Usuario(id, Usuario, Contrasena))
        }
        cursor.close()
        return listaUsuarios
    }

    private fun actualizarListaUsuarios() {
        val Usuarios = obtenerUsuarios()
        if (Usuarios.isEmpty()) {
            txtSinUsuarios.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            txtSinUsuarios.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            usuariosAdapter = UsuariosAdapter(Usuarios)
            recyclerView.adapter = usuariosAdapter
        }
        btnActualizarUsuario.visibility = View.GONE
        btnEliminarUsuario.visibility = View.GONE
        UsuarioSeleccionado = null
    }

    private fun eliminarUsuario(Usuario: Usuario) {
        val db = dbHelper.writableDatabase
        val rowsDeleted = db.delete("Usuarios", "id = ?", arrayOf(Usuario.id.toString()))
        if (rowsDeleted > 0) {
            Toast.makeText(this, "Usuario eliminado", Toast.LENGTH_SHORT).show()
            actualizarListaUsuarios()
        } else {
            Toast.makeText(this, "Error al eliminar Usuario", Toast.LENGTH_SHORT).show()
        }
    }

    inner class UsuariosAdapter(private val Usuarios: List<Usuario>) :
        RecyclerView.Adapter<UsuariosAdapter.UsuarioViewHolder>() {

        private var selectedPosition: Int = RecyclerView.NO_POSITION

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_usuario, parent, false)
            return UsuarioViewHolder(view)
        }

        override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
            val Usuario = Usuarios[position]
            holder.bind(Usuario)

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

                UsuarioSeleccionado = Usuario
                btnActualizarUsuario.visibility = View.VISIBLE
                btnEliminarUsuario.visibility = View.VISIBLE
            }
        }

        override fun getItemCount(): Int {
            return Usuarios.size
        }

        inner class UsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val txtUsuario: TextView = itemView.findViewById(R.id.txtUsuarios)
            private val txtContrasena: TextView = itemView.findViewById(R.id.txtContrasena)

            @SuppressLint("SetTextI18n")
            fun bind(Usuario: Usuario) {
                txtUsuario.text = "Usuario: ${Usuario.usuario}"
                txtContrasena.text = "${Usuario.contrasena}"
            }
        }
    }

    data class Usuario(
        val id: Int,
        val usuario: String,
        val contrasena: String
    )

    companion object {
        const val COLUMN_ID = "id"
        const val COLUMN_USUARIO = "usuario"
        const val COLUMN_CONTRASENA = "contrasena"
    }
}
