package ir.nilva.abotorab.webservices.base;

import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

public abstract class BaseProcess {

    protected <T> T send(Call<T> call) throws IOException, WebserviceException {
        Response<T> execute = call.execute();
        if (execute.code() > 299 || execute.code() < 200) {
            throw new WebserviceException(execute.code(), execute.errorBody().string());
        }
        T body = execute.body();
        return body;
    }

    public abstract <T> T process() throws IOException, WebserviceException;

}
