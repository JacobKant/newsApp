package ru.jacobkant.newsapp.presentation.newsDetails

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.frag_news_details.*
import moxy.MvpAppCompatFragment
import ru.jacobkant.newsapp.R

class NewsDetailsFragment : MvpAppCompatFragment() {

    private val chromeClient = object : WebChromeClient() {
        override fun onPermissionRequest(request: PermissionRequest) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity?.runOnUiThread {
                    request.grant(request.resources)
                }
            }
        }

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            frag_news_details_progress.visibility = View.VISIBLE
            frag_news_details_progress.progress = newProgress
        }

    }

    private val webClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            frag_news_details_progress.visibility = View.GONE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.frag_news_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initWebView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        frag_news_details_webView.webViewClient = null
        frag_news_details_webView.webChromeClient = null
    }

    override fun onDestroy() {
        super.onDestroy()
        frag_news_details_webView?.destroy()
    }

    private fun initWebView() {
        frag_news_details_webView.webViewClient = webClient
        frag_news_details_webView.webChromeClient = chromeClient
        frag_news_details_webView.settings.builtInZoomControls = false
        frag_news_details_webView.settings.setSupportZoom(false)
        frag_news_details_webView.settings.javaScriptEnabled = true
        frag_news_details_webView.settings.javaScriptCanOpenWindowsAutomatically = true
        frag_news_details_webView.settings.allowFileAccess = true
        frag_news_details_webView.settings.domStorageEnabled = true
        frag_news_details_webView.loadUrl(arguments?.getString("url") ?: "https://google.com")
        frag_news_details_srl.setOnRefreshListener {
            frag_news_details_webView.reload()
            frag_news_details_srl.isRefreshing = false
        }
    }
}
