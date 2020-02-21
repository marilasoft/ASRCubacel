package cu.marilasoft.asrcubacel


import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cu.marilasoft.asrcubacel.lib.Communicator
import cu.marilasoft.asrcubacel.lib.SharedApp
import cu.marilasoft.selibrary.MCPortal
import cu.marilasoft.selibrary.utils.CommunicationException
import cu.marilasoft.selibrary.utils.LoginException
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException

/**
 * A simple [Fragment] subclass.
 */
class OperationResultFragment : Fragment() {
    lateinit var mContext: Context
    lateinit var phoneNumberInput: String
    lateinit var password: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_operation_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = context as Context
        phoneNumberInput = OperationResultFragmentArgs.fromBundle(arguments!!).phoneNumer
        password = OperationResultFragmentArgs.fromBundle(arguments!!).password
        RunTask(mContext).execute()
    }

    inner class RunTask(override var mContext: Context) : AsyncTask<Void?, Void?, Void?>(),
        Communicator, MCPortal {
        private val progressDialog = customProgressBar
        private lateinit var errorMessage: String
        private var runError = false

        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog.show(mContext, getString(R.string.please_wait))
        }

        override fun doInBackground(vararg params: Void?): Void? {
            try {
                enableSSLSocket()
                login(phoneNumberInput, password)
                SharedApp.prefs.portalUser = cookies["portaluser"].toString()
                SharedApp.prefs.urlMyAccount = urls["myAccount"].toString()
                SharedApp.prefs.urlProducts = urls["products"].toString()
            } catch (e: KeyManagementException) {
                e.printStackTrace()
            } catch (e2: NoSuchAlgorithmException) {
                e2.printStackTrace()
            } catch (e: CommunicationException) {
                e.printStackTrace()
                runError = true
                errorMessage = e.message.toString()
            } catch (e: LoginException) {
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
            else mContext.startActivity(Intent(mContext, HomeActivity::class.java))
        }
    }
}
