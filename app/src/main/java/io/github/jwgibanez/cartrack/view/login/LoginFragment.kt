package io.github.jwgibanez.cartrack.view.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import io.github.jwgibanez.cartrack.R
import io.github.jwgibanez.cartrack.databinding.FragmentLoginBinding
import io.github.jwgibanez.cartrack.viewmodel.LoginViewModel
import android.widget.AdapterView

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val username = binding.username
        val password = binding.password
        val spinner = binding.countrySpinner
        val spinnerError = binding.spinnerError
        val shouldRemember = binding.rememberLogin
        val login = binding.login
        val loading = binding.loading

        viewModel.loginFormState.observe(viewLifecycleOwner, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError!!)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError!!)
            }
            spinnerError?.text =
                if (loginState.countryError != null) getString(loginState.countryError!!) else ""
        })

        viewModel.loginResult.observe(viewLifecycleOwner, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                val action = LoginFragmentDirections
                    .actionLoginFragmentToListFragment(binding.username.text.toString())
                findNavController().navigate(action)
            }
        })

        viewModel.rememberedAccount.observe(viewLifecycleOwner) {
            if (it != null) {
                isValidationPaused = true
                username.setText(it.username)
                password.setText(it.password)
                if (shouldRemember?.isChecked == false)
                    shouldRemember.isChecked = true
                spinner?.setSelection(resources.getStringArray(R.array.countries_array).indexOf(it.country))
                login.isEnabled = true
                isValidationPaused = false
            }
        }

        username.afterTextChanged {
            viewModel.loginDataChanged(
                requireContext(),
                username.text.toString(),
                password.text.toString(),
                spinner?.selectedItem as String
            )
        }

        password.apply {
            afterTextChanged {
                viewModel.loginDataChanged(
                    requireContext(),
                    username.text.toString(),
                    password.text.toString(),
                    spinner?.selectedItem as String
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        viewModel.login(
                            username.text.toString(),
                            password.text.toString(),
                            spinner?.selectedItem as String,
                            shouldRemember?.isChecked ?: false
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                viewModel.login(
                    username.text.toString(),
                    password.text.toString(),
                    spinner?.selectedItem as String,
                    shouldRemember?.isChecked ?: false
                )
            }
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.countries_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.countrySpinner?.adapter = adapter
        }

        var initializing = true // do not trigger validation on init
        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (initializing) {
                    initializing = false
                    return
                }
                viewModel.loginDataChanged(
                    requireContext(),
                    username.text.toString(),
                    password.text.toString(),
                    spinner?.selectedItem as String
                )
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(context, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
var isValidationPaused = false

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            if (!isValidationPaused) afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}