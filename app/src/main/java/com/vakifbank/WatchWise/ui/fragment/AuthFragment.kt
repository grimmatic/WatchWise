package com.vakifbank.WatchWise.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.vakifbank.WatchWise.R
import com.vakifbank.WatchWise.databinding.FragmentAuthBinding

class AuthFragment : Fragment() {
    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private var isLoginMode = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        // Kullanıcı zaten giriş yapmışsa direkt ana sayfaya yönlendir
        if (auth.currentUser != null) {
            navigateToHome()
            return
        }

        setupUI()
        setupClickListeners()
    }

    private fun setupUI() {
        if (isLoginMode) {
            binding.titleTextView.text = "Giriş Yap"
            binding.authButtonText.text = "Giriş Yap"
            binding.switchModeButton.text = "Hesabın yok mu? Kayıt ol"
        } else {
            binding.titleTextView.text = "Kayıt Ol"
            binding.authButtonText.text = "Kayıt Ol"
            binding.switchModeButton.text = "Zaten hesabın var mı? Giriş yap"
        }
    }

    private fun setupClickListeners() {
        binding.authButton.setOnClickListener {
            if (isLoginMode) {
                loginUser()
            } else {
                registerUser()
            }
        }

        binding.switchModeButton.setOnClickListener {
            isLoginMode = !isLoginMode
            setupUI()
        }

        binding.skipButton.setOnClickListener {
            navigateToHome()
        }
    }


    private fun loginUser() {
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()

        if (!validateInput(email, password)) return

        showLoading(isLoading = true)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                showLoading(isLoading = false)
                if (task.isSuccessful) {
                    showToast("Giriş başarılı!")
                    navigateToHome()
                } else {
                    showToast("Giriş başarısız!")
                }
            }
    }

    private fun registerUser() {
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()

        if (!validateInput(email, password)) return

        showLoading(isLoading = true)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                showLoading(isLoading = false)
                if (task.isSuccessful) {
                    showToast("Kayıt başarılı!")
                    navigateToHome()
                } else {
                    showToast("Kayıt başarısız!")
                }
            }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            binding.emailInputLayout.error = "E-posta adresi gerekli"
            binding.emailInputLayout.setErrorIconDrawable(null)
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailInputLayout.error = "Geçerli bir e-posta adresi girin"
            binding.emailInputLayout.setErrorIconDrawable(null)
            return false
        }

        if (password.isEmpty()) {
            binding.passwordInputLayout.error = "Şifre gerekli"
            binding.passwordInputLayout.setErrorIconDrawable(null)
            return false
        }

        if (password.length < 6) {
            binding.passwordInputLayout.error = "Şifre en az 6 karakter olmalı"
            binding.passwordInputLayout.setErrorIconDrawable(null)

            return false
        }

        return true
    }

    private fun showLoading(isLoading: Boolean) {
        binding.authButton.isEnabled = !isLoading
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.authButtonText.text = if (isLoading) "Lütfen bekleyin..." else {
            if (isLoginMode) "Giriş Yap" else "Kayıt Ol"
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToHome() {
        findNavController().navigate(R.id.action_authFragment_to_MovieListFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}