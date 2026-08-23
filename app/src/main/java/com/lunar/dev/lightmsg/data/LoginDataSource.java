package com.lunar.dev.lightmsg.data;

import android.app.Activity;
import android.app.Application;
import android.content.ContentProvider;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ApplicationInfo;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyInfo;
import android.security.keystore.KeyProperties;

import com.lunar.dev.lightmsg.R;
import com.lunar.dev.lightmsg.data.model.LoggedInUser;
import com.lunar.dev.lightmsg.ui.login.LoginActivity;

import java.io.IOException;
import java.util.logging.ConsoleHandler;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;

import android.content.pm.PackageManager;
import android.content.Context;
import android.webkit.ConsoleMessage;
import android.widget.Toast;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication
            PackageManager pm = LoginActivity.appContext().getPackageManager();
            boolean hasStrongBoxFeature = pm.hasSystemFeature(PackageManager.FEATURE_STRONGBOX_KEYSTORE, 100);
            if(hasStrongBoxFeature){
               // Toast.makeText(LoginActivity.appContext(), "STRONGBOX ENABLED YAY", Toast.LENGTH_LONG).show();

                KeyGenerator keyGenerator = KeyGenerator.getInstance(
                        KeyProperties.KEY_ALGORITHM_HMAC_SHA256, "AndroidKeyStore");
                keyGenerator.init(
                        new KeyGenParameterSpec.Builder("light.msg.hmac", KeyProperties.PURPOSE_ATTEST_KEY).setUnlockedDeviceRequired(true).setUserAuthenticationRequired(true).setUserPresenceRequired(true).setAttestKeyAlias(null).setAttestationChallenge(null).setIsStrongBoxBacked(true).build());
                SecretKey key = keyGenerator.generateKey();



                SecretKeyFactory factory = SecretKeyFactory.getInstance(key.getAlgorithm(), "AndroidKeyStore");
                KeyInfo keyInfo = (KeyInfo) factory.getKeySpec(key, KeyInfo.class);


               // Toast.makeText(LoginActivity.appContext(), keyInfo.getKeySize()+" "+ keyInfo.getSecurityLevel()+" "+ keyInfo.getPurposes(), Toast.LENGTH_LONG).show();
/*
                Mac mac = Mac.getInstance("HmacSHA256");
                mac.init(key);
*/

                LoggedInUser fakeUser =
                        new LoggedInUser(
                                "attKey",
                                keyInfo.toString()+" "+keyInfo.hashCode());
                return new Result.Success<>(fakeUser);

            } else{
                return new Result.Error(new IOException("Device does not support hardware secure keystores."));
            }




        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}