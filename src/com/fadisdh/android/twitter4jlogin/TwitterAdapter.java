package com.fadisdh.android.twitter4jlogin;


import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.util.Log;

public class TwitterAdapter {
	public static final String TAG = "TwitterAdapter";

	public static final String PREFERENCE_NAME = "twitter_oauth";
	public static final String PREF_KEY_SECRET = "oauth_token_secret";
	public static final String PREF_KEY_TOKEN = "oauth_token";

	public static final String TWITTER_CALLBACK_URL = "x-oauthflow-twitter://twitterlogin";

	public static final String IEXTRA_AUTH_URL = "auth_url";
	public static final String IEXTRA_OAUTH_VERIFIER = "oauth_verifier";
	public static final String IEXTRA_OAUTH_TOKEN = "oauth_token";
	
	public static final int TWITTER_LOGIN_REQUEST_CODE = 0x2468;
	
	private Context context;
	private Activity activity;
	private String appKey;
	private String appSecret;
	
	private TwitterFactory twitterFactory;
	private Twitter twitter;
	private RequestToken requestToken;
	private AccessToken accessToken;
	
	private SharedPreferences sharedPrefs;
	private Callback loginCallback;
	
	private static TwitterAdapter instance;
	
	public static TwitterAdapter getInstance(){
		return instance;
	}
	
	public static TwitterAdapter getInstance(Context ctx, String key, String secret){
		if(instance == null){
			instance = new TwitterAdapter(ctx, key, secret);
			return instance;
		}
		
		return instance;
	}
	
	private TwitterAdapter(Context ctx, String key, String secret) {
		this.context = ctx;
		this.appKey = key;
		this.appSecret = secret;
		init();
	}
	
	private void init(){
		activity = (Activity) context;
		sharedPrefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
			
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder.setOAuthConsumerKey(appKey);
		configurationBuilder.setOAuthConsumerSecret(appSecret);
		Configuration configuration = configurationBuilder.build();
		twitterFactory = new TwitterFactory(configuration);
		
		String tokenKey = sharedPrefs.getString(PREF_KEY_TOKEN, null);
		String tokenSecret = sharedPrefs.getString(PREF_KEY_SECRET, null);
		
		accessToken = tokenKey != null ? new AccessToken(tokenKey, tokenSecret) : null;
		twitter = isConnected() ? twitterFactory.getInstance(accessToken) : twitterFactory.getInstance();
	}
	
	public boolean isConnected() {
		return accessToken != null && accessToken.getToken() != null; 
	}
	
	public void connect(){
		connect(loginCallback);
	}
	
	public void connect(Callback callback){
		loginCallback = callback;
		if(!isConnected()){
			openLoginActivity();
		}else{
			callback.success(CallbackType.OAUTH_RESULT);
		}
	}
	
	public void disconnect(){
		Editor e = sharedPrefs.edit();
        e.putString(PREF_KEY_TOKEN, null); 
        e.putString(PREF_KEY_SECRET, null);
        e.commit();
        
        accessToken = null;
        requestToken = null;
        twitter = twitterFactory.getInstance();
	}
	
	public void setLoginCallback(Callback callback){
		loginCallback = callback;
	}
	
	public void getOAuthRequestToken(final Callback callback) {
		if(requestToken != null){
			callback.success(CallbackType.OAUTH_REQUEST);
			return;
		}
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					requestToken = twitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
					activity.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							callback.success(CallbackType.OAUTH_REQUEST);
						}
					});
				} catch (Exception e) {
					final Exception exception = e;
					activity.runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							callback.failed(CallbackType.OAUTH_REQUEST, exception);
						}
					});
					return;
				}
			}
		}).start();
	}
	
	public void getOAuthToken(final Uri uri, final Callback callback){
		new Thread(new Runnable() {
			@Override
			public void run() {
				String verifier = uri.getQueryParameter(IEXTRA_OAUTH_VERIFIER);
	            try { 
	                accessToken = twitter.getOAuthAccessToken(requestToken, verifier); 
	                twitter.setOAuthAccessToken(accessToken);
	                saveAccessToken();
	                callback.success(CallbackType.OAUTH_RESULT);
		        } catch (Exception e) { 
		        	e.printStackTrace();
		        	callback.failed(CallbackType.OAUTH_RESULT, e);
				}
			}
		}).start();
	}
	
	private void saveAccessToken(){
		Editor e = sharedPrefs.edit();
        e.putString(PREF_KEY_TOKEN, accessToken.getToken()); 
        e.putString(PREF_KEY_SECRET, accessToken.getTokenSecret()); 
        e.commit();
	}
	
	private void openLoginActivity(){
		if(!isConnected()){
		   Intent twitterLoginIntent = new Intent(activity, TwitterLoginActivity.class);
		   activity.startActivityForResult(twitterLoginIntent, TWITTER_LOGIN_REQUEST_CODE);
		}
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(loginCallback != null && requestCode == TWITTER_LOGIN_REQUEST_CODE){
			if(resultCode == TwitterLoginActivity.RESULT_CODE_SUCCESS){
				loginCallback.success(CallbackType.OAUTH_RESULT);
			}else if(resultCode == TwitterLoginActivity.RESULT_CODE_FAILURE){
				loginCallback.failed(CallbackType.OAUTH_RESULT, null);
			}else{
				loginCallback.failed(CallbackType.OAUTH_RESULT, null);
			}
		}
	}
	
	public void getUser(final UserCallback callback){
		if(!isConnected()){
			callback.failed(new Exception("Twitter is not Connected yest, try TwiiterAdapter#connect first"));
		}
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try{
	            	final twitter4j.User user = twitter.showUser(twitter.getId());
	            	if(user != null){ 		
	            		((Activity) context).runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								callback.success(user);
							}
						});
	            	}
	            }catch(Exception e){
	            	Log.d("Twitter Login", "Error Retriving the user");
	            	callback.failed(e);
	            }
			}
		}).start();
	}
	
	public RequestToken getRequestToken(){
		return requestToken;
	}
	
	public AccessToken getAccessToken(){
		return accessToken;
	}
	
	public Twitter getTwitterInstance(){
		return twitter;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
	
	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}
	
	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}
	
	public static class CallbackType{
		public static final int OAUTH_REQUEST = 1;
		public static final int OAUTH_RESULT = 2;
	}
	
	public interface Callback{
		public void success(int type);
		public void failed(int type, Exception exception);
	}
	
	public interface UserCallback{
		public void success(twitter4j.User user);
		public void failed(Exception exception);
	}
}
