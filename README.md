Twitter4JAndroidLogin
=====================

Until Twitter decides to realease an offical SDK for Android, you can use this simple android library for logging into twitter and retrieving the user information.

This library is using the famous [Twiiter4J library](http://twitter4j.org/en/), and inspired by [ppierson/T4JTwitterLogin library](https://github.com/ppierson/T4JTwitterLogin); many thanks to [ppierson](https://github.com/ppierson)

This library allows you to retrive the **Twitter** object of Twitter4J after the authentication witch let you do all sort of things with the Twitter API.


###How To Login
--------------------

First of all, you need to register the following activity in your **AndroidManifest.xml**
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

Next, put this in your **onActivityResult()** method of the activity you are trying to connect from
```java
@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
  super.onActivityResult(requestCode, resultCode, data);
  TwitterAdapter.getInstance().onActivityResult(requestCode, resultCode, data);
}
```

Finally, register your callback in the **onCreate()** method from the same activity. This callback will be called once you logged in. so all your twitter related code should be in the **success()** method of this callback.
Don't forget to change *{Your App Key}* and your *{Your App Secret}* to your actual twitter app key and secret.
```java

TwitterAdapter ta = TwitterAdapter.getInstance(this, "{Your App Key}", "{Your App Secret}");
TwitterAdapter.Callback callback = new TwitterAdapter.Callback(){
 
  @Override
  public void success(int type){
    //woohooo, you are logged in. Do stuff here 
  }
  
  @Override
  public void failed(int type, Exception e){
    //the authentication failed. Do stuff here
  }
};
```

You can connect to twitter using **connect()** or **connect(callback)**
```java
ta.connect();
```


###How to Retrieve The User Data
--------------------

To retrieve user data use the following code. Remember, this code should be called from inside the **success()** method of the login callback
```java
ta.getUser(new TwitterAdapter.UserCallback(){
  
  @Override
  public void success(twitter4j.User user){
    //use the user object to retrieve the user data
  }
  
  @Override
  public void failed(Exception e){
    //failed, do stuff
  }
});
```


###Get The Twitter Object and The AccessToken of twitter4j library
--------------------

To get the **Twitter** object or the **AccessToken** object use the following code inside the **success()** method of the login callback
```java
Twitter t = ta.getTwitterInstance();
AccessToken at = ta.getAccessToken();
```
You can visit [Twiiter4J library](http://twitter4j.org/en/) website to know how to use the **Twitter object** to post a tweet, get the user timeline ... etc


###What's Next
--------------------

As you noticed, this library still needs a lot of work to be done. How about helping us?

###License
--------------------
This project is under [The MIT License](https://github.com/fadisdh/Twitter4JAndroidLogin/blob/master/LICENSE)
