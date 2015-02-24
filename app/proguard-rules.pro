-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-dontskipnonpubliclibraryclassmembers
-dontwarn android.support.**
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-dontwarn com.actionbarsherlock.internal.**

-dontwarn butterknife.internal.**
-keep class **$$ViewInjector { *; }
-keepnames class * { @butterknife.InjectView *;}

-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}

-keep class android.support.v4.app.** { *; }  
-keep interface android.support.v4.app.** { *; }  
-keep class com.actionbarsherlock.** { *; }  
-keep interface com.actionbarsherlock.** { *; }  

-keep public class cn.waps.** { *; }
-keep public interface cn.waps.** { *; }
-dontwarn cn.waps.AdView
-dontwarn cn.waps.OffersWebView
  
-keepattributes *Annotation* 

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep class com.hufeng.filemanager.settings.*Fragment
-keep class com.kanbox.filemanager.settings.*Fragment
-keep public class com.android.vending.licensing.ILicensingService
-keepattributes Exceptions, InnerClases, Signature, Deprecated, SourceFile, LineNumberTable, *Annotation*, EnclosingMethod

-dontwarn android.support.**

-keep class android.support.v4.** { 
	*; 
}

-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}

-keep public class com.hufeng.filemanager.R$*{
	public static final int *; 
}

-keep public class com.kanbox.filemanager.R$*{
	public static final int *; 
}

-keep public class com.belugamobile.filemanager.R$*{
	public static final int *;
}

-keep public class com.feedback.ui.ThreadView{
}

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**

-dontwarn rx.**
-dontwarn retrofit.**
-dontwarn okio.**
-keep class retrofit.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}
