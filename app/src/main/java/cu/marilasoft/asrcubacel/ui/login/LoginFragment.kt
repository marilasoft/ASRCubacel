package cu.marilasoft.asrcubacel.ui.login


import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import cu.marilasoft.asrcubacel.HomeActivity

import cu.marilasoft.asrcubacel.R
import cu.marilasoft.asrcubacel.lib.MCPortalCommunicator
import cu.marilasoft.selibrary.utils.CommunicationException
import cu.marilasoft.selibrary.utils.LoginException
import kotlinx.android.synthetic.main.fragment_login.*
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment() {
    lateinit var mContext: Context
    lateinit var phoneNumber: String
    lateinit var password: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = context as Context
        tv_forgot_password.setOnClickListener {
            findNavController().navigate(R.id.to_reset_password)
        }
        tv_sign_up.setOnClickListener {
            findNavController().navigate(R.id.to_sign_up)
        }
        ib_enter.setOnClickListener {
            var error = false
            if (et_phone_number.text.toString().isEmpty()
                || et_phone_number.text.toString().length < 8
                || (!et_phone_number.text.toString().startsWith("5"))
                && !et_phone_number.text.toString().startsWith("6")) {
                et_phone_number.error = getString(R.string.error_phone_number)
                error = true
            }
            if (et_password.text.toString().isEmpty())  {
                et_password.error = getString(R.string.input_required)
                error = true
            } else if (et_password.text.toString().length < 6) {
                et_password.error = getString(R.string.error_password)
                error = true
            }
            if (!error) {
                phoneNumber = et_phone_number.text.toString()
                password = et_password.text.toString()
                RunTask(mContext).execute()
            }
        }
    }

    inner class RunTask(override var mContext: Context) : AsyncTask<Void?, Void?, Void?>(),
        MCPortalCommunicator {
        private val progressDialog = customProgressBar
        lateinit var errorMessage: String
        private var runError = false

        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog.show(mContext, getString(R.string.please_wait))
        }

        override fun doInBackground(vararg params: Void?): Void? {
            try {
                enableSSLSocket()
                login(phoneNumber, password)
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
