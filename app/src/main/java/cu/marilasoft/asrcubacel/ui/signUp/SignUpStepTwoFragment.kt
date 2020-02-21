package cu.marilasoft.asrcubacel.ui.signUp


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
import kotlinx.android.synthetic.main.fragment_sign_up_step_two.*
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException

/**
 * A simple [Fragment] subclass.
 */
class SignUpStepTwoFragment : Fragment() {
    lateinit var mContext: Context
    lateinit var sessionId: String
    lateinit var phoneNumberInput: String
    lateinit var mActivity: Activity
    lateinit var code: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up_step_two, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = context as Context
        mActivity = activity as Activity
        phoneNumberInput = SignUpStepTwoFragmentArgs.fromBundle(arguments!!).phoneNumber
        sessionId = SignUpStepTwoFragmentArgs.fromBundle(arguments!!).sessionId
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

        ib_continue.setOnClickListener {
            var error = false
            if (et_code.text.toString().isEmpty()) {
                et_code.error = getString(R.string.input_required)
                error = true
            }
            if (et_code.text.toString().length < 4) {
                et_code.error = getString(R.string.error_code_receiver)
                error = true
            }
            if (!error) {
                code = et_code.text.toString()
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
                verifyCode(code, cookies)
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
            if (runError) {
                showAlertDialog(errorMessage)
                et_code.isEnabled = true
                et_code.setText("")
            } else {
                val action =
                    SignUpStepTwoFragmentDirections.toSignUpThree(phoneNumberInput, sessionId)
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
                            code = messageText.split("CODIGO MiCubacel: ")[1]
                            Log.e("Code: ", code)
                            try {
                                et_code.setText(code)
                                et_code.isEnabled = false
                                RunTask(mContext).execute()
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
