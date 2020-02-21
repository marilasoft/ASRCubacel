package cu.marilasoft.asrcubacel.ui.store

import android.content.Context
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cu.marilasoft.asrcubacel.R
import cu.marilasoft.asrcubacel.lib.Communicator
import cu.marilasoft.asrcubacel.lib.SharedApp
import cu.marilasoft.asrcubacel.lib.convertFloatMoneyToString
import cu.marilasoft.selibrary.models.Product
import cu.marilasoft.selibrary.utils.CommunicationException
import cu.marilasoft.selibrary.utils.OperationException
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException

class ProductAdapter(var list: ArrayList<Product>) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(list[position])
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindItem(data: Product) {
            val title: TextView = itemView.findViewById(R.id.tv_title)
            val description: TextView = itemView.findViewById(R.id.tv_description)
            val price: TextView = itemView.findViewById(R.id.tv_price)
            val buy: Button = itemView.findViewById(R.id.btn_buy)

            title.text = data.title
            description.text = data.description
            price.text = convertFloatMoneyToString(data.price.toString())
            buy.setOnClickListener {
                RunTask(itemView.context, data).execute()
            }
        }

        inner class RunTask(override var mContext: Context, var data: Product) :
            AsyncTask<Void?, Void?, Void?>(), Communicator {
            private val progressDialog = customProgressBar
            lateinit var errorMessage: String
            private var runError = false

            override fun onPreExecute() {
                super.onPreExecute()
                progressDialog.show(mContext, mContext.getString(R.string.please_wait))
            }

            override fun doInBackground(vararg params: Void?): Void? {
                try {
                    enableSSLSocket()
                    val cookies = HashMap<String, String>()
                    cookies["portaluser"] = SharedApp.prefs.portalUser
                    data.buy(data.urlBuyAction, cookies)
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
                if (progressDialog.dialog.isShowing) progressDialog.dialog.dismiss()
                if (runError) showAlertDialog(errorMessage)
                else showAlertDialog("Success")
            }
        }
    }
}