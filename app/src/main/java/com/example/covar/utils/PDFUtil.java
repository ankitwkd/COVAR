package com.example.covar.utils;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.widget.Toast;
import android.content.Intent;


import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.covar.data.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

//https://www.geeksforgeeks.org/how-to-generate-a-pdf-file-in-android-app/
public class PDFUtil{

    // declaring width and height
    // for our PDF file.
    int pageHeight = 1120;
    int pagewidth = 792;

    // creating a bitmap variable
    // for storing our images
    Bitmap bmp, scaledbmp;

    User user;
    int blue;
    Context context;

    // constant code for runtime permissions
    private static final int PERMISSION_REQUEST_CODE = 200;
    private ArrayList<String> pdfText;

    public PDFUtil(Resources resources,
                   int image_id, User user, int blue, Context context, ArrayList<String> pdfText) {
        this.user = user;
        this.blue = blue;
        this.context = context;
        this.pdfText = pdfText;
        // initializing our variables.

        bmp = BitmapFactory.decodeResource(resources, image_id);
        scaledbmp = Bitmap.createScaledBitmap(bmp, 140, 140, false);

        // below code is used for
        // checking our permissions.
    }

    public boolean generatePDF() {
        // creating an object variable
        // for our PDF document.
        PdfDocument pdfDocument = new PdfDocument();

        // two variables for paint "paint" is used
        // for drawing shapes and we will use "title"
        // for adding text in our PDF file.
        Paint paint = new Paint();
        Paint title = new Paint();

        // we are adding page info to our PDF file
        // in which we will be passing our pageWidth,
        // pageHeight and number of pages and after that
        // we are calling it to create our PDF.
        PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pagewidth, pageHeight, 1).create();

        // below line is used for setting
        // start page for our PDF file.
        PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);

        // creating a variable for canvas
        // from our page of PDF.
        Canvas canvas = myPage.getCanvas();

        int xPos = 325;

        // below line is used for setting
        // our text to center of PDF.
        title.setTextAlign(Paint.Align.LEFT);

        // below line is used to draw our image on our PDF file.
        // the first parameter of our drawbitmap method is
        // our bitmap
        // second parameter is position from left
        // third parameter is position from top and last
        // one is our variable for paint.
        canvas.drawBitmap(scaledbmp, xPos, 40, paint);

        // below line is used for adding typeface for
        // our text which we will be adding in our PDF file.
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        // below line is used for setting text size
        // which we will be displaying in our PDF file.
        title.setTextSize(15);

        // below line is sued for setting color
        // of our text inside our PDF file.
        title.setColor(blue);


        // below line is used to draw text in our PDF file.
        // the first parameter is our text, second parameter
        // is position from start, third parameter is position from top
        // and then we are passing our variable of paint which is title.
        canvas.drawText("Name: " + user.getFullName(), xPos, 560, title);


        canvas.drawText("Age: " + user.getAge(), xPos, 580, title);
        canvas.drawText("Mobile: " + user.getMobileNum(), xPos, 600, title);

        canvas.drawText(pdfText.get(1), xPos, 640, title);

        canvas.drawText(pdfText.get(2), xPos, 680, title);

        canvas.drawText(pdfText.get(3), xPos, 700, title);

        // similarly we are creating another text and in this
        // we are aligning this text to center of our PDF file.
        title.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
        title.setColor(blue);
        title.setTextSize(15);

        // below line is used for setting
        // our text to center of PDF.
        title.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("© COVAR APP", 600, 1000, title);

        // after adding all attributes to our
        // PDF file we will be finishing our page.
        pdfDocument.finishPage(myPage);

        // below line is used to set the name of
        // our PDF file and its path.
        String timestamp = new Timestamp(System.currentTimeMillis()).toString();
        File directory = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        //File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        if (!directory.exists()){
            directory.mkdirs();
        }
        //creating new folder instance
        File file = new File(directory
, "VaccineDetails"+timestamp+".pdf");
        Toast.makeText(context, file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

        try {
            // after creating a file name we will
            // write our PDF file to that location.
            pdfDocument.writeTo(new FileOutputStream(file));

            // below line is to print toast message
            // on completion of PDF generation.
            pdfDocument.close();
            return true;
        } catch (IOException e) {
            // below line is used
            // to handle error
            e.printStackTrace();
        }
        // after storing our pdf to that
        // location we are closing our PDF file.
        pdfDocument.close();
        return false;
    }

    public boolean checkPermission() {
        // checking of permissions.
        int permission1 = ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions((Activity)context, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    private void createPdf(Activity a) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, "xyz");
        //a.startActivityForResult(intent, EXPORT_PDF_REQUEST_CODE);
    }
}
