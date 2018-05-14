package cn.psvmc.zjblog.ui

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast

import java.io.IOException
import cn.psvmc.zjblog.R
import cn.psvmc.zjblog.model.ApiModel
import cn.psvmc.zjblog.utils.Androids
import kotlinx.android.synthetic.main.activity_markdown.*

class MarkdownActivity : BaseActivity() {

    internal var TAG = "MarkdownActivity"

    internal var mWebView: WebView? = null


    internal var title: String? = null
    internal var url: String? = null
    internal var path: String? = null

    internal var content: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_markdown)
        val intent = intent
        title = intent.getStringExtra("title")
        url = intent.getStringExtra("url")
        toolbar?.title = title
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        initTitleView()
        initWebView()
    }

    private fun initTitleView() {
        tv_title?.setFactory {
            val textView = TextView(this@MarkdownActivity)
            textView.setTextAppearance(this@MarkdownActivity, R.style.WebTitle)
            textView.setSingleLine(true)
            textView.ellipsize = TextUtils.TruncateAt.MARQUEE
            textView.postDelayed({ textView.isSelected = true }, 1738)
            textView
        }
        tv_title?.setInAnimation(this, android.R.anim.fade_in)
        tv_title?.setOutAnimation(this, android.R.anim.fade_out)
        if (title != null) {
            tv_title?.setText(title)
        }
    }

    private fun initWebView() {
        mWebView = WebView(applicationContext)
        val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        web_frame_layout?.addView(mWebView, layoutParams)
        val settings = mWebView?.settings
        settings?.javaScriptEnabled = true
        settings?.loadWithOverviewMode = true
        settings?.cacheMode = WebSettings.LOAD_DEFAULT
        settings?.domStorageEnabled = true
        settings?.setAppCacheEnabled(true)
        settings?.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        settings?.setSupportZoom(true)
        mWebView?.setWebChromeClient(ChromeClient())
        mWebView?.setWebViewClient(ZJClient())
        mWebView?.loadUrl(ApiModel.baseUrl + url)
    }

    override fun onDestroy() {
        if (mWebView != null) {
            web_frame_layout?.removeAllViews()
            mWebView?.stopLoading()
            mWebView?.removeAllViews()
            mWebView?.destroy()
            mWebView = null
        }
        super.onDestroy()
    }


    override fun onPause() {
        if (mWebView != null) mWebView?.onPause()
        super.onPause()
    }


    override fun onResume() {
        super.onResume()
        if (mWebView != null)
            mWebView?.onResume()
    }

    private inner class ChromeClient : WebChromeClient() {

        override fun onProgressChanged(view: WebView, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            m_progressbar?.progress = newProgress
            if (newProgress == 100) {
                m_progressbar?.visibility = View.GONE
            } else {
                m_progressbar?.visibility = View.VISIBLE
            }
        }


        override fun onReceivedTitle(view: WebView, title: String) {
            super.onReceivedTitle(view, title)
            setTitle(title)
        }
    }

    private inner class ZJClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
            if (url != null) view.loadUrl(url)
            return true
        }

        override fun shouldInterceptRequest(view: WebView, url: String): WebResourceResponse? {
            var response: WebResourceResponse? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                response = getResponseByUrl(url)
            }
            return response
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
            val url = request.url.toString()
            return getResponseByUrl(url)
        }

        private fun getResponseByUrl(url: String): WebResourceResponse? {
            var response: WebResourceResponse? = null
            if (url.endsWith("style.css")) {
                response = getResponseByPath("css/article_style.css")
            } else if (url.endsWith("syntax-highlighting.css")) {
                response = getResponseByPath("css/syntax-highlighting.css")
            } else if (url.endsWith("glyphicons-halflings-regular.eot")) {
                response = getResponseByPath("bootstrap-3.3.5/fonts/glyphicons-halflings-regular.eot")
            } else if (url.endsWith("glyphicons-halflings-regular.svg")) {
                response = getResponseByPath("bootstrap-3.3.5/fonts/glyphicons-halflings-regular.svg")
            } else if (url.endsWith("glyphicons-halflings-regular.ttf")) {
                response = getResponseByPath("bootstrap-3.3.5/fonts/glyphicons-halflings-regular.ttf")
            } else if (url.endsWith("glyphicons-halflings-regular.woff")) {
                response = getResponseByPath("bootstrap-3.3.5/fonts/glyphicons-halflings-regular.woff")
            } else if (url.endsWith("glyphicons-halflings-regular.woff2")) {
                response = getResponseByPath("bootstrap-3.3.5/fonts/glyphicons-halflings-regular.woff2")
            } else if (url.endsWith("bootstrap.min.css")) {
                response = getResponseByPath("bootstrap-3.3.5/css/bootstrap.min.css")
            } else if (url.endsWith("bootstrap.min.js")) {
                response = getResponseByPath("bootstrap-3.3.5/js/bootstrap.min.js")
            } else if (url.endsWith("hdbg01.jpg")) {
                response = getResponseByPath("bootstrap-3.3.5/js/bootstrap.min.js")
            }
            return response
        }

        private fun getResponseByPath(path: String): WebResourceResponse? {
            try {
                var mimeType = "application/octet-stream"
                if (path.endsWith("css")) {
                    mimeType = "text/css"
                } else if (path.endsWith("js")) {
                    mimeType = "application/javascript"
                } else if (path.endsWith("png")) {
                    mimeType = "image/png"
                } else if (path.endsWith("jpg") || path.endsWith("jpeg")) {
                    mimeType = "image/jpeg"
                }
                val `is` = assets.open(path)
                return WebResourceResponse(mimeType, "UTF-8", `is`)
            } catch (e: IOException) {
                return null
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_markdown, menu)
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.close_enter, R.anim.close_exit)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.action_refresh -> {
                mWebView?.reload()
                return true
            }
            R.id.action_copy_url -> {
                Androids.copyToClipBoard(this, mWebView?.url, "复制成功")
                return true
            }
            R.id.action_open_url -> {
                val intent = Intent()
                intent.action = Intent.ACTION_VIEW
                val uri = Uri.parse(ApiModel.baseUrl + url)
                intent.data = uri
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                } else {
                    Toast.makeText(content, "打开链接失败！", Toast.LENGTH_LONG).show()
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
