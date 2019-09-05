package ir.nilva.abotorab.webservices;

import ir.nilva.abotorab.webservices.base.BaseProcess;
import ir.nilva.abotorab.webservices.base.BaseResponse;
import ir.nilva.abotorab.webservices.base.MyRetrofit;
import ir.nilva.abotorab.webservices.base.WebserviceException;

import java.io.IOException;

public class AccountingProcess extends BaseProcess {

    private String firstName;
    private String lastName;
    private String email;
    private String password;

    AccountingProcess(String firstName, String lastName, String email, String password){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    @Override
    public BaseResponse process() throws IOException, WebserviceException {
        return send(MyRetrofit.getInstance().getWebserviceUrls().accounting(firstName, lastName, email, password));
    }
}
