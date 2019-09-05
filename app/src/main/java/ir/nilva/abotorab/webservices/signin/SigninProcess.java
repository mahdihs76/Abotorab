package ir.nilva.abotorab.webservices.signin;

import ir.nilva.abotorab.webservices.base.BaseProcess;
import ir.nilva.abotorab.webservices.base.MyRetrofit;
import ir.nilva.abotorab.webservices.base.WebserviceException;

import java.io.IOException;

public class SigninProcess extends BaseProcess {

    private String username;
    private String password;

    public SigninProcess(String username, String password){
        this.username = username;
        this.password = password;
    }

    @Override
    public SigninResponse process() throws IOException, WebserviceException {
        return send(MyRetrofit.getInstance().getWebserviceUrls().signin(username, password));
    }
}
