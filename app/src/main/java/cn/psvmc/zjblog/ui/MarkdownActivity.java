package cn.psvmc.zjblog.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.daimajia.numberprogressbar.NumberProgressBar;

import java.io.IOException;
import java.io.InputStream;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.psvmc.zjblog.R;
import cn.psvmc.zjblog.model.ApiModel;
import cn.psvmc.zjblog.utils.Androids;

public class MarkdownActivity extends AppCompatActivity {

    String TAG = "MarkdownActivity";

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    WebView mWebView;

    @Bind(R.id.web_frame_layout)
    FrameLayout web_frame_layout;

    @Bind(R.id.progressbar)
    NumberProgressBar mProgressbar;

    @Bind(R.id.tv_title)
    TextSwitcher mTextSwitcher;

    String title;
    String url;
    String path;

    Context content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_markdown);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        url = intent.getStringExtra("url");
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initTitleView();
        initWebView();
    }

    private void initTitleView() {
        mTextSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                final TextView textView = new TextView(MarkdownActivity.this);
                textView.setTextAppearance(MarkdownActivity.this, R.style.WebTitle);
                textView.setSingleLine(true);
                textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                textView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        textView.setSelected(true);
                    }
                }, 1738);
                return textView;
            }
        });
        mTextSwitcher.setInAnimation(this, android.R.anim.fade_in);
        mTextSwitcher.setOutAnimation(this, android.R.anim.fade_out);
        if (title != null) setTitle(title);
    }

    private void initWebView() {
        mWebView = new WebView(getApplicationContext());
        FrameLayout.LayoutParams layoutParams=new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        web_frame_layout.addView(mWebView,layoutParams);
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setDomStorageEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setSupportZoom(true);
        mWebView.setWebChromeClient(new ChromeClient());
        mWebView.setWebViewClient(new ZJClient());
        mWebView.loadUrl(ApiModel.baseUrl + url);
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        mTextSwitcher.setText(title);
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            web_frame_layout.removeAllViews();
            mWebView.stopLoading();
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView = null;
        };
        ButterKnife.unbind(this);
        super.onDestroy();
    }


    @Override
    protected void onPause() {
        if (mWebView != null) mWebView.onPause();
        super.onPause();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mWebView != null)
            mWebView.onResume();
    }

    private class ChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            mProgressbar.setProgress(newProgress);
            if (newProgress == 100) {
                mProgressbar.setVisibility(View.GONE);
            } else {
                mProgressbar.setVisibility(View.VISIBLE);
            }
        }


        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            setTitle(title);
        }
    }

    private class ZJClient extends WebViewClient {

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url != null) view.loadUrl(url);
            return true;
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            WebResourceResponse response = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                response = getResponseByUrl(url);
            }
            return response;
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            return getResponseByUrl(url);
        }

        private WebResourceResponse getResponseByUrl(String url) {
            WebResourceResponse response = null;
            if (url.endsWith("style.css")) {
                response = getResponseByPath("css/article_style.css");
            } else if (url.endsWith("syntax-highlighting.css")) {
                response = getResponseByPath("css/syntax-highlighting.css");
            } else if (url.endsWith("glyphicons-halflings-regular.eot")) {
                response = getResponseByPath("bootstrap-3.3.5/fonts/glyphicons-halflings-regular.eot");
            } else if (url.endsWith("glyphicons-halflings-regular.svg")) {
                response = getResponseByPath("bootstrap-3.3.5/fonts/glyphicons-halflings-regular.svg");
            } else if (url.endsWith("glyphicons-halflings-regular.ttf")) {
                response = getResponseByPath("bootstrap-3.3.5/fonts/glyphicons-halflings-regular.ttf");
            } else if (url.endsWith("glyphicons-halflings-regular.woff")) {
                response = getResponseByPath("bootstrap-3.3.5/fonts/glyphicons-halflings-regular.woff");
            } else if (url.endsWith("glyphicons-halflings-regular.woff2")) {
                response = getResponseByPath("bootstrap-3.3.5/fonts/glyphicons-halflings-regular.woff2");
            } else if (url.endsWith("bootstrap.min.css")) {
                response = getResponseByPath("bootstrap-3.3.5/css/bootstrap.min.css");
            } else if (url.endsWith("bootstrap.min.js")) {
                response = getResponseByPath("bootstrap-3.3.5/js/bootstrap.min.js");
            }else if (url.endsWith("hdbg01.jpg")) {
                response = getResponseByPath("bootstrap-3.3.5/js/bootstrap.min.js");
            }
            return response;
        }

        private WebResourceResponse getResponseByPath(String path) {
            try {
                String mimeType = "application/octet-stream";
                if (path.endsWith("css")) {
                    mimeType = "text/css";
                } else if (path.endsWith("js")) {
                    mimeType = "application/javascript";
                } else if (path.endsWith("png")) {
                    mimeType = "image/png";
                } else if (path.endsWith("jpg") || path.endsWith("jpeg")) {
                    mimeType = "image/jpeg";
                }
                InputStream is = getAssets().open(path);
                return new WebResourceResponse(mimeType, "UTF-8", is);
            } catch (IOException e) {
                return null;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_markdown, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.close_enter, R.anim.close_exit);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_refresh:
                mWebView.reload();
                return true;
            case R.id.action_copy_url:
                Androids.copyToClipBoard(this, mWebView.getUrl(), "复制成功");
                return true;
            case R.id.action_open_url:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                Uri uri = Uri.parse(ApiModel.baseUrl + url);
                intent.setData(uri);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(content, "打开链接失败！", Toast.LENGTH_LONG).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
