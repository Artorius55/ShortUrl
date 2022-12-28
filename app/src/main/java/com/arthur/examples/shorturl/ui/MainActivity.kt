package com.arthur.examples.shorturl.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.webkit.URLUtil
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.arthur.examples.shorturl.R
import com.arthur.examples.shorturl.data.DataResult
import com.arthur.examples.shorturl.data.local.models.AliasLocal
import com.arthur.examples.shorturl.data.remote.models.Alias
import com.arthur.examples.shorturl.databinding.ActivityMainBinding
import com.arthur.examples.shorturl.ui.adapter.AliasAdapter
import com.arthur.examples.shorturl.ui.viewmodel.ShortUrlViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewmodel by viewModels<ShortUrlViewModel>()

    private val aliasAdapter = AliasAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initComponents()
        collectFromViewModel()
    }

    /**
     * Function to init view components of this Activity
     *
     * @author: Arturo Segura
     * @since: 1.0
     */
    private fun initComponents() {
        with(binding) {
            rvShortenedUrls.addItemDecoration(
                DividerItemDecoration(
                    this@MainActivity,
                    LinearLayoutManager.VERTICAL
                )
            )
            rvShortenedUrls.layoutManager =
                LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
            rvShortenedUrls.adapter = aliasAdapter
        }
        manageButtonStatus(false)
    }

    /**
     * Method for loading data flow from viewmodel.
     *
     * @author: Arturo Segura
     * @since: 1.0
     */
    private fun collectFromViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch { viewmodel.storedAlias.collect(::collectStoredAlias) }
            launch { viewmodel.shortenResult.collect(::collectShortenResult) }
        }
    }

    /**
     * Method for collect [storedAlias] locally in database.
     *
     * @author: Arturo Segura
     * @since: 1.0
     */
    private fun collectStoredAlias(storedAlias: List<AliasLocal>) {
        with(binding) {
            if (storedAlias.isNotEmpty()) {
                tvEmptyMessage.visibility = View.GONE
                rvShortenedUrls.visibility = View.VISIBLE

                aliasAdapter.setData(storedAlias)
            } else {
                tvEmptyMessage.visibility = View.VISIBLE
                rvShortenedUrls.visibility = View.GONE
            }
        }
    }

    /**
     * Method for collecting [result] from shorten process.
     *
     * @author: Arturo Segura
     * @since: 1.0
     */
    private fun collectShortenResult(result: DataResult<Alias>?) {
        result?.let { safeResult ->
            when (safeResult) {
                is DataResult.Loading -> manageButtonStatus(true)
                is DataResult.Success -> {
                    manageButtonStatus(false)
                    binding.etUrlInput.text = null
                }
                is DataResult.Error -> {
                    manageButtonStatus(false)
                    showError()
                }
            }
        }
    }

    /**
     * Function that shows an error message to the user
     *
     * @author: Arturo Segura
     * @since: 1.0
     */
    private fun showError() {
        Toast.makeText(
            applicationContext,
            getString(R.string.short_url_error_message),
            Toast.LENGTH_LONG
        ).show()
    }

    /**
     * Function validate the data introduced by user
     *
     * @author: Arturo Segura
     * @since: 1.0
     */
    private fun validateUrl() {
        with(binding) {
            val url = this.etUrlInput.text.toString()
            if (Patterns.WEB_URL.matcher(url).matches()) {
                this.tilUrl.error = ""
                this.tilUrl.isErrorEnabled = false
                shortUrl(url)
            } else {
                this.tilUrl.error = getString(R.string.bad_url_error_message)
            }
        }
    }

    /**
     * Function to start process for short URL
     *
     * @author: Arturo Segura
     * @since: 1.0
     */
    private fun shortUrl(validUrl: String) = viewmodel.shortUrl(validUrl)


    /**
     * Function to manage button status depending on [isLoading] value
     *
     * @author: Arturo Segura
     * @since: 1.0
     */
    private fun manageButtonStatus(isLoading: Boolean) {
        if (isLoading) {
            val loadingDrawable = CircularProgressDrawable(this).apply {
                setColorSchemeColors(ContextCompat.getColor(this@MainActivity, R.color.white))
                centerRadius = 30f
                strokeWidth = 5f
            }
            loadingDrawable.start()

            binding.btnAction.setImageDrawable(loadingDrawable)
            binding.btnAction.setOnClickListener { }
        } else {
            binding.btnAction.setImageDrawable(getDrawable(R.drawable.ic_action))
            binding.btnAction.setOnClickListener { validateUrl() }
        }
    }
}