Twitter4JAndroidLogin
=====================

Until Twitter decides to realease its offical SDK fro Android, you can use this simple android library for logging into twitter and retrieving the user information.

This library is using the famous [Twiiter4J library](http://twitter4j.org/en/), and inspired by [ppierson/T4JTwitterLogin library](https://github.com/ppierson/T4JTwitterLogin); so thank you [ppierson](https://github.com/ppierson)

This library allows you to retrive the Twitter object of Twitter4J after the authentication witch let you do all sort of things with the Twitter API.


###How To Login
--------------------

First, you need to add the following activity to your **AndroidManifest.xml**
```xml
<activity
		android:name="com.fadisdh.android.twitter4jlogin.TwitterLoginActivity"
		android:theme="@style/Twitter4jLoginTheme.NoTitleBar">
		<intent-filter>
    		<action android:name="android.intent.action.VIEW" />
    		<category android:name="android.intent.category.DEFAULT" />
    		<category android:name="android.intent.category.BROWSABLE" />
    		<data android:scheme="x-oauthflow-twitter" android:host="twitterlogin"/>
		</intent-filter>
</activity>
```

Next, Put this is your **onCreate()** method of the activity your trying to connect from
```java

TwitterAdapter ta = TwitterAdapter.getInstance();

TwitterAdapter.Callback callback = new TwitterAdapter.Callback{
 
  @override
  public void success(int type){
    //woohooo, you are in. Do stuff here 
  }
  
  @override
  public void failed(int type, Exception e){
    //the authentication failed. Do stuff here
  }
}
```

Next, Add this to your **onActivityResult()** method of the activity you are trying to connect from
```java
@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
  super.onActivityResult(requestCode, resultCode, data);
  TwitterAdapter.getInstance().onActivityResult(requestCode, resultCode, data);
}
```

You can connect to twitter using the follwong line
```java
  ta.connect();
```

or this, if you don't want to use the global callback you've just initiated
```java
  ta.connect(callback);
```


###How to retrieve user data
--------------------

to retrieve user data use the following code inside the **success()** method of the login callback
```java
TwitterAdapter.UserCallback userCallback = new TwitterAdapter.UserCallback{
  
  @override
  public void success(Twitter4J.User user){
    //use the user object to retrieve the user data
  }
  
  @override
  public void failed(Exception e){
    //failed, do stuff
  }
}

ta.getUser(userCallback);
```


###Get The Twitter Object and The AccessToken of Twitter4J library
--------------------

to get the **Twitter** object or the **AccessToken** object use the following code inside the **success()** method of the login callback
```java
Twitter t = ta.getTwitterInstance();
AccessToken at = ta.getAccessToken();
```
You can visit [Twiiter4J library](http://twitter4j.org/en/) website to know how to use the **Twitter object** to post a tweet, get the user timeline ... etc


###Don't Like it?
--------------------

If you dont like the interface of this library, you can always use [ppierson/T4JTwitterLogin library](https://github.com/ppierson/T4JTwitterLogin) witch this library was inspired from. Just dont forget to update the Twitter4J library there because it's too old.
