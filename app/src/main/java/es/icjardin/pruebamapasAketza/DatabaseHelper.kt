package es.icjardin.pruebamapasAketza

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/**
 * Representa un marcador geográfico con un ID, título, latitud, longitud y descripción.
 *
 * @property id El ID único del marcador en la base de datos.
 * @property latitud La latitud de la ubicación del marcador.
 * @property longitud La longitud de la ubicación del marcador.
 * @property titulo El título o nombre del marcador.
 * @property descripcion La descripción del marcador.
 */
data class Marcador(
    val id: Long,
    val latitud: Double,
    val longitud: Double,
    val titulo: String,
    val descripcion: String
)

/**
 * DatabaseHelper gestiona las operaciones de la base de datos relacionadas con los marcadores.
 * @param context El contexto de la aplicación, usado para crear y acceder a la base de datos.
 */
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "mapa.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_MAP = "Mapa"
        private const val COLUMN_ID = "Id"
        private const val COLUMN_TITULO="Ubicacion"
        private const val COLUMN_LATITUDE="Latitud"
        private const val COLUMN_LONGITUDE="Longitud"
        private const val COLUMN_DESCRIPTION="Descripcion"
    }
    /**
     * Lista de ciudades por defecto que pueden ser insertadas en la base de datos.
     */
    public val ciudades: List<Marcador> = listOf(
        Marcador(0,42.849998, -2.683333,"vitoria","Capital del País Vasco"),
        Marcador(0,43.263056, -2.934611,"bilbao", "Centro económico y cultural del País Vasco"),
        Marcador(0,40.416775, -3.703790,"madrid", "Capital de España"),
        Marcador(0,41.385064, 2.173403, "barcelona", "Capital de Cataluña"),
        Marcador(0,37.382830, -5.973174, "sevilla", "Capital de Andalucia"),
        Marcador(0,39.466667, -0.375000, "valencia", "Ciudad costera conocida por su Ciudad de las Artes y las Ciencias"),
        Marcador(0,41.650002, -0.883, "zaragoza", "Capital de Aragón"),
        Marcador(0,41.6544, -4.723611, "valladolid", "Ciudad con un gran patrimonio histórico"),
        Marcador(0,40.966668, -5.650000, "salamanca", "Hogar de una de las universidades más antiguas de Europa"),
        Marcador(0,43.366667, -5.800000, "oviedo", "Capital de Asturias")
    )

    /**
     * Crea la tabla de marcadores en la base de datos.
     * @param db La base de datos en la que se crea la tabla.
     */
    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE $TABLE_MAP (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " $COLUMN_TITULO TEXT," +
                "$COLUMN_LATITUDE DOUBLE," +
                "$COLUMN_LONGITUDE DOUBLE," +
                "$COLUMN_DESCRIPTION TEXT)"
        db.execSQL(createTable)
    }

    /**
     * Actualiza la base de datos al cambiar la versión.
     * @param db La base de datos a actualizar.
     * @param oldVersion La versión anterior de la base de datos.
     * @param newVersion La nueva versión de la base de datos.
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MAP")
        onCreate(db)
    }

    /**
     * Inserta un nuevo marcador en la base de datos.
     * @param marcador El marcador a insertar.
     */
    fun insertPoint(marcador: Marcador) {
        //la abro en modo escritura
        val db = writableDatabase
        db.beginTransaction()
        try {
            val values = ContentValues().apply {
                put(COLUMN_TITULO, marcador.titulo)
                put(COLUMN_DESCRIPTION, marcador.descripcion)
                put(COLUMN_LATITUDE, marcador.latitud)
                put(COLUMN_LONGITUDE, marcador.longitud)
            }
            //insertamos datos
            db.insert(TABLE_MAP, null, values)
            //y ahora a cerrar
            //db.close()
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    /**
     * Recupera todos los marcadores de la base de datos.
     * @return Una lista mutable de marcadores.
     */
    fun getMarcadores(): MutableList<Marcador> {
        val marcadores = mutableListOf<Marcador>()
        val db = readableDatabase
        val cursor = db.query(TABLE_MAP, arrayOf(COLUMN_ID, COLUMN_TITULO, COLUMN_LATITUDE,
            COLUMN_LONGITUDE, COLUMN_DESCRIPTION), null, null, null, null, null)
        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(COLUMN_ID))
                val titulo=getString(getColumnIndexOrThrow(COLUMN_TITULO))
                val latitud=getDouble(getColumnIndexOrThrow(COLUMN_LATITUDE))
                val longitud=getDouble(getColumnIndexOrThrow(COLUMN_LONGITUDE))
                val descripcion=getString(getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                marcadores.add(Marcador(id, latitud,longitud,titulo,descripcion))
            }
            close()
        }
        return marcadores
    }

    /**
     * Recupera un marcador específico por su título.
     * @param titulo El título del marcador a buscar.
     * @return El marcador encontrado, o null si no existe.
     */
    fun getMarcadorByTitulo(titulo: String): Marcador? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_MAP,
            arrayOf(COLUMN_ID, COLUMN_LATITUDE, COLUMN_LONGITUDE, COLUMN_TITULO, COLUMN_DESCRIPTION),
            "$COLUMN_TITULO = ?",
            arrayOf(titulo),
            null, null, null
        )

        var marcador: Marcador? = null
        with(cursor) {
            if (moveToFirst()) {
                val id = getLong(getColumnIndexOrThrow(COLUMN_ID))
                val latitud = getDouble(getColumnIndexOrThrow(COLUMN_LATITUDE))
                val longitud = getDouble(getColumnIndexOrThrow(COLUMN_LONGITUDE))
                val descripcion = getString(getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                marcador = Marcador(id, latitud, longitud, titulo, descripcion)
            }
            close()
        }
        return marcador
    }

}
