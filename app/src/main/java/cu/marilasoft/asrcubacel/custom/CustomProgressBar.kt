package cu.marilasoft.asrcubacel.custom

import android.app.Dialog
import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import androidx.annotation.NonNull
import androidx.core.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.custom_progress_bar.view.*
import cu.marilasoft.asrcubacel.R

class CustomProgressBar() {
    lateinit var dialog: Dialog

    fun show(context: Context): Dialog {
        return show(context,null)
    }

    fun show(context: Context, title: CharSequence?): Dialog {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)  as LayoutInflater
        val view = inflater.inflate(R.layout.custom_progress_bar, null)
        if (title != null) {
            view.cp_title.text = title
        }
        view.cp_bg_view.setBackgroundColor(Color.parseColor("#60000000")) // Color de fondo
        view.cp_cardview.setCardBackgroundColor(Color.parseColor("#70000000")) // Color del cuadro
        setColorFilter(view.cp_pbar.indeterminateDrawable,
            ResourcesCompat.getColor(context.resources, R.color.colorPrimary,null)) // Color de la barra de progreso
        view.cp_title.setTextColor(Color.WHITE) // Color del texto

        dialog = Dialog(context, R.style.CustomProgressBarTheme)
        dialog.setContentView(view)
        dialog.show()

        return dialog
    }

    private fun setColorFilter(@NonNull drawable: Drawable, color: Int) {
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
        } else {
            @Suppress ("DEPRECACIÓN")
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP )
        }
    }
}





