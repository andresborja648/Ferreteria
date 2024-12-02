package com.example.ferreteria.menu

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.ferreteria.R
import com.example.ferreteria.usuarios.LoginActivity
import com.example.ferreteria.clientes.ClientesActivity
import com.example.ferreteria.pedidos.PedidosActivity
import com.example.ferreteria.productos.ProductosActivity
import com.example.ferreteria.facturas.FacturasActivity
import com.example.ferreteria.usuarios.UsuariosActivity

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val btnClientes: Button = findViewById(R.id.btnClientes)
        val btnPedidos: Button = findViewById(R.id.btnPedidos)
        val btnProductos: Button = findViewById(R.id.btnProductos)
        val btnFacturas: Button = findViewById(R.id.btnFacturas)
        val btnLogout: Button = findViewById(R.id.btnLogout)
        val btnUsuarios: Button = findViewById(R.id.btnUsuarios)

        btnClientes.setOnClickListener {
            val intent = Intent(this, ClientesActivity::class.java)
            startActivity(intent)
        }

        btnPedidos.setOnClickListener {
            val intent = Intent(this, PedidosActivity::class.java)
            startActivity(intent)
        }

        btnProductos.setOnClickListener {
            val intent = Intent(this, ProductosActivity::class.java)
            startActivity(intent)
        }

        btnFacturas.setOnClickListener {
            val intent = Intent(this, FacturasActivity::class.java)
            startActivity(intent)
        }

        btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        btnUsuarios.setOnClickListener {
            val intent = Intent(this, UsuariosActivity::class.java)
            startActivity(intent)
        }
    }
}
