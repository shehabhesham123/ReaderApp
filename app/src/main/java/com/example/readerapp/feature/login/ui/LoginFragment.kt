package com.example.readerapp.feature.login.ui

import android.os.Bundle
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.readerapp.R
import com.example.readerapp.core.interation.UseCase
import com.example.readerapp.core.navigation.Navigation
import com.example.readerapp.core.network.firebase.NormalAuth
import com.example.readerapp.core.platform.BaseFragment
import com.example.readerapp.core.validation.Validation
import com.example.readerapp.databinding.FragmentLoginBinding
import com.example.readerapp.feature.auth.credentials.Authenticator
import com.example.readerapp.feature.login.data.model.User
import com.google.android.material.snackbar.Snackbar

class LoginFragment : BaseFragment() {

    private lateinit var mBinding: FragmentLoginBinding
    private val navigation by lazy { Navigation(Authenticator(requireContext())) }
    private lateinit var normalAuth: NormalAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        normalAuth = NormalAuth(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentLoginBinding.inflate(inflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI()
        mBinding.login.setOnClickListener {
            val user = getUserData()
            user?.apply {
                loadingState()
                normalAuth.login(email!!, password!!, {
                    successState("Success")
                }, {
                    failureState(it)
                })
            }
        }
    }

    private fun getUserData(): User? {
        val email = mBinding.email.text.toString()
        val password = mBinding.password.text.toString()
        return if (isValid(email, password))
            User(email, password)
        else null
    }

    private fun isValid(email: String, password: String): Boolean {
        val isEmailValid = Validation.isEmailValid(email)
        val isPasswordValid = Validation.isPasswordValid(password)

        if (!isEmailValid) mBinding.email.error = "your email is not valid"
        if (!isPasswordValid) mBinding.password.error = "your password is not valid"

        return isEmailValid && isPasswordValid
    }

    private fun updateUI() {
        hideAppbar()
        prepareSignUpSentence()
    }

    private fun prepareSignUpSentence() {
        val spannableString = SpannableString(resources.getString(R.string.SignUp))
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                navigation.showRegisterActivity(requireContext())
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                val color = resources.getColor(R.color.link, null)
                ds.color = color
            }
        }
        spannableString.setSpan(clickableSpan, 24, 31, 0)
        mBinding.signUp.text = spannableString
        mBinding.signUp.movementMethod = LinkMovementMethod()
    }

    override fun sendAction(interaction: UseCase) {}
    override fun render() {}
    override fun idleState() {}
    override fun failureState(message: String) {
        mBinding.loading.visibility = View.GONE
        mBinding.login.visibility = View.VISIBLE
        Snackbar.make(mBinding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun successState(data: Any) {
        mBinding.loading.visibility = View.GONE
        mBinding.login.visibility = View.VISIBLE
        navigation.showStoryDetails(requireContext(),true)
        finish()
    }

    override fun loadingState() {
        mBinding.loading.visibility = View.VISIBLE
        mBinding.login.visibility = View.GONE
    }

}