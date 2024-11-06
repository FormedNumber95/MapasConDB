package es.icjardin.pruebamapasAketza

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/**
 * Representa una tarea con un ID único y una descripción.
 *
 * @property id El ID único de la tarea en la base de datos.
 * @property description La descripción de la tarea.
 */
data class Marcador(
    val id: Long,
    val latitud: Double,
    val longitud: Double,
    val titulo: String,
    val descripcion: String
)

/**
 * Ayudante de base de datos para gestionar las operaciones relacionadas con las tareas.
 *
 * @param context El contexto de la aplicación, usado para crear la base de datos.
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
     * Crea la tabla de tareas en la base de datos.
     *
     * @param db La base de datos en la que se crea la tabla.
     */
    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE $TABLE_MAP (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " $COLUMN_TITULO TEXT," +
                "$COLUMN_LATITUDE TEXT," +
                "$COLUMN_LONGITUDE TEXT," +
                "$COLUMN_DESCRIPTION TEXT)"
        db.execSQL(createTable)
    }

    /**
     * Actualiza la base de datos al cambiar la versión.
     *
     * @param db La base de datos que se está actualizando.
     * @param oldVersion La versión anterior de la base de datos.
     * @param newVersion La nueva versión de la base de datos.
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MAP")
        onCreate(db)
    }

    fun getMarcadores(): MutableList<Marcador> {
        val tasks = mutableListOf<Marcador>()
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
                tasks.add(Marcador(id, latitud,longitud,titulo,descripcion))
            }
            close()
        }
        return tasks
    }
}
