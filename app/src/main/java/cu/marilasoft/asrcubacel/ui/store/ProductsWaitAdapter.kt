package cu.marilasoft.asrcubacel.ui.store

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import cu.marilasoft.asrcubacel.R

class ProductsWaitAdapter(var list: ArrayList<ProductWait>): RecyclerView.Adapter<ProductsWaitAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.product_wait_item, parent, false)

        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(list[position])
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        fun bindItem(data: ProductWait) {
            val title: LinearLayout = itemView.findViewById(R.id.ll_title)
            val descriptionOne: LinearLayout = itemView.findViewById(R.id.ll_description_one)
            val descriptionTwo: LinearLayout = itemView.findViewById(R.id.ll_description_two)
            val price: LinearLayout = itemView.findViewById(R.id.ll_price)
            val mostInfo: LinearLayout = itemView.findViewById(R.id.ll_most_info)

            val animation = AnimationUtils.loadAnimation(itemView.context, R.anim.anim)

            title.startAnimation(animation)
            descriptionOne.startAnimation(animation)
            descriptionTwo.startAnimation(animation)
            price.startAnimation(animation)
            mostInfo.startAnimation(animation)
        }
    }
}