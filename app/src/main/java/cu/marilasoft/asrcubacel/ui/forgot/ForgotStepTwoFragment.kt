package cu.marilasoft.asrcubacel.ui.forgot


import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cu.marilasoft.asrcubacel.R
import cu.marilasoft.asrcubacel.lib.Communicator
import cu.marilasoft.selibrary.MCPortal
import cu.marilasoft.selibrary.utils.CommunicationException
import cu.marilasoft.selibrary.utils.OperationException
import kotlinx.android.synthetic.main.fragment_forgot_step_two.*
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException

/**
 * A simple [Fragment] subclass.
 */
class ForgotStepTwoFragment : Fragment() {

    lateinit var mContext: Context
    lateinit var mActivity: Activity
    lateinit var phoneNumberInput: String
    lateinit var code: String
    lateinit var newPassword: String
    lateinit var sessionId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_step_two, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = context as Context
        mActivity = activity as Activity

        sessionId = ForgotStepTwoFragmentArgs.fromBundle(arguments!!).sessionId
        phoneNumberInput = ForgotStepTwoFragmentArgs.fromBundle(arguments!!).phoneNumber

        val permissionCheck = ContextCompat.checkSelfPermission(
            mContext, Manifest.permission.RECEIVE_SMS
        )
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.i("Mensaje", "No se tiene permiso para enviar SMS.")
            ActivityCompat.requestPermissions(
                mActivity,
                arrayOf(Manifest.permission.RECEIVE_SMS),
                225
            )
        } else {
            val mr = MessageReceiver()
            val filter = IntentFilter("android.provider.Telephony.SMS_RECEIVED")
            mActivity.registerReceiver(mr, filter)
        }

        ib_finish.setOnClickListener {
            code = et_code.text.toString()
            newPassword = et_new_password.text.toString()
            val repeatPassword = et_repeat_password.text.toString()
            var error = false
            if (code.length < 4) {
                et_code.error = getString(R.string.error_code_receiver)
                error = true
            }
            if (newPassword.length < 6) {
                et_new_password.error = getString(R.string.error_password)
                error = true
            }
            if (repeatPassword.isEmpty()) {
                et_repeat_password.error = getString(R.string.input_required)
                error = true
            } else if (repeatPassword != newPassword) {
                et_repeat_password.error = getString(R.string.passwords_do_not_match)
                error = true
            }
            if (!error) {
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
                completeResetPassword(code, newPassword, cookies)
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
                val action = ForgotStepTwoFragmentDirections.fromResetToResult(
                    phoneNumberInput,
                    newPassword
                )
                findNavController().navigate(action)
            }
        }
    }

    inner class MessageReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val bundle = intent.extras
            if (bundle != null) {
                val permissionCheck = ContextCompat.checkSelfPermission(
                    mContext, Manifest.permission.READ_SMS
                )
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    Log.i("Mensaje", "No se tiene permiso para enviar SMS.")
                    ActivityCompat.requestPermissions(
                        mActivity,
                        arrayOf(Manifest.permission.READ_SMS),
                        224
                    )
                } else {
                    val sms = bundle["pdus"] as Array<Any>?
                    for (i in sms!!.indices) {
                        val message: SmsMessage =
                            SmsMessage.createFromPdu(sms[i] as ByteArray)
                        val numero: String = message.displayOriginatingAddress
                        Log.e("Numero: ", numero)
                        val messageText: String = message.messageBody.toString()
                        Log.e("Message: ", messageText)
                        if (numero == "Cubacel") {
                            val fCode = messageText.split("CODIGO MiCubacel: ")[1]
                            Log.e("Code: ", fCode)
                            try {
                                et_code.setText(fCode)
                                et_code.isEnabled = false
                            } catch (e: NullPointerException) {
                                Log.e("Error", e.message.toString())
                            }
                        }
                    }
                }
            }
        }
    }
}
