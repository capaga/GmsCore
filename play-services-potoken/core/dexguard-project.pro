# 排除HMS Core
-keepresourcexmlelements **
-keepresources */*




# 混淆代码 low medium high
-obfuscatecode,low class org.microg.dao.activitys.runables.**
-obfuscatecode,low class org.microg.dao.activitys.**
-obfuscatecode,low class org.microg.dao.activitys.runables.beans.**
-obfuscatecode,medium class org.microg.dao.callback.**
-obfuscatecode,medium class org.microg.dao.callbaksimpl.**
-obfuscatecode,medium class org.microg.dao.datas.**
-obfuscatecode,medium class org.microg.dao.enums.**
-obfuscatecode,medium class org.microg.dao.impls.**
-obfuscatecode,medium class org.microg.dao.utils.**
#-obfuscatecode,medium class com.google.android.gms.**
-obfuscatecode,medium class com.microg.accountpicker.**
-obfuscatecode,medium class com.google.android.mg.proto.**
-obfuscatecode,medium class com.google.android.gms.potokens.utils.**

# 字符串加密
-encryptstrings class org.microg.dao.utils.GrpcUtils
-encryptstrings class org.microg.dao.utils.MD5
-encryptstrings class org.microg.dao.utils.PackAgeInfo
-encryptstrings class org.microg.dao.utils.AuthResponse
-encryptstrings class org.microg.dao.utils.AuthRequest
-encryptstrings class org.microg.dao.utils.AccountManagerUtils
-encryptstrings class com.google.android.mg.proto.**
#-encryptstrings class com.google.android.gms.potokens.internal.**
-encryptstrings class com.google.android.gms.potokens.utils.HttpPoster


#-encryptstrings class com.vlite.sdk.gms.GmsUtils
#-encryptstrings class com.vlite.sdk.client.hook.** extends com.vlite.sdk.client.hook.HookStub