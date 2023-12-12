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



# 打印混淆信息
-verbose
# 不对class进行优化，默认开启优化
-dontoptimize
# 不忽略非公共的库的类的成员
-dontskipnonpubliclibraryclassmembers
# 声明不进行压缩操作，默认除了-keep的类及其直接或间接引用到的类，都会被移除
-dontshrink
# 将.class信息中的类名重新定义为"SourceFile"字符串
-renamesourcefileattribute SourceFile
# 保留源文件名为"SourceFile"字符串，而非原始的类名 并保留行号
-keepattributes SourceFile, LineNumberTable, RuntimeVisible*Annotation*

-keepattributes RuntimeVisible*Annotation*,InnerClasses
-keepattributes Signature,EnclosingMethod

-dontwarn android.**
-dontwarn com.android.**
-dontwarn ref.**


-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.ContentProvider

-keepclassmembers class * extends java.lang.Enum {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}


-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}


-keep class androidx.** {*;}
-keep public class * extends androidx.**
-keep interface androidx.** {*;}
-keep class android.view.** {*;}
-keep class android.content.** {*;}
-keep class com.google.android.gms.auth.** { *; }
-keep class com.google.android.gms.common.** { *; }
#-keep class org.microg.gms.auth.signin.service.SignInChimeraService
#-keep class org.microg.dao.datas.ResponseParams {*;}
-dontwarn androidx.**


-keep class com.google.android.gms.droidguard.internal.**
-keep class com.google.android.gms.common.internal.**
-keep class com.google.android.gms.potokens.internal.**
#-keep class com.google.android.mg.proto.** {*;}
-keep class org.apache.commons.io.** {*;}
-keep class org.microg.gms.droidguard.** {*;}

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keep class com.google.gson.stream.** { *; }

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

-keep class com.google.gson.** { *; }
-keep class com.google.protobuf.** { *; }
