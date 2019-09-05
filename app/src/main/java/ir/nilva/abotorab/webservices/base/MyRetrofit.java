package ir.nilva.abotorab.webservices.base;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ir.nilva.abotorab.helper.CacheHelperKt;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

import static ir.nilva.abotorab.webservices.base.WebserviceAdresses.BASE_URL;

public class MyRetrofit {

    private static MyRetrofit instance;

    public static MyRetrofit getInstance() {
        if (instance == null) {
            instance = new MyRetrofit();
        }
        return instance;
    }

    public static void newInstance() {
        instance = new MyRetrofit();
    }

    WebserviceUrls urls;

    public WebserviceUrls getWebserviceUrls() {
        if (urls == null) {
            urls = getUrls();
        }

        return urls;
    }

    public static WebserviceUrls getService(){
        return getInstance().getWebserviceUrls();
    }


    private WebserviceUrls getUrls() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(interceptor);
        addAuthHeader(builder);
        OkHttpClient client = builder.build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();
        return retrofit.create(WebserviceUrls.class);
    }


    private void addAuthHeader(OkHttpClient.Builder client) {
        final String token = CacheHelperKt.defaultCache().getString("token", "");
        if (token.isEmpty()) return;
        client.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();
                Request request = original.newBuilder()
                        .addHeader("Authorization", "JWT " + token)
                        .build();

                return chain.proceed(request);
            }
        });
    }

}
