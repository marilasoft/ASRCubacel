package cu.marilasoft.asrcubacel.ui.signUp


import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cu.marilasoft.asrcubacel.R
import cu.marilasoft.asrcubacel.lib.MCPortalCommunicator
import cu.marilasoft.selibrary.utils.CommunicationException
import kotlinx.android.synthetic.main.fragment_sign_up_step_one.*
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException

/**
 * A simple [Fragment] subclass.
 */
class SignUpStepOneFragment : Fragment() {
    lateinit var phoneNumber: String
    lateinit var firstName: String
    lateinit var lastName: String
    lateinit var email: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up_step_one, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mContext = context as Context

        ib_continue.setOnClickListener {
            var error = false
            if (et_phone_number.text.isEmpty()) {
                et_phone_number.error = getString(R.string.input_required)
                error = true
            } else if (et_phone_number.text.length < 8) {
                et_phone_number.error = getString(R.string.error_phone_number)
                error = true
            }
            if (et_first_name.text.isEmpty()) {
                et_phone_number.error = getString(R.string.input_required)
                error = true
            }
            if (et_last_name.text.isEmpty()) {
                et_phone_number.error = getString(R.string.input_required)
                error = true
            }
            if (!error) {
                phoneNumber = et_phone_number.text.toString()
                firstName = et_first_name.text.toString()
                lastName = et_last_name.text.toString()
                email = et_email_address.text.toString()
                RunTask(mContext).execute()
            }
        }
    }

    inner class RunTask(override var mContext: Context) : AsyncTask<Void?, Void?, Void?>(),
        MCPortalCommunicator {
        private val progressDialog = customProgressBar
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
                sessionId = signUp(phoneNumber, firstName, lastName, email)
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
                val action = SignUpStepOneFragmentDirections.toSignUpTwo(phoneNumber)
                findNavController().navigate(action)
            }
        }
    }
}
