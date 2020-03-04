# ———————— 微信 ————————

-keep class com.tencent.mm.opensdk.** { *; }
-keep class com.tencent.wxop.** { *; }
-keep class com.tencent.mm.sdk.** { *; }

# ———————— qq ————————

-keep class * extends android.app.Dialog { *; }
-keep class com.tencent.open.TDialog$*
-keep class com.tencent.open.TDialog$* { *; }
-keep class com.tencent.open.PKDialog
-keep class com.tencent.open.PKDialog { *; }
-keep class com.tencent.open.PKDialog$*
-keep class com.tencent.open.PKDialog$* { *; }