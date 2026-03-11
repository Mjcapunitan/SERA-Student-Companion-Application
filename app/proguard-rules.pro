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
-keepclasseswithmembers class **.R$* {
    public static final int define_*;
}

# --- PDFBox Android ---
-dontwarn org.apache.pdfbox.**
-keep class org.apache.pdfbox.** { *; }

# --- Mammoth (for Word extraction) ---
-dontwarn org.zwobble.mammoth.**

# --- Ignore optional JPEG2000 decoder/encoder from PDFBox ---
-dontwarn com.gemalto.jp2.**

# --- Ignore optional LDAP/CRL lookup classes in BouncyCastle ---
-dontwarn javax.naming.**
-dontwarn javax.naming.directory.**

# --- Keep Gson related classes ---
-keep class com.google.gson.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**

# --- Keep classes that will be serialized/deserialized by Gson ---
-keep class com.example.sera.utils.JsonQuestion { *; }
-keep class com.example.sera.utils.GeneratedQuestion { *; }
-keep class com.example.sera.utils.QuestionOption { *; }

# --- Keep custom deserializers ---
-keep class com.example.sera.utils.QuestionGenerationService$JsonQuestionDeserializer { *; }

# --- Keep Gemini API related classes ---
-keep class com.google.ai.client.generativeai.** { *; }
-dontwarn com.google.ai.client.generativeai.**

# --- Keep fields names for reflection ---
-keepclassmembers class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# --- Room Database ---
-keep class androidx.room.** { *; }
-keepclassmembers class * extends androidx.room.RoomDatabase { *; }
-keepclassmembers @androidx.room.Entity class * { *; }
-keep class * extends androidx.room.TypeConverter { *; }

# --- Keep your quiz attempt models ---
-keep class com.example.sera.common.value_objects.entities.QuizAttemptEntity { *; }
-keep class com.example.sera.utils.QuizAttempt { *; }
-keep class com.example.sera.utils.QuestionResult { *; }
-keep class com.example.sera.utils.Converters

# --- Java Time API ---
-keep class java.time.** { *; }
-keep class kotlinx.datetime.** { *; }

-keep class com.example.sera.utils.QuestionResult { *; }

-keepclassmembers class com.example.sera.utils.QuizAttempt {
    <fields>;
}
-keepclassmembers class com.example.sera.utils.QuestionResult {
    <fields>;
}

-keepclassmembers class com.example.sera.utils.Converters {
    public <methods>;
}

-keepattributes Exceptions, InnerClasses, Signature, Deprecated, SourceFile, LineNumberTable, *Annotation*, EnclosingMethod
-keep class com.google.gson.TypeAdapter
-keep class * extends com.google.gson.TypeAdapter
-keep class com.google.gson.reflect.TypeToken
-keep class * extends com.google.gson.reflect.TypeToken

-keep class com.example.sera.utils.QuizAttempt$** { *; }
-keep class com.example.sera.utils.QuestionResult$** { *; }

# --- ML Kit Text Recognition ---
-keep class com.google.mlkit.** { *; }
-keep class com.google.android.gms.vision.** { *; }
-dontwarn com.google.mlkit.**
-dontwarn com.google.android.gms.vision.**

# --- TensorFlow Lite used by ML Kit ---
-keep class org.tensorflow.lite.** { *; }
-dontwarn org.tensorflow.lite.**

# --- ML Kit dependencies ---
-keep class com.google.android.datatransport.** { *; }
-dontwarn com.google.android.datatransport.**
-keep class com.google.firebase.ml.** { *; }
-dontwarn com.google.firebase.ml.**

# --- Performance Evaluation Service and related classes ---
-keep class com.example.sera.utils.PerformanceEvaluationService { *; }
-keep class com.example.sera.utils.PerformanceAssessment { *; }
-keep class com.example.sera.utils.StrengthsAndWeaknesses { *; }
-keep class com.example.sera.utils.QuizTypePerformance { *; }
-keep class com.example.sera.utils.TopicPerformance { *; }
-keep class com.example.sera.utils.ImprovementArea { *; }

# --- For custom exceptions in the service ---
-keep class com.example.sera.utils.QuotaExceededException { *; }

# --- JSON handling for assessment response parsing ---
-keepclassmembers class org.json.** { *; }
-dontwarn org.json.**



