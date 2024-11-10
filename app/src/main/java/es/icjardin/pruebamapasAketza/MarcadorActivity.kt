package es.icjardin.pruebamapasAketza

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView

/**
 * Actividad que muestra la información de un marcador específico.
 * Obtiene el título del marcador desde un Intent y usa la base de datos para obtener sus detalles.
 */
class MarcadorActivity : AppCompatActivity() {

    /**
     * Método llamado cuando la actividad es creada.
     * Inicializa la interfaz y muestra los datos del marcador especificado en la base de datos.
     * @param savedInstanceState estado previamente guardado de la actividad.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marcador)

        // Obtén el título del marcador desde el Intent
        val titulo = intent.getStringExtra("point") ?: return

        // Inicializa la base de datos para obtener el marcador
        val dbHelper = DatabaseHelper(this)
        val marcador = dbHelper.getMarcadorByTitulo(titulo)

        // Si el marcador existe, actualiza la interfaz de usuario con sus datos
        marcador?.let {
            // Muestra el título y la descripción
            findViewById<TextView>(R.id.tituloTextView).text = it.titulo
            findViewById<TextView>(R.id.descripcionTextView).text = it.descripcion

            // Muestra la imagen que tiene el mismo nombre que el título en la carpeta raw
            val imageResource = resources.getIdentifier(it.titulo.lowercase(), "raw", packageName)
            if (imageResource != 0) { // Verifica si la imagen existe
                findViewById<ImageView>(R.id.marcadorImageView).setImageResource(imageResource)
            }
        }
    }
}
