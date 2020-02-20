package cu.marilasoft.asrcubacel.ui.store

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cu.marilasoft.asrcubacel.R

class StoreFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_store, container, false)
        val mContext = context as Context
        val recyclerView: RecyclerView = root.findViewById(R.id.rv_products)
        recyclerView.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        val listWait = ArrayList<ProductWait>()
        listWait.add(ProductWait("hello"))
        listWait.add(ProductWait("hello"))
        val adapterWait = ProductsWaitAdapter(listWait)
        recyclerView.adapter = adapterWait
        return root
    }
}