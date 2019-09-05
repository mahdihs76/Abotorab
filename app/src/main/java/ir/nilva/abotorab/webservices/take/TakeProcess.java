package ir.nilva.abotorab.webservices.take;

import ir.nilva.abotorab.webservices.base.BaseProcess;
import ir.nilva.abotorab.webservices.base.MyRetrofit;
import ir.nilva.abotorab.webservices.base.WebserviceException;

import java.io.IOException;

public class TakeProcess extends BaseProcess {

    private String firstname;
    private String lastname;
    private String phoneNumber;
    private String country;
    private String passportId;
    private int bagCount;
    private int suitcaseCount;
    private int pramCount;

    public TakeProcess(String firstname, String lastname,
                       String phoneNumber, String country,
                       String passportId, int bagCount,
                       int suitcaseCount, int pramCount){
        this.firstname = firstname;
        this.lastname = lastname;
        this.phoneNumber = phoneNumber;
        this.country = country;
        this.passportId = passportId;
        this.bagCount = bagCount;
        this.suitcaseCount = suitcaseCount;
        this.pramCount = pramCount;
    }

    @Override
    public TakeResponse process() throws IOException, WebserviceException {
        return send(MyRetrofit.getInstance().getWebserviceUrls().take(firstname, lastname,
                phoneNumber, country, passportId, bagCount, suitcaseCount, pramCount));
    }
}
