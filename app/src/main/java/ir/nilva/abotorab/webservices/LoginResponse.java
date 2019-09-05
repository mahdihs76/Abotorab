package ir.nilva.abotorab.webservices;

import com.google.gson.annotations.SerializedName;
import ir.nilva.abotorab.webservices.base.BaseResponse;

public class LoginResponse extends BaseResponse {

    @SerializedName("key")
    private String key;

    public LoginResponse(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
