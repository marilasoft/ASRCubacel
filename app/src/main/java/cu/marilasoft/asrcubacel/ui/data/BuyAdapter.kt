package cu.marilasoft.asrcubacel.ui.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import cu.marilasoft.asrcubacel.R
import cu.marilasoft.selibrary.models.NavigationPackage

class BuyAdapter(var list: List<NavigationPackage>) :
    RecyclerView.Adapter<BuyAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.navigation_package_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(list[position])
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindItem(data: NavigationPackage) {
            val title: TextView = itemView.findViewById(R.id.tv_buy_title)
            val progress: CircularProgressBar = itemView.findViewById(R.id.cpb_consumption_progress)
            val buyRest: TextView = itemView.findViewById(R.id.tv_buy_rest)
            val buyUnit: TextView = itemView.findViewById(R.id.tv_buy_unit)
            val expireDate: TextView = itemView.findViewById(R.id.tv_buy_expire_date)
            val descriptionAction: TextView = itemView.findViewById(R.id.tv_buy_description_action)
            val description: TextView = itemView.findViewById(R.id.tv_buy_description)

            title.text = data.title
            progress.setProgressWithAnimation(data.percent!!.toFloat(), 1000)
            buyRest.text = data.restData.toString()
            buyUnit.text = data.dataInfo
            expireDate.text = data.expireDate
            description.text = data.description
            descriptionAction.setOnClickListener {
                if (description.visibility != View.VISIBLE) description.visibility = View.VISIBLE
                else description.visibility = View.GONE
            }
        }
    }
}