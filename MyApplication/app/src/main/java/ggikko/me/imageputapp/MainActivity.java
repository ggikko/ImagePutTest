package ggikko.me.imageputapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.getfromlocal) Button getfromlocal;
    @Bind(R.id.getfromserver) Button getfromserver;
    @Bind(R.id.send) Button send;
    @Bind(R.id.image_one) ImageView image_one;
    @Bind(R.id.image_two)
    ImageView image_two;
    @Bind(R.id.base)
    EditText base;

    Uri uri;
    int height;
    int width;
    int resizedheight;
    int resizedWidth;

    private static final int REQUEST_CODE = 1000;



    Bitmap bitmap;

    @OnClick(R.id.getfromlocal)
    void getImageFromLocal() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @OnClick(R.id.getfromserver)
    void getImageFromServer() {
        Glide.with(this).load(TARGET_URL).into(image_two);
    }

    @OnClick(R.id.send)
    void sendImageToServer() {
        uploadFile(uri);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

//        try {
//            TARGET_URL url = new TARGET_URL(TARGET_URL);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setDoOutput(true);
//            connection.setRequestMethod("PUT");
//
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


//        Thread t = new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                try{
//
//                    HttpClient httpclient = new DefaultHttpClient();
//                    HttpPost httppost = new HttpPost("server-link/folder-name/upload_image.php");
//                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//                    HttpResponse response = httpclient.execute(httppost);
//                    String the_string_response = convertResponseToString(response);
//                    runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            Toast.makeText(UploadImage.this, "Response " + the_string_response, Toast.LENGTH_LONG).show();
//                        }
//                    });
//
//                }catch(Exception e){
//                    runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            Toast.makeText(UploadImage.this, "ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
//                        }
//                    });
//                    System.out.println("Error in http connection "+e.toString());
//                }
//            }
//        });
//        t.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK)
            try {
                // We need to recyle unused bitmaps
                if (bitmap != null) {
                    bitmap.recycle();
                }

                Log.e("ggikko", "get");

                uri = data.getData();

                InputStream stream = getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(stream);
                Display display = getWindowManager().getDefaultDisplay();
                int samplesize = calculateImageSize(bitmap, display.getWidth(), display.getHeight());


                height = bitmap.getHeight();
                width = bitmap.getWidth();
                resizedheight = height / samplesize;
                resizedWidth = width / samplesize;

                bitmap = Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedheight, true);

                image_one.setScaleType(ImageView.ScaleType.FIT_CENTER);
                image_one.setImageBitmap(bitmap);

                stream.close();
//                image_one.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private static int calculateImageSize(Bitmap bitmap, int reqWidth, int reqHeight) {

        final int height = bitmap.getHeight();
        final int width = bitmap.getWidth();

        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }

        inSampleSize = inSampleSize + 3;

        if (inSampleSize == 1) {
            return 1;
        }

        return inSampleSize;
    }

    private void uploadFile(Uri fileUri)  {
        // create upload service client
        ImageUploadService service = ImageUploadService.retrofit.create(ImageUploadService.class);

        // use the FileUtils to get the actual file by uri
        String realPathFromURI = getRealPathFromURI(this, fileUri);
        Log.e("ggikko", "real : " + realPathFromURI);
        File file = new File(realPathFromURI);
        File file2 = new File(fileUri.getPath());

        if (file != null) Log.e("ggikko", "file not null 1 : " + realPathFromURI);
        if (file2 != null) Log.e("ggikko", "file not null 2" + fileUri.getPath());


        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();
        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);


//        base.setText(encodedImage);
//        Log.d("ggikko", "string : " + encodedImage);
//        StringBuilder stringBuilder = new StringBuilder(encodedImage);
//        System.out.printf(stringBuilder.toString());
        // MultipartBody.Part is used to send also the actual file name
//        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        // add another part within the multipart request
//        String descriptionString = "hello, this is description speaking";
//        RequestBody description =
//                RequestBody.create(
//                        MediaType.parse("multipart/form-data"), descriptionString);
//        File file = new File(fileUri.getPath());
//        RequestBody fbody = RequestBody.create(MediaType.parse("image/*"), file);

        // finally, execute the request

//        try {
//            InputStream in = new FileInputStream(file);
//            byte[] buf;
//            buf = new byte[in.available()];
//            while (in.read(buf) != -1) ;
//
//            OkHttpClient okHttpClient = new OkHttpClient();

//            RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), buf);

//            Request request = new Request.Builder().url(TARGET_URL)
//                    .put(null)
//                    .addHeader("cache-control", "no-cache")
//                    .addHeader("postman-token", "6aad304c-a601-a7b7-484d-4d3a1ed0329f")
//                    .build();
//
//            okhttp3.Response execute = okHttpClient.newCall(request).execute();
//            Log.e("ggikko", execute.body().string());

            RequestBody requestBody1 = RequestBody.create(MediaType.parse("image/*"), file);

//            Call<ImageUploadResult> call = service.sendFile(b);

//            call.enqueue(new Callback<ImageUploadResult>() {
//                @Override
//                public void onResponse(Call<ImageUploadResult> call, Response<ImageUploadResult> response) {
//                    String errmsg = response.body().errmsg;
//                    Log.e("ggikko", "bababa");
//                    Log.e("ggikko", "error : " + errmsg);
//                }
//
//                @Override
//                public void onFailure(Call<ImageUploadResult> call, Throwable t) {
////                Log.e("Upload error:", t.getMessage());
//                    Log.e("Upload error:", "error");
//                }
//            });


            AsyncTask asyncTask = new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] params) {

                    try {
                    InputStream in = new FileInputStream(new File(realPathFromURI));
                    byte[] buf;
                    buf = new byte[in.available()];
                    while (in.read(buf) != -1) ;
                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), buf);

                    Call<ImageUploadResult> call = service.sendFile(requestBody);

                    call.enqueue(new Callback<ImageUploadResult>() {
                        @Override
                        public void onResponse(Call<ImageUploadResult> call, Response<ImageUploadResult> response) {
                            String errmsg = response.body().errmsg;
                            Log.e("ggikko", "bababa");
                            Log.e("ggikko", "error : " + errmsg);
                        }

                        @Override
                        public void onFailure(Call<ImageUploadResult> call, Throwable t) {
//                Log.e("Upload error:", t.getMessage());
                            Log.e("Upload error:", "error");
                        }
                    });

                    }catch(Exception e){
                        Log.e("ggikko", "IOEXCEPTION : " + e.toString());
                    }
                    return null;

                }
            }.execute();

        try {
            InputStream in = new FileInputStream(new File(realPathFromURI));
            byte[] buf;
            buf = new byte[in.available()];
            while (in.read(buf) != -1) ;

            Bitmap bitmap = BitmapFactory.decodeByteArray(buf, 0, buf.length);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedheight, false);

            image_two.setImageBitmap(scaledBitmap);

        }catch(Exception e){
            Log.e("ggikko", "IOEXCEPTION : " + e.toString());
        }

//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call,
//                                   Response<ResponseBody> response) {
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//            }
//        });
//
//        } catch (Exception e) {
//
//        }
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


}
