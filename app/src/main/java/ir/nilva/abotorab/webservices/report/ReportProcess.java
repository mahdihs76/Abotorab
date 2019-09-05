package ir.nilva.abotorab.webservices.report;

import ir.nilva.abotorab.webservices.base.BaseProcess;
import ir.nilva.abotorab.webservices.base.MyRetrofit;
import ir.nilva.abotorab.webservices.base.WebserviceException;

import java.io.IOException;

public class ReportProcess extends BaseProcess {
    @Override
    public ReportResponse process() throws IOException, WebserviceException {
        return send(MyRetrofit.getInstance().getWebserviceUrls().report());
    }
}
