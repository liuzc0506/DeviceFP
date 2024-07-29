package com.ashenone.dfp.collector;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CanvasCollector {
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getCanvasFeature(String key){
        String s = "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";
        byte[] decode = Base64.getDecoder().decode(s);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decode,0,decode.length);
        Bitmap copy = bitmap.copy(Bitmap.Config.valueOf("RGB_565"), true);
        int width = copy.getWidth();
        int height = copy.getHeight();
        Canvas canvas = new Canvas(copy);
        RadialGradient radialGradient = new RadialGradient(0,0,1,0,0,Shader.TileMode.valueOf("CLAMP"));
        Paint paint = new Paint();
        paint.setShader(radialGradient);
        canvas.drawRect(0,0,width,height,paint);
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.valueOf("FILL_AND_STROKE"));
        paint.setTextAlign(Paint.Align.valueOf("LEFT"));
        canvas.drawText(key,0,0,paint);
        int byteCount = copy.getByteCount();
        ByteBuffer byteBuffer = ByteBuffer.allocate(byteCount);
        copy.copyPixelsToBuffer(byteBuffer);
        return Base64.getEncoder().encodeToString(byteBuffer.array());
    }
}
