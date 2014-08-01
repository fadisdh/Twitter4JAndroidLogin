Twitter4JAndroidLogin
=====================

Until Twitter decides to realease its offical SDK fro Android, you can use this simple android library for logging into twitter and retrieving the user information.

This library is using the famus [Twiiter4J library](http://twitter4j.org/en/) and inspired by [ppierson/T4JTwitterLogin library](https://github.com/ppierson/T4JTwitterLogin)

This library allows you to retrive the Twitter object of Twitter4J after the authentication witch let you do all sort of things with the Twitter API.


##How To Use

```java
TwitterAdapter ta = TwitterAdapter.getInstance();
ta.setCallback(callback);
ta.connect();
```
