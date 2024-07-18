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

# Platform calls Class.forName on types which do not exist on Android to determine platform.


-keep public class * extends java.lang.Exception
-keep class androidx.* {*;}
-dontwarn retrofit.**
-keep class retrofit.* { *; }
-keep class com.google.android.* {*;}
-keep class androidx.core.app.CoreComponentFactory { *; }
-keep class android.content.Context.*{*;}
-keep class android.content.Intent.*{*;}
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-keepattributes Exceptions,InnerClasses,Signature


-keep public class * extends java.lang.Exception
-keep class androidx.* {*;}
-dontwarn retrofit.**
-keep class retrofit.* { *; }
-keep class com.google.android.* {*;}
-keep class androidx.core.app.CoreComponentFactory { *; }
-keep class android.content.Context.*{*;}
-keep class android.content.Intent.*{*;}
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-keepattributes Exceptions,InnerClasses,Signature



# Keep Room database, entities, DAO, and migration classes
-keep class androidx.room.RoomDatabase { *; }
-keep class androidx.room.migration.Migration { *; }
-keep class androidx.room.Entity { *; }
-keep class androidx.room.Database { *; }
-keep class androidx.room.Dao { *; }
-keep class androidx.room.Embedded { *; }
-keep class androidx.room.PrimaryKey { *; }
-keep class androidx.room.Relation { *; }
-keep class androidx.room.ColumnInfo { *; }
-keep class androidx.room.Ignore { *; }

# Keep methods annotated with Room annotations
-keepclassmembers class ** {
    @androidx.room.* <methods>;
}

# Keep generated classes by Room
-keep class **_Impl { *; }


