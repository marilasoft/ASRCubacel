package cu.marilasoft.asrcubacel.ui.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import cu.marilasoft.asrcubacel.R

class BuyWaitAdapter(var list: ArrayList<BuyWait>): RecyclerView.Adapter<BuyWaitAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.navigation_package_wait_item, parent,  false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(list[position])
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        fun bindItem(data: BuyWait) {
            val title: LinearLayout = itemView.findViewById(R.id.ll_buy_title)
            val rest: LinearLayout = itemView.findViewById(R.id.ll_buy_rest)
            val unit: LinearLayout = itemView.findViewById(R.id.ll_buy_unit)
            val date: LinearLayout = itemView.findViewById(R.id.ll_buy_date)
            val description: LinearLayout = itemView.findViewById(R.id.ll_buy_description_action)

            val animation = AnimationUtils.loadAnimation(itemView.context, R.anim.anim)

            title.startAnimation(animation)
            rest.startAnimation(animation)
            unit.startAnimation(animation)
            date.startAnimation(animation)
            description.startAnimation(animation)
        }
    }
}