# Adapted from https://github.com/tommyettinger/CaveCops/

# === Needs changed for reuse ===

-keep public class agvoron.tetris.desktop.DesktopLauncher {
    public static void main(java.lang.String[]);
}

-keep public class !agvoron.tetris** { *; }

# === Generic reusable stuff ===

-keepattributes '*Annotation*'

-keepclasseswithmembernames class * { native <methods>; }

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements java.io.Serializable {
         static final long serialVersionUID;
         static final java.io.ObjectStreamField[] serialPersistentFields;
         private void writeObject(java.io.ObjectOutputStream);
         private void readObject(java.io.ObjectInputStream);
         java.lang.Object writeReplace();
         java.lang.Object readResolve();
     }

# LAMBDA FIX
-keepclassmembernames class * {
    private static synthetic *** lambda$*(...);
}

-verbose
-allowaccessmodification
-mergeinterfacesaggressively
-overloadaggressively
-repackageclasses

-forceprocessing
-ignorewarnings

-optimizationpasses 5
