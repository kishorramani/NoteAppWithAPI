package com.kishorramani.noteappwithapi.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.kishorramani.noteappwithapi.R
import com.kishorramani.noteappwithapi.databinding.FragmentRegisterBinding
import com.kishorramani.noteappwithapi.models.UserRequest
import com.kishorramani.noteappwithapi.utils.Helper
import com.kishorramani.noteappwithapi.utils.NetworkResult
import com.kishorramani.noteappwithapi.utils.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val authViewModel by activityViewModels<AuthViewModel>()

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        if (tokenManager.getToken() != null) {
            findNavController().navigate(R.id.action_registerFragment_to_mainFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            it.findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.btnSignUp.setOnClickListener {
            Helper.hideKeyboard(it)
            val validationResult = validateUserInput()
            if (validationResult.first) {
                //val userRequest = getUserRequest()
                //authViewModel.registerUser(userRequest)
                authViewModel.registerUser(getUserRequest())
            } else {
                showValidationErrors(validationResult.second)
            }
        }

        bindObservers()
    }

    private fun validateUserInput(): Pair<Boolean, String> {
        /*val emailAddress = binding.txtEmail.text.toString()
        val userName = binding.txtUsername.text.toString()
        val password = binding.txtPassword.text.toString()
        return authViewModel.validateCredentials(emailAddress, userName, password, false)*/

        val userRequest = getUserRequest()
        return authViewModel.validateCredentials(userRequest.email, userRequest.username, userRequest.password, false)
    }

    private fun showValidationErrors(error: String) {
        binding.txtError.text = String.format(resources.getString(R.string.txt_error_message, error))
    }

    private fun getUserRequest(): UserRequest {
        return binding.run {
            UserRequest(
                txtEmail.text.toString(),
                txtPassword.text.toString(),
                txtUsername.text.toString()
            )
        }
    }

    private fun bindObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                authViewModel.userResponseStateFlow.collect {
                    binding.progressBar.isVisible = false
                    when (it) {
                        is NetworkResult.Success -> {
                            tokenManager.saveToken(it.data!!.token)
                            findNavController().navigate(R.id.action_registerFragment_to_mainFragment)
                        }

                        is NetworkResult.Error -> {
                            showValidationErrors(it.message.toString())
                        }

                        is NetworkResult.Loading -> {
                            binding.progressBar.isVisible = true
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}