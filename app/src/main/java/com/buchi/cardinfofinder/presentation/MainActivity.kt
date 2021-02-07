package com.buchi.cardinfofinder.presentation

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.buchi.cardinfofinder.R
import com.buchi.cardinfofinder.data.model.Card
import com.buchi.cardinfofinder.databinding.ActivityMainBinding
import com.buchi.cardinfofinder.databinding.ProcessFailedDialogBinding
import com.buchi.cardinfofinder.databinding.ProgressDialogBinding
import com.buchi.cardinfofinder.di.ViewModelFactory
import com.buchi.cardinfofinder.utils.hideKeyboard
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: MainViewModel by viewModels { viewModelFactory }
    var eventAndStateJob: Job? = null
    var progressDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Needed a factory to inject context into MainRepoImpl(used context of internet check)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        observeEventAndState()
        progressDialog = progressDialog()
        removeDetail()
        binding.startButton.setOnClickListener {
            val number = binding.cardNumberEntry.text.toString()
            if (number.isNotBlank() && number.length > 6) {
                clearDetailView()
                viewModel.fetchCardDetails(number)
            } else {
                binding.cardNumberEntryLayout.error = "Enter first 6-9 digits of card!"
            }
        }

        // Auto do fetching when the user has put in all 9 digits
        binding.cardNumberEntry.doAfterTextChanged { p0 ->
            if (p0?.length!! >= 9) {
                viewModel.fetchCardDetails(p0.toString())
            } else{
                clearDetailView()
            }
        }
    }

    // Update the view with the card detail fetched
    private fun updateDetailView(card: Card) {
        displayDetail()
        val emptyValue = "Not Available"
        binding.detailInclude.cardType.text = card.type?.capitalize(Locale.ROOT) ?: emptyValue
        binding.detailInclude.cardBrand.text = card.brand?.capitalize(Locale.ROOT) ?: emptyValue
        binding.detailInclude.cardBank.text = resources.getString(
            R.string.card_bank_data,
            "${card.bank?.name?.capitalize(Locale.ROOT) ?: emptyValue} ${
                card.bank?.city?.capitalize(Locale.ROOT) ?: ""
            }"
        )
        binding.detailInclude.cardCountry.text = card.country?.name?.capitalize(Locale.ROOT)
    }

    // Update the view with the card detail fetched
    private fun clearDetailView() {
        removeDetail()
        if (binding.cardNumberEntryLayout.isErrorEnabled) binding.cardNumberEntryLayout.error = null
        binding.detailInclude.cardType.text = null
        binding.detailInclude.cardBrand.text = null
        binding.detailInclude.cardBank.text = null
        binding.detailInclude.cardCountry.text = null
    }

    private fun removeDetail() {
        binding.detailInclude.root.visibility = View.INVISIBLE
    }

    private fun displayDetail() {
        this.hideKeyboard(binding.root)
        binding.detailInclude.root.animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        binding.detailInclude.root.visibility = View.VISIBLE
    }

    // Observes the live-data states from the viewModel and execute as necessary
    private fun observeEventAndState() {
        eventAndStateJob = lifecycleScope.launch {
            viewModel.dataState.observe(
                this@MainActivity,
                { viewState ->
                    viewState.loading?.let {
                        // show loading if loading state is true
                        Log.d(javaClass.simpleName, "Loading: $it")
                        if (it) progressDialog?.show() else progressDialog?.dismiss()
                    }

                    viewState.message?.let {
                        // show error when error is returned
                        it.getContentIfNotHandled()?.let { msg ->
                            inflateErrorDialog(msg)
                        }
                    }
                    viewState.data?.let { event ->
                        event.getContentIfNotHandled()?.let { state ->
                            viewModel.setViewState(viewState = state)
                        }
                    }
                }
            )

            viewModel.viewState.observe(
                this@MainActivity,
                { viewState ->
                    viewState.cardDetail?.let {
                        Log.d(javaClass.simpleName, "Fetched card $it")
                        updateDetailView(it)
                    }
                }
            )
        }
    }

    private fun inflateErrorDialog(msg: String?) {
        val dialogBinding = ProcessFailedDialogBinding.inflate(layoutInflater)
        val view = dialogBinding.root
        dialogBinding.failureMsg.text = msg
        val errorDialog = MaterialAlertDialogBuilder(this)
            .setView(view)
            .setCancelable(false)
            .show()
        dialogBinding.okButton.setOnClickListener {
            if (errorDialog.isShowing) errorDialog.dismiss()
        }
    }

    private fun progressDialog(): AlertDialog {
        val dialogBinding = ProgressDialogBinding.inflate(layoutInflater)
        val view = dialogBinding.root
        return MaterialAlertDialogBuilder(this)
            .setView(view)
            .setCancelable(false)
            .create()
    }


    override fun onDestroy() {
        super.onDestroy()
        eventAndStateJob?.cancel()
    }
}