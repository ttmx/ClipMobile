# Picasso
-dontwarn okio.**
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *; }
-keep interface com.squareup.okhttp3.** { *; }
-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.ParametersAreNonnullByDefault

# Crashlytics
#-keep class com.crashlytics.** { *; }
-keepattributes SourceFile,LineNumberTable

# Jsoup
-keeppackagenames org.jsoup.nodes

# For enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Allow obfuscation of android.support.v7.internal.view.menu.**
# to avoid problem on Samsung 4.2.2 devices with appcompat v21
# see https://code.google.com/p/android/issues/detail?id=78377
-keep class !android.support.v7.internal.view.menu.** { *; }