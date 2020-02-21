package cu.marilasoft.asrcubacel.ui.account

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import cu.marilasoft.asrcubacel.R
import cu.marilasoft.asrcubacel.lib.Communicator
import cu.marilasoft.asrcubacel.lib.SharedApp
import cu.marilasoft.asrcubacel.lib.convertFloatMoneyToString
import cu.marilasoft.selibrary.MCPortal
import cu.marilasoft.selibrary.utils.CommunicationException
import cu.marilasoft.selibrary.utils.OperationException
import kotlinx.android.synthetic.main.fragment_account.*
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException

class AccountFragment : Fragment() {
    private lateinit var animationShow: Animation
    private lateinit var animationDismiss: Animation

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_account, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mContext = context as Context
        updateInterface()
        animationShow = AnimationUtils.loadAnimation(mContext, R.anim.anim_update_show)
        animationDismiss = AnimationUtils.loadAnimation(mContext, R.anim.anim_update_dismiss)
        RunTask(mContext).execute()
    }

    fun updateInterface() {
        tv_remaining_credit.text = convertFloatMoneyToString(SharedApp.prefs.credit.toString())
        tv_expire.text = "${getString(R.string.expire_service)} ${SharedApp.prefs.expire}"
        sw_bonus_services_state.isChecked = SharedApp.prefs.bonusServices
        if (SharedApp.prefs.creditBonus != 0.0f) {
            tv_remaining_bonus_credit.text =
                convertFloatMoneyToString(SharedApp.prefs.creditBonus.toString())
            tv_expire_bonus.text =
                "${getString(R.string.expire_bonus)} ${SharedApp.prefs.expireBonus}"
            tv_remaining_bonus_credit.visibility = View.VISIBLE
            tv_remaining_bonus_credit_info.visibility = View.VISIBLE
        }
        tv_payable_balance.text =
            "${getString(R.string.payable_balance)} ${SharedApp.prefs.payableBalance}"
        if (SharedApp.prefs.payableBalance != "0.00 CUC") {
            tv_date.text = "${getString(R.string.date)} ${SharedApp.prefs.date}"
            tv_date.visibility = View.VISIBLE
        } else {
            rb_accept_payable_balance_conditions.isEnabled = true
            btn_loan_request.isEnabled = true
        }
        if (SharedApp.prefs.isSubscribeFNF) {
            ll_f_and_f.visibility = View.VISIBLE
            et_phone_number_one.setText(SharedApp.prefs.phoneNumberOne)
            if (SharedApp.prefs.phoneNumberOne != "") et_phone_number_one.isEnabled = false
            et_phone_number_two.setText(SharedApp.prefs.phoneNumberTwo)
            if (SharedApp.prefs.phoneNumberTwo != "") et_phone_number_two.isEnabled = false
            et_phone_number_three.setText(SharedApp.prefs.phoneNumberThree)
            if (SharedApp.prefs.phoneNumberThree != "") et_phone_number_three.isEnabled = false
        }
    }

    inner class RunTask(override var mContext: Context) : AsyncTask<Void?, Void?, Void?>(),
        Communicator, MCPortal {
        lateinit var errorMessage: String
        private var runError = false

        override fun onPreExecute() {
            super.onPreExecute()
            rl_update.startAnimation(animationShow)
        }

        override fun doInBackground(vararg params: Void?): Void? {
            try {
                enableSSLSocket()
                cookies["portaluser"] = SharedApp.prefs.portalUser
                loadMyAccount(null, cookies, loadHomePage = true)
                SharedApp.prefs.sessionId = cookies["DRUTT_DSERVER_SESSIONID"].toString()
                SharedApp.prefs.phoneNumber = phoneNumber!!
                SharedApp.prefs.credit = credit!!.replace(" CUC", "").toFloat()
                SharedApp.prefs.expire = expire.toString()
                if (creditBonus != null) SharedApp.prefs.creditBonus =
                    creditBonus!!.replace(" CUC", "").toFloat()
                SharedApp.prefs.expireBonus = expireBonus.toString()
                SharedApp.prefs.date = date.toString()
                SharedApp.prefs.payableBalance = payableBalance.toString()
                SharedApp.prefs.bonusServices = isActiveBonusServices
                SharedApp.prefs.urlChangeBonusServices = urls["changeBonusServices"].toString()
                SharedApp.prefs.urlProducts = urls["products"].toString()
                SharedApp.prefs.isSubscribeFNF = familyAndFriends.isSubscribe
                SharedApp.prefs.phoneNumberOne = familyAndFriends.phoneNumbers[0].phoneNumber
                SharedApp.prefs.phoneNumberTwo = familyAndFriends.phoneNumbers[1].phoneNumber
                SharedApp.prefs.phoneNumberThree = familyAndFriends.phoneNumbers[2].phoneNumber
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
            if (runError) {
                pb_updating.visibility = View.GONE
                tv_update_message.text = errorMessage
                rl_update.setBackgroundColor(getColor(mContext, android.R.color.holo_red_light))
            } else {
                updateInterface()
                pb_updating.visibility = View.GONE
                tv_update_message.text = getString(R.string.updated)
                rl_update.setBackgroundColor(getColor(mContext, R.color.colorSuccess))
            }
            Wait().execute()
        }

    }

    inner class Wait : AsyncTask<Void?, Void?, Void?>() {
        override fun doInBackground(vararg params: Void?): Void? {
            Thread.sleep(4000)
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            rl_update.startAnimation(animationDismiss)
        }
    }
}