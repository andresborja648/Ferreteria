package com.example.ferreteria

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Definir las tablas y columnas
const val DATABASE_NAME = "ferreteria.db"
const val DATABASE_VERSION = 1

// Tablas
const val TABLE_CLIENTES = "clientes"
const val TABLE_PEDIDOS = "pedidos"
const val TABLE_PRODUCTOS = "productos"
const val TABLE_FACTURAS = "facturas"
const val TABLE_USUARIOS = "usuarios"

// Columnas de las tablas
const val COLUMN_ID = "id"
const val COLUMN_CEDULA = "cedula"
const val COLUMN_NOMBRE = "nombre"
const val COLUMN_DIRECCION = "direccion"
const val COLUMN_TELEFONO = "telefono"

const val COLUMN_FECHA_PEDIDO = "fecha"
const val COLUMN_CANTIDAD_PEDIDO = "cantidad"
const val COLUMN_ID_CLIENTE = "id_cliente"
const val COLUMN_ID_PRODUCTO = "id_producto"
const val COLUMN_VALOR_TOTAL = "valor_total"

const val COLUMN_CODIGO_PRODUCTO = "codigo"
const val COLUMN_DESCRIPCION_PRODUCTO = "descripcion"
const val COLUMN_VALOR_PRODUCTO = "valor"

const val COLUMN_NUMERO_FACTURA = "numero_factura"
const val COLUMN_FECHA_FACTURA = "fecha"
const val COLUMN_TOTAL_FACTURA = "total"

const val COLUMN_USUARIO = "usuario"
const val COLUMN_CONTRASENA = "contrasena"

class FerreteriaDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {

        val createClientesTable = """
            CREATE TABLE IF NOT EXISTS $TABLE_CLIENTES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CEDULA INTEGER NOT NULL,
                $COLUMN_NOMBRE TEXT NOT NULL,
                $COLUMN_DIRECCION TEXT NOT NULL,
                $COLUMN_TELEFONO TEXT NOT NULL
            );
        """

        val createPedidosTable = """
            CREATE TABLE IF NOT EXISTS $TABLE_PEDIDOS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ID_CLIENTE INTEGER NOT NULL,
                $COLUMN_ID_PRODUCTO INTEGER NOT NULL,
                $COLUMN_FECHA_PEDIDO TEXT NOT NULL,
                $COLUMN_CANTIDAD_PEDIDO INTEGER NOT NULL,
                $COLUMN_VALOR_TOTAL REAL NOT NULL,
                FOREIGN KEY ($COLUMN_ID_CLIENTE) REFERENCES $TABLE_CLIENTES($COLUMN_ID) ON DELETE CASCADE ON UPDATE CASCADE,
                FOREIGN KEY ($COLUMN_ID_PRODUCTO) REFERENCES $TABLE_PRODUCTOS($COLUMN_ID) ON DELETE CASCADE ON UPDATE CASCADE
            );
        """


        val createProductosTable = """
            CREATE TABLE IF NOT EXISTS $TABLE_PRODUCTOS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CODIGO_PRODUCTO INTEGER NOT NULL,
                $COLUMN_DESCRIPCION_PRODUCTO TEXT NOT NULL,
                $COLUMN_VALOR_PRODUCTO REAL NOT NULL
            );
        """

        val createFacturasTable = """
            CREATE TABLE IF NOT EXISTS $TABLE_FACTURAS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NUMERO_FACTURA INTEGER NOT NULL,
                $COLUMN_FECHA_FACTURA TEXT NOT NULL,
                $COLUMN_TOTAL_FACTURA REAL NOT NULL
            );
        """

        val createUsuariosTable = """
            CREATE TABLE IF NOT EXISTS $TABLE_USUARIOS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USUARIO TEXT NOT NULL,
                $COLUMN_CONTRASENA TEXT NOT NULL
            );
        """

        db?.execSQL(createClientesTable)
        db?.execSQL(createPedidosTable)
        db?.execSQL(createProductosTable)
        db?.execSQL(createFacturasTable)
        db?.execSQL(createUsuariosTable)

        val insertUsuario = """
            INSERT INTO $TABLE_USUARIOS ($COLUMN_USUARIO, $COLUMN_CONTRASENA)
            VALUES ('admin', '1234');
        """.trimIndent()

        db?.execSQL(insertUsuario)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        db?.execSQL("DROP TABLE IF EXISTS $TABLE_CLIENTES")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PEDIDOS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTOS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_FACTURAS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIOS")
        onCreate(db)
    }

    // --- CRUD Clientes ---
    fun insertCliente(db: SQLiteDatabase, cedula: Int, nombre: String, direccion: String, telefono: String) {
        val values = ContentValues().apply {
            put(COLUMN_CEDULA, cedula)
            put(COLUMN_NOMBRE, nombre)
            put(COLUMN_DIRECCION, direccion)
            put(COLUMN_TELEFONO, telefono)
        }
        db.insert(TABLE_CLIENTES, null, values)
    }

    fun getClientes(db: SQLiteDatabase): Cursor {
        return db.query(TABLE_CLIENTES, null, null, null, null, null, null)
    }

    fun updateCliente(db: SQLiteDatabase, cedula: Int, nombre: String, direccion: String, telefono: String) {
        val values = ContentValues().apply {
            put(COLUMN_NOMBRE, nombre)
            put(COLUMN_DIRECCION, direccion)
            put(COLUMN_TELEFONO, telefono)
        }
        db.update(TABLE_CLIENTES, values, "$COLUMN_CEDULA = ?", arrayOf(cedula.toString()))
    }

    fun deleteCliente(db: SQLiteDatabase, cedula: Int) {
        db.delete(TABLE_CLIENTES, "$COLUMN_CEDULA = ?", arrayOf(cedula.toString()))
    }

    // --- CRUD Pedidos ---
    fun insertPedido(db: SQLiteDatabase, id_cliente: Int, id_producto: Int, fecha: String, cantidad: Int, total: Double) {
        val values = ContentValues().apply {
            put(COLUMN_ID_CLIENTE, id_cliente)
            put(COLUMN_ID_PRODUCTO, id_producto)
            put(COLUMN_FECHA_PEDIDO, fecha)
            put(COLUMN_CANTIDAD_PEDIDO, cantidad)
            put(COLUMN_VALOR_TOTAL, total)
        }
        db.insert(TABLE_PEDIDOS, null, values)
    }

    fun getPedidos(db: SQLiteDatabase): Cursor {
        return db.query(
            TABLE_PEDIDOS, // Nombre de la tabla
            arrayOf( // Columnas a seleccionar
                COLUMN_ID,
                COLUMN_ID_CLIENTE,
                COLUMN_ID_PRODUCTO,
                COLUMN_FECHA_PEDIDO,
                COLUMN_CANTIDAD_PEDIDO,
                COLUMN_VALOR_TOTAL
            ),
            null, // WHERE
            null, // Argumentos WHERE
            null, // GROUP BY
            null, // HAVING
            null  // ORDER BY
        )
    }

    fun updatePedido(db: SQLiteDatabase, id_pedido: Int ,id_cliente: Int, id_producto: Int, fecha: String, cantidad: Int, total: Double) {
        val values = ContentValues().apply {
            put(COLUMN_ID_CLIENTE, id_cliente)
            put(COLUMN_ID_PRODUCTO, id_producto)
            put(COLUMN_FECHA_PEDIDO, fecha)
            put(COLUMN_CANTIDAD_PEDIDO, cantidad)
            put(COLUMN_VALOR_TOTAL, total)
        }
        db.update(TABLE_PEDIDOS, values, "$COLUMN_ID = ?", arrayOf(id_pedido.toString()))
    }

    fun deletePedido(db: SQLiteDatabase, codigoPedido: Int) {
        db.delete(TABLE_PEDIDOS, "$COLUMN_ID = ?", arrayOf(codigoPedido.toString()))
    }

    // --- CRUD Productos ---
    fun insertProducto(db: SQLiteDatabase, descripcion: String, valor: Double) {
        val values = ContentValues().apply {
            put(COLUMN_DESCRIPCION_PRODUCTO, descripcion)
            put(COLUMN_VALOR_PRODUCTO, valor)
        }
        db.insert(TABLE_PRODUCTOS, null, values)
    }

    fun getProductos(db: SQLiteDatabase): Cursor {
        return db.query(TABLE_PRODUCTOS, null, null, null, null, null, null)
    }

    fun updateProducto(db: SQLiteDatabase, codigoProducto: Int, descripcion: String, valor: Double) {
        val values = ContentValues().apply {
            put(COLUMN_DESCRIPCION_PRODUCTO, descripcion)
            put(COLUMN_VALOR_PRODUCTO, valor)
        }
        db.update(TABLE_PRODUCTOS, values, "$COLUMN_CODIGO_PRODUCTO = ?", arrayOf(codigoProducto.toString()))
    }

    fun deleteProducto(db: SQLiteDatabase, codigoProducto: Int) {
        db.delete(TABLE_PRODUCTOS, "$COLUMN_CODIGO_PRODUCTO = ?", arrayOf(codigoProducto.toString()))
    }

    // --- CRUD Facturas ---
    fun insertFactura(db: SQLiteDatabase, fecha: String, total: Double) {
        val values = ContentValues().apply {
            put(COLUMN_FECHA_FACTURA, fecha)
            put(COLUMN_TOTAL_FACTURA, total)
        }
        db.insert(TABLE_FACTURAS, null, values)
    }

    fun getFacturas(db: SQLiteDatabase): Cursor {
        return db.query(TABLE_FACTURAS, null, null, null, null, null, null)
    }

    fun updateFactura(db: SQLiteDatabase, numeroFactura: Int, fecha: String, total: Double) {
        val values = ContentValues().apply {
            put(COLUMN_FECHA_FACTURA, fecha)
            put(COLUMN_TOTAL_FACTURA, total)
        }
        db.update(TABLE_FACTURAS, values, "$COLUMN_NUMERO_FACTURA = ?", arrayOf(numeroFactura.toString()))
    }

    fun deleteFactura(db: SQLiteDatabase, numeroFactura: Int) {
        db.delete(TABLE_FACTURAS, "$COLUMN_NUMERO_FACTURA = ?", arrayOf(numeroFactura.toString()))
    }

    // --- CRUD Usuarios ---
    fun insertUsuario(db: SQLiteDatabase, usuario: String, contrasena: String) {
        val values = ContentValues().apply {
            put(COLUMN_USUARIO, usuario)
            put(COLUMN_CONTRASENA, contrasena)
        }
        db.insert(TABLE_USUARIOS, null, values)
    }

    fun getUsuarios(db: SQLiteDatabase): Cursor {
        return db.query(TABLE_USUARIOS, null, null, null, null, null, null)
    }

    fun updateUsuario(db: SQLiteDatabase, id_usuario: Int, usuario: String, contrasena: String) {
        val values = ContentValues().apply {
            put(COLUMN_USUARIO, usuario)
            put(COLUMN_CONTRASENA, contrasena)
        }
        db.update(TABLE_USUARIOS, values, "$COLUMN_ID = ?", arrayOf(id_usuario.toString()))
    }

    fun deleteUsuario(db: SQLiteDatabase, id_usuario: Int) {
        db.delete(TABLE_USUARIOS, "$COLUMN_ID = ?", arrayOf(id_usuario.toString()))
    }


    fun checkUsuario(db: SQLiteDatabase, usuario: String, contrasena: String): Boolean {
        val cursor = db.query(TABLE_USUARIOS, null, "$COLUMN_USUARIO = ? AND $COLUMN_CONTRASENA = ?",
            arrayOf(usuario, contrasena), null, null, null)
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }
}
