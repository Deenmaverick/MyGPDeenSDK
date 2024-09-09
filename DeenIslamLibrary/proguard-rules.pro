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


# Save the obfuscation mapping to a file, so we can de-obfuscate any stack
# traces later on. Keep a fixed source file attribute and all line number
# tables to get line numbers in the stack traces.
# You can comment this out if you're not interested in stack traces.

# Keep all Room database, entities, DAO, and migration classes
#-keep class com.deenislamic.sdk.views.gphome.* { *; }
#-keep class com.deenislamic.sdk.views.main.MainActivityDeenSDK { *; }


#-keep class com.deenislamic.sdk.views.allah99names.Allah99NamesFragment {
   # public <init>(...);
   # public android.view.View onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle);
    #public void onViewCreated(android.view.View, android.os.Bundle);
 #}

 #-keep class com.deenislamic.sdk.views.dashboard.DashboardFakeFragment {
    # public <init>(...);
    # public android.view.View onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle);
    # public void onViewCreated(android.view.View, android.os.Bundle);
 #}
#-keep class com.deenislamic.sdk.views.allah99names.* { *; }
#-keep class com.deenislamic.sdk.views.compass.* { *; }
#-keep class com.deenislamic.sdk.views.dailydua.* { *; }
#-keep class com.deenislamic.sdk.views.dashboard.* { *; }
#-keep class com.deenislamic.sdk.views.duaandamal.* { *; }
#-keep class com.deenislamic.sdk.views.hadith.* { *; }
#-keep class com.deenislamic.sdk.views.hajjandumrah.* { *; }
#-keep class com.deenislamic.sdk.views.ijtema.* { *; }
#-keep class com.deenislamic.sdk.views.islamicbook.* { *; }
#-keep class com.deenislamic.sdk.views.islamicboyan.* { *; }
#-keep class com.deenislamic.sdk.views.islamiceducationvideo.* { *; }
#-keep class com.deenislamic.sdk.views.islamicevent.* { *; }
#-keep class com.deenislamic.sdk.views.islamicname.* { *; }
#-keep class com.deenislamic.sdk.views.islamifazael.* { *; }
#-keep class com.deenislamic.sdk.views.islamimasaIl.* { *; }
#-keep class com.deenislamic.sdk.views.khatamquran.* { *; }
#-keep class com.deenislamic.sdk.views.more.* { *; }
#-keep class com.deenislamic.sdk.views.mydownloads.* { *; }
#-keep class com.deenislamic.sdk.views.myfavorites.* { *; }
#-keep class com.deenislamic.sdk.views.nearestmosque.* { *; }
#-keep class com.deenislamic.sdk.views.payment.* { *; }
#-keep class com.deenislamic.sdk.views.podcast.* { *; }
#-keep class com.deenislamic.sdk.views.prayerlearning.* { *; }
#-keep class com.deenislamic.sdk.views.prayertimes.* { *; }
#-keep class com.deenislamic.sdk.views.quran.* { *; }
#-keep class com.deenislamic.sdk.views.quran.learning.* { *; }
#-keep class com.deenislamic.sdk.views.qurbani.* { *; }
#-keep class com.deenislamic.sdk.views.ramadan.* { *; }
#-keep class com.deenislamic.sdk.views.share.* { *; }
#-keep class com.deenislamic.sdk.views.tasbeeh.* { *; }
#-keep class com.deenislamic.sdk.views.webview.* { *; }
#-keep class com.deenislamic.sdk.views.youtubevideo.* { *; }
#-keep class com.deenislamic.sdk.views.zakat.* { *; }


#Main
-keep class com.deenislamic.sdk.views.main.MainActivityDeenSDK { *; }

#GP Home

-keep class com.deenislamic.sdk.views.gphome.GPHome { *; }



# Dasboard
-keep class com.deenislamic.sdk.views.dashboard.** {
    public <init>(...);
    public android.view.View onCreateView(...);
    public void onViewCreated(android.view.View, android.os.Bundle);
}
-dontwarn com.deenislamic.sdk.views.dashboard.**
-assumenosideeffects class com.deenislamic.sdk.views.dashboard.** {
    *;
}

# Quran
-keep class com.deenislamic.sdk.views.quran.QuranFragment {
    public <init>(...);
    public android.view.View onCreateView(...);
    public void onViewCreated(android.view.View, android.os.Bundle);
}
-dontwarn com.deenislamic.sdk.views.quran.QuranFragment
-assumenosideeffects class com.deenislamic.sdk.views.quran.QuranFragment {
    *;
}

-keep class com.deenislamic.sdk.views.base.BaseFragment { *; }
-keep class com.deenislamic.sdk.views.base.BaseRegularFragment { *; }

# Blank Fragment
-keep class com.deenislamic.sdk.views.start.BlankFragment { *; }

-keep class com.deenislamic.sdk.views.base.BaseRegularFragment {
    public *;
}

# Keep all lifecycle methods in fragments
-keep class androidx.fragment.app.Fragment {
    public void onAttach(android.content.Context);
    public void onCreate(android.os.Bundle);
    public  onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle);
    public void onViewCreated(android.view.View, android.os.Bundle);
    public void onActivityCreated(android.os.Bundle);
    public void onDestroyView();
    public void onDetach();
}


-keepattributes *Annotation*

-keep @androidx.annotation.Keep class * { *; }
-printmapping mapping.txt



