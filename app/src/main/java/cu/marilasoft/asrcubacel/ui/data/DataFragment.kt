package cu.marilasoft.asrcubacel.ui.data

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cu.marilasoft.asrcubacel.R
import cu.marilasoft.asrcubacel.lib.Communicator
import cu.marilasoft.asrcubacel.lib.SharedApp
import cu.marilasoft.selibrary.MCPortal
import cu.marilasoft.selibrary.utils.CommunicationException
import cu.marilasoft.selibrary.utils.OperationException
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException

class DataFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_data, container, false)
        val mContext = context as Context
        recyclerView = root.findViewById(R.id.rv_buys)
        recyclerView.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
        val listWait = ArrayList<BuyWait>()
        listWait.add(BuyWait("hello"))
        val adapterWait = BuyWaitAdapter(listWait)
        recyclerView.adapter = adapterWait
        return root
    }

    inner class RunTask(override var mContext: Context) : AsyncTask<Void?, Void?, Void?>(),
        MCPortal, Communicator {
        lateinit var errorMessage: String
        private var runError = false

        override fun doInBackground(vararg params: Void?): Void? {
            try {
                enableSSLSocket()
                cookies["portaluser"] = SharedApp.prefs.portalUser
                loadMyAccount(SharedApp.prefs.urlMyAccount, cookies)
            } catch (e: KeyManagementException) {
                e.printStackTrace()
            } catch (e2: NoSuchAlgorithmException) {
                e2.printStackTrace()
            } catch (e: CommunicationException) {
                e.printStackTrace()
                runError = true
                errorMessage = e.message.toString()
            } catch (e: OperationException) {
                e.printStackTrace()
                runError = true
                errorMessage = e.message.toString()
            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            if (runError) showAlertDialog(errorMessage)
            recyclerView.adapter = BuyAdapter(getBuys())
        }
    }
}