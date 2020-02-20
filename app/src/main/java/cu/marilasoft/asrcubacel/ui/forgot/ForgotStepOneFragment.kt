package cu.marilasoft.asrcubacel.ui.forgot


import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

import cu.marilasoft.asrcubacel.R
import cu.marilasoft.asrcubacel.custom.CustomProgressBar
import cu.marilasoft.asrcubacel.lib.MCPortalCommunicator
import cu.marilasoft.selibrary.utils.CommunicationException
import kotlinx.android.synthetic.main.fragment_forgot_step_one.*
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException

/**
 * A simple [Fragment] subclass.
 */
class ForgotStepOneFragment : Fragment() {
    lateinit var phoneNumber: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_step_one, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mContext = context as Context

        ib_continue.setOnClickListener {
            phoneNumber = et_phone_number.text.toString()
            RunTask(mContext).execute()
        }
    }

    inner class RunTask(override var mContext: Context) : AsyncTask<Void?, Void?, Void?>(), MCPortalCommunicator {
        private val progressDialog = CustomProgressBar()
        lateinit var errorMessage: String
        lateinit var sessionId: String
        private var runError = false

        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog.show(mContext, getString(R.string.please_wait))
        }

        override fun doInBackground(vararg params: Void?): Void? {
            try {
                enableSSLSocket()
                sessionId = resetPassword(phoneNumber)
            } catch (e: KeyManagementException) {
                e.printStackTrace()
            } catch (e2: NoSuchAlgorithmException) {
                e2.printStackTrace()
            } catch (e: CommunicationException) {
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
            else {
                val action = ForgotStepOneFragmentDirections.toResetStepTwo(sessionId, phoneNumber)
                findNavController().navigate(action)
            }
        }
    }
}
