package cu.marilasoft.asrcubacel.ui.data

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cu.marilasoft.asrcubacel.R

class DataFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_data, container, false)
        val mContext = context as Context
        val rvBuys: RecyclerView = root.findViewById(R.id.rv_buys)
        rvBuys.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        val listWait = ArrayList<BuyWait>()
        listWait.add(BuyWait("hello"))
        val adapterWait = BuyWaitAdapter(listWait)
        rvBuys.adapter = adapterWait
        return root
    }
}