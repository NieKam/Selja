package io.selja.base

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import io.selja.R
import io.selja.model.ThrowableError

abstract class BaseActivity<T : BaseViewModel> : AppCompatActivity() {
    abstract val viewModel: T

    private val itemsObserver = Observer<ThrowableError> { error ->
        showErrorToast(error)
    }

    override fun onStart() {
        super.onStart()
        viewModel.let {
            it.onAttached()
            it.errors.observe(this, itemsObserver)
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.let {
            it.onAttached()
            it.errors.removeObserver(itemsObserver)
        }
    }

    private fun showErrorToast(err: ThrowableError) {
        val errorText = if (err.error != null) {
            err.error.errors.joinToString(separator = ", ")
        } else {
            getString(R.string.unknown_error)
        }

        val snack =
            Snackbar.make(findViewById(android.R.id.content), errorText, Snackbar.LENGTH_LONG)
                .apply {
                    view.setBackgroundColor(Color.RED)
                }
        snack.show()
    }
}