package ir.nilva.abotorab.webservices.pref;

import android.content.Context;
import android.content.SharedPreferences;

public class WebservicePrefSetting {

    private static WebservicePrefSetting instance;

    private SharedPreferences sharedPreferences;

    private WebservicePrefSetting(Context context) {
        sharedPreferences = context.getSharedPreferences(PrefCostance.SHARED_NAME, Context.MODE_PRIVATE);
    }

    public static WebservicePrefSetting getInstance(Context context) {
        if (instance == null) {
            instance = new WebservicePrefSetting(context);
        }
        return instance;
    }

    public static WebservicePrefSetting getInstanceWithoutContext() {
        if (instance == null) {
            throw new RuntimeException("Please set context first");
        }
        return instance;
    }

    public boolean isRegister() {
        return sharedPreferences.getBoolean(PrefCostance.IS_REGISTER, false);
    }

    public void setRegister(boolean register) {
        sharedPreferences.edit().putBoolean(PrefCostance.IS_REGISTER, register).apply();

    }

    public void saveToken(String key) {
        sharedPreferences.edit().putString(PrefCostance.SHARED_KEY, key).apply();

    }

    public String getToken() {
        return sharedPreferences.getString(PrefCostance.SHARED_KEY, "");
    }
}
