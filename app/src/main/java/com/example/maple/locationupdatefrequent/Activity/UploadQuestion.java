package com.example.maple.locationupdatefrequent.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maple.locationupdatefrequent.Helper.DBHelper;
import com.example.maple.locationupdatefrequent.Models.UploadInstall;
import com.example.maple.locationupdatefrequent.R;
import com.example.maple.locationupdatefrequent.rest.ApiClient;
import com.example.maple.locationupdatefrequent.rest.ApiInterface;
import com.squareup.okhttp.MultipartBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.http.Part;
import retrofit2.http.Query;

public class UploadQuestion extends AppCompatActivity implements View.OnClickListener {
    ProgressDialog progress;
    File otherImagefile2_offline;
    String offlineimgpath2 = "";
    File otherImagefile2 = null;
    MultipartBody.Part imageFilePart2;
    Uri iv_url2 ;
    Button clickimage;
    ImageView ivOtherImage2;
    int O_IMAGE2=2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_question);
        ivOtherImage2 = findViewById(R.id.ivOtherImage2);
        clickimage = findViewById(R.id.clickimage);
        clickimage.setOnClickListener(this);
        ivOtherImage2.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.clickimage:
                 String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root + "/RecceImages/");
                myDir.mkdirs();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                otherImagefile2 = new File(myDir,
                        String.valueOf(System.currentTimeMillis()) + ".jpg");
               //  iv_url2 = Uri.fromFile(otherImagefile2);

               iv_url2= FileProvider.getUriForFile(getApplicationContext(),
                        getApplication().getPackageName() + ".provider", otherImagefile2);


                intent.putExtra(MediaStore.EXTRA_OUTPUT, iv_url2);
                startActivityForResult(intent, O_IMAGE2);
                break;

            case  R.id.ivOtherImage2:
                SharedPreferences s = getSharedPreferences("Userdetails", MODE_PRIVATE);
                imageFilePart2 = MultipartBody.Part.createFormData("Photo", otherImagefile2.getName(),
                        RequestBody.create(MediaType.parse("image"), otherImagefile2));
                offlineimgpath2 = otherImagefile2.getAbsolutePath();


                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                String millisInString = dateFormat.format(new Date());
                RequestBody Token = RequestBody.create(MediaType.parse("text/plain"),"VVD@14");
                RequestBody DeviceID = RequestBody.create(MediaType.parse("text/plain"), s.getString("DeviceId", "") );
                RequestBody MessageDescription = RequestBody.create(MediaType.parse("text/plain"),"dadi how are you ");
                RequestBody Long = RequestBody.create(MediaType.parse("text/plain"),"83.33");
                RequestBody Lat = RequestBody.create(MediaType.parse("text/plain"),"12.22");
                RequestBody ReportedFrom = RequestBody.create(MediaType.parse("text/plain"),s.getString("PersonName", ""));
                RequestBody ReportedDateTime = RequestBody.create(MediaType.parse("text/plain"),millisInString);
                RequestBody DR = RequestBody.create(MediaType.parse("text/plain"),"1");
                RequestBody MobileDeviceID = RequestBody.create(MediaType.parse("text/plain"),s.getString("MobileDeviceID", ""));
                updateInstall(Token, DeviceID,
                        MessageDescription, Long, Lat, ReportedFrom, ReportedDateTime, DR,MobileDeviceID,imageFilePart2);

                break;
        }
    }

    public void updateInstall(@Part("Token") RequestBody Token,
                              @Part("DeviceID") RequestBody DeviceID, @Part("MessageDescription") RequestBody MessageDescription,
                              @Part("Long") final RequestBody Long, @Part("Lat") final RequestBody Lat,
                              @Part("ReportedFrom") final RequestBody ReportedFrom, @Part("ReportedDateTime") final RequestBody ReportedDateTime,
                              @Part("DR") final RequestBody DR, @Part("MobileDeviceID") final RequestBody MobileDeviceID,
                              @Part final MultipartBody.Part imageFilePart2) {
        ApiInterface apiService = ApiClient.getSams().create(ApiInterface.class);
        retrofit2.Call<UploadInstall> call = apiService.getUploadInstall(Token, DeviceID, MessageDescription,
                Long, Lat, ReportedFrom, ReportedDateTime, DR,MobileDeviceID,imageFilePart2);
        call.enqueue(new retrofit2.Callback<UploadInstall>() {
            @Override
            public void onResponse(retrofit2.Call<UploadInstall> call, retrofit2.Response<UploadInstall> response) {
                String result = String.valueOf(response.code());

                Log.d("goodma",result+" "+offlineimgpath2);
                Log.d("goodma",offlineimgpath2+ "  ");

            }

            @Override
            public void onFailure(retrofit2.Call<UploadInstall> call, Throwable throwable) {
                //  Toast.makeText(getBaseContext(), throwable.toString(), Toast.LENGTH_SHORT).show();


                Log.d("message_image", throwable.toString());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

             try {
                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inSampleSize = 8;
                opt.inMutable = true;
                Bitmap bmImage = BitmapFactory.decodeFile(otherImagefile2.getPath().toString(), opt);
                ivOtherImage2.setScaleType(ImageView.ScaleType.FIT_XY);
                ivOtherImage2.setImageBitmap(bmImage);
                compressImage(otherImagefile2.getAbsolutePath().toString());
            } catch (Exception e) {
                Log.e("msg", e.getMessage());
            }

    }
    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        // String filename = getFilename();
        try {
            out = new FileOutputStream(imageUri);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 25, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return imageUri;

    }

}
