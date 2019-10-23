package io.selja.ui.newITem

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function4
import io.selja.R
import io.selja.base.BaseActivity
import io.selja.databinding.ActivityNewItemBinding
import io.selja.model.AdItem
import io.selja.model.PARCEL_PARAM
import io.selja.permissions.CAMERA_PERMISSION
import io.selja.utils.*
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit

class NewItemActivity : BaseActivity<NewItemViewModel>() {
    override val viewModel: NewItemViewModel by inject()
    private val cameraHelper: CameraHelper by inject()

    private val newItemObserver = Observer<AdItem> { item ->
        val intent = Intent().apply {
            putExtra(PARCEL_PARAM, item)
        }

        setResult(if (item != null) Activity.RESULT_OK else Activity.RESULT_CANCELED, intent)
        finish()
    }

    private val disposables = CompositeDisposable()

    private lateinit var binding: ActivityNewItemBinding

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView<ActivityNewItemBinding>(this, R.layout.activity_new_item).apply {
            viewModel = this@NewItemActivity.viewModel
        }


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.tvLocation.setOnClickListener { showMap(viewModel.lastLocation?.toIntentString()) }
        RxView.clicks(binding.fab).throttleFirst(500, TimeUnit.MILLISECONDS).subscribe { saveItem() }
        RxView.clicks(binding.btnAddPhoto).throttleFirst(500, TimeUnit.MILLISECONDS).subscribe { openCamera() }
        disposables.add(subscribeToInputValidation())
        binding.fab.isEnabled = false
    }

    private fun openCamera() {
        if (!permissionManager.hasPermission(this, CAMERA_PERMISSION)) {
            permissionManager.requestCameraPermission(this)
            return
        }

        val photoFile = cameraHelper.getPhotoFile(this)
        viewModel.onPhotoFileCreated(cameraHelper.getPhotoFilePath(photoFile), photoFile.absolutePath)
        startActivityForResult(cameraHelper.getCameraIntent(this, photoFile), REQUEST_CODE)
    }

    private fun saveItem() {
        viewModel.save(
            binding.etName.stringValue(),
            binding.etDescription.stringValue(),
            binding.etContact.stringValue(),
            binding.etPrice.doubleValue(),
            binding.spinner.selectedItemPosition
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionManager.onResults(requestCode, grantResults) { granted ->
            if (granted) {
                openCamera()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when {
            item.itemId == android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showMap(intentString: String?) {
        intentString ?: return

        val gmmIntentUri = Uri.parse(intentString)
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
            setPackage("com.google.android.apps.maps")
        }

        if (mapIntent.resolveActivity(packageManager) != null) {
            startActivity(mapIntent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.let { if (!it.isDisposed) it.dispose() }
    }

    override fun onStop() {
        super.onStop()
        viewModel.newItemLiveData.removeObserver(newItemObserver)
    }

    override fun onStart() {
        super.onStart()
        viewModel.newItemLiveData.observe(this, newItemObserver)
    }

    private fun subscribeToInputValidation(): Disposable {
        val nameObs = RxTextView.textChanges(binding.etName).skip(1)
        val descObs = RxTextView.textChanges(binding.etDescription).skip(1)
        val contactObs = RxTextView.textChanges(binding.etContact).skip(1)
        val priceObs = RxTextView.textChanges(binding.etPrice).skip(1)

        return Observable.combineLatest(nameObs, descObs, contactObs, priceObs,
            Function4<CharSequence, CharSequence, CharSequence, CharSequence, Boolean> { name, desc, contact, price ->
                return@Function4 isNameValid(name.toString()) && isContactValid(contact.toString())
                        && isPriceValid(price.toString()) && isDescValid(desc.toString())
            })
            .subscribe { isOk ->
                showUploadFab(isOk)
            }
    }

    private fun showUploadFab(isEnabled: Boolean) {
        binding.let {
            if (it.fab.isEnabled == isEnabled) {
                return
            }

            it.fab.isEnabled = isEnabled

            val margin = resources.getDimensionPixelOffset(R.dimen.fab_margin)
            val set = ConstraintSet().apply {
                clone(it.clRoot)
                clear(R.id.fab, ConstraintSet.START)
                clear(R.id.fab, ConstraintSet.END)
            }

            set.connect(
                R.id.fab,
                if (isEnabled) ConstraintSet.END else ConstraintSet.START,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END,
                margin
            )
            set.applyTo(it.clRoot)
        }
    }

    private fun isNameValid(name: String): Boolean {
        return if (name.isEmpty()) {
            binding.etName.error = getString(R.string.field_not_empty)
            false
        } else {
            true
        }
    }

    private fun isDescValid(desc: String): Boolean {
        return if (desc.isEmpty()) {
            binding.etDescription.error = getString(R.string.field_not_empty)
            false
        } else {
            true
        }
    }

    private fun isContactValid(contact: String): Boolean {
        return if (contact.length < 4) {
            binding.etContact.error = getString(R.string.contact_invalid)
            false
        } else {
            true
        }
    }

    private fun isPriceValid(price: String): Boolean {
        return try {
            if (price.isEmpty() || price.toFloat() <= 0) {
                binding.etPrice.error = getString(R.string.price_invalid)
                false
            } else {
                true
            }
        } catch (e: NumberFormatException) {
            binding.etPrice.error = getString(R.string.price_invalid)
            false
        }
    }

}
