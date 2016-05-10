package ggikko.me.imageputapp;

import java.io.File;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import rx.Observable;

/**
 * Created by admin on 16. 5. 2..
 */
public interface ImageUploadService {


    //TODO 따로 관리가 필요


//    @Multipart
    @PUT()
    Call<ImageUploadResult> sendFile(@Body RequestBody file);
//    Call<ImageUploadResult> sendFile(File requestBody);

    Retrofit retrofit = new Retrofit.Builder().baseUrl(baseURL)
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

}
