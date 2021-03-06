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
import cu.marilasoft.asrcubacel.lib.Communicator
import cu.marilasoft.selibrary.MCPortal
import cu.marilasoft.selibrary.utils.CommunicationException
import cu.marilasoft.selibrary.utils.OperationException
import kotlinx.android.synthetic.main.fragment_sign_up_step_three.*
import kotlinx.android.synthetic.main.fragment_sign_up_step_three.view.*
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException

/**
 * A simple [Fragment] subclass.
 */
class SignUpStepThreeFragment : Fragment() {
    lateinit var mContext: Context
    lateinit var sessionId: String
    lateinit var phoneNumberInput: String
    lateinit var password: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up_step_three, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = context as Context
        phoneNumberInput = SignUpStepThreeFragmentArgs.fromBundle(arguments!!).phoneNumber
        sessionId = SignUpStepThreeFragmentArgs.fromBundle(arguments!!).sessionId

        ib_finish.setOnClickListener {
            var error = false
            if (et_new_password.text.toString().length < 6) {
                et_new_password.error = getString(R.string.error_password)
                error = true
            }
            if (et_repeat_password.text.toString().isEmpty()) {
                et_repeat_password.error = getString(R.string.input_required)
                error = true
            } else if (et_repeat_password.text.toString() != et_new_password.text.toString()) {
                et_repeat_password.error = getString(R.string.passwords_do_not_match)
                error = true
            }
            if (!error) {
                password = et_new_password.et_new_password.text.toString()
                RunTask(mContext).execute()
            }
        }
    }

    inner class RunTask(override var mContext: Context) : AsyncTask<Void?, Void?, Void?>(),
        Communicator, MCPortal {
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
                cookies["JSESSIONID"] = sessionId
                completeSignUp(password, cookies)
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
            else {
                val action =
                    SignUpStepThreeFragmentDirections.fromSignUpToResult(phoneNumberInput, password)
                findNavController().navigate(action)
            }
        }
    }
}
