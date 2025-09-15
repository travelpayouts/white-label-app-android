# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keepclassmembers,allowobfuscation class * {  @com.google.gson.annotations.SerializedName <fields>;}

# okHttp fix
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.*
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE

-keep,allowobfuscation,allowshrinking class okhttp3.RequestBody -keep,allowobfuscation,allowshrinking class okhttp3.ResponseBody

-keepattributes Signature
-keep class kotlin.coroutines.Continuation

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { <fields>; }

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Retain generic signatures of TypeToken and its subclasses with R8 version 3.0 and higher.
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken

##---------------End: proguard configuration for Gson  ----------

# https://github.com/square/okhttp/blob/339732e3a1b78be5d792860109047f68a011b5eb/okhttp/src/jvmMain/resources/META-INF/proguard/okhttp3.pro#L11-L14
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# TODO: Waiting for new retrofit release to remove these rules
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

-keep class * extends android.app.Activity { *; }

-keep,allowobfuscation,allowshrinking class com.travelapp.sdk.internal.network.utils.NetworkResponse
-keep,allowobfuscation,allowshrinking class com.travelapp.sdk.internal.network.utils.GenericErrorResponseBody

# region Travel SDK rules


-keep class com.travelapp.** { *; }


-dontwarn java.lang.invoke.StringConcatFactory
-dontwarn androidx.navigation.ui.R$anim
-dontwarn com.google.android.material.R$attr
-dontwarn com.google.android.material.R$id

-keep @kotlinx.parcelize.Parcelize public class * {
    *;
}

-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

-keep class * implements android.os.Parcelable$Creator { *; }

-keep class * extends java.lang.Enum {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

-keep class com.travelapp.sdk.internal.ui.base.** {
    <fields>;
    <methods>;
 }

 -keep class com.travelapp.sdk.hotels.utils.FoundHotel {
    *;
 }

 -keepnames class * extends com.travelapp.sdk.internal.ui.base.BaseFragment
 -keepnames class * extends com.travelapp.sdk.internal.ui.base.BaseBottomSheetDialogFragment

 -keep class com.travelapp.sdk.config.** { *; }
 -keep class com.travelapp.common.debug.** { *; }

-keep interface com.travelapp.sdk.internal.ui.utils.BottomBarVisibilityHandler {
    public <fields>;
    public <methods>;
}
-keep interface com.travelapp.sdk.internal.ui.utils.TabSelector {
    public <methods>;
}
-keep class com.travelapp.sdk.internal.ui.utils.NavigationExtensionsKt {
    *;
}
-keep class com.travelapp.sdk.internal.ui.utils.CommonExtensionsKt {
    *;
}
-keep class com.travelapp.sdk.internal.ui.utils.KeyboardVisibilityListener {
    public <methods>;
}

# endregion