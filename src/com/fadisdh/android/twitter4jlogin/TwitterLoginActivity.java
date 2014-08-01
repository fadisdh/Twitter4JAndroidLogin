package com.fadisdh.android.twitter4jlogin;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class TwitterLoginActivity extends Activity {
	public static final String INTENT_EXTRA_TWITTER_ADAPTER = "twitter_adapter";
	
	public static final int RESULT_CODE_SUCCESS = 1;
	public static final int RESULT_CODE_FAILURE = 2;
	
	private WebView webView;
	private View overlayView;

	private TwitterAdapter twitterAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_twitter_login);
		
		twitterAdapter = TwitterAdapter.getInstance();
		if(twitterAdapter == null){
			setResult(RESULT_CODE_FAILURE);
			finish();
		}
		
		if(twitterAdapter.getAppKey() == null || twitterAdapter.getAppSecret() == null){
			Log.e(TwitterAdapter.TAG, "ERROR: Consumer Key and Consumer Secret required!");
        	TwitterLoginActivity.this.setResult(RESULT_CODE_FAILURE);
        	TwitterLoginActivity.this.finish();
		}
		
		overlayView = findViewById(R.id.twitter_login_overlay);
		overlayView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View view, MotionEvent motion) {
				view.performClick();
				return true;
			}
		});
		
		webView = (WebView)findViewById(R.id.twitter_login_web_view);
		webView.setWebViewClient( new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                if( url.contains(TwitterAdapter.TWITTER_CALLBACK_URL))
                {
                    Uri uri = Uri.parse(url);
                    twitterAdapter.getOAuthToken(uri, callback);
                    return true;
                }
                return false;
            }
            
            @Override
            public void onPageFinished(WebView view, String url) {
            	super.onPageFinished(view, url);
            	hideProgressbar();
            }
            
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
            	super.onPageStarted(view, url, favicon);
            	showProgressbar();
            }
        });

    	twitterAdapter.getOAuthRequestToken(callback);
	}
	
	private TwitterAdapter.Callback callback = new TwitterAdapter.Callback() {
		
		@Override
		public void success(int type) {
			switch (type) {
			case TwitterAdapter.CallbackType.OAUTH_REQUEST:
				oAuthRequestUrlSuccess();
				break;
			case TwitterAdapter.CallbackType.OAUTH_RESULT:
				oAuthRequestTokenSuccess();
				break;
			default:
				break;
			}
		}
		
		@Override
		public void failed(int type, Exception exception) {
			switch (type) {
			case TwitterAdapter.CallbackType.OAUTH_REQUEST:
				oAuthRequestUrlFailed();
				break;
			case TwitterAdapter.CallbackType.OAUTH_RESULT:
				oAuthRequestTokenFailed();
				break;
			default:
				break;
			}
		}
	};
	
	public void oAuthRequestUrlSuccess(){
		webView.loadUrl(twitterAdapter.getRequestToken().getAuthenticationURL());
	}
	
	public void oAuthRequestUrlFailed(){
		Log.e(TwitterAdapter.TAG, "Error in OAuth Request URL");
		setResult(RESULT_CODE_FAILURE);
		finish();
	}
	
	public void oAuthRequestTokenSuccess(){
		setResult(RESULT_CODE_SUCCESS);
		finish();
	}
	
	public void oAuthRequestTokenFailed(){
		Log.e(TwitterAdapter.TAG, "Error in OAuth Request Token");
		setResult(RESULT_CODE_FAILURE);
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		if(twitterAdapter == null){
			setResult(RESULT_CODE_FAILURE);
			finish();
		}
	}
	
	private void showProgressbar(){
		overlayView.setVisibility(View.VISIBLE);
	}
	
	private void hideProgressbar(){
		overlayView.setVisibility(View.INVISIBLE);
	}
}