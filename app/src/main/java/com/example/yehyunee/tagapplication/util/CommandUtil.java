package com.example.yehyunee.tagapplication.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Patterns;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import java.util.regex.Pattern;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * 공통 함수 정의
 */
public class CommandUtil {

    // 비밀번호 정규식
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$");

    /**
     * 이메일 유효성 검사
     */
    public static boolean isValidEmail(Context context, String email) {
        if (email.isEmpty()) {
            // 이메일 공백
            Toast.makeText(context, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, "이메일 형식이 불일치합니다.", Toast.LENGTH_SHORT).show();
            // 이메일 형식 불일치
            return false;
        } else {
            return true;
        }
    }

    /**
     * 비밀번호 유효성 검사
     */
    public static boolean isValidPasswd(Context context, String password) {
        if (password.isEmpty()) {
            // 비밀번호 공백
            Toast.makeText(context, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            Toast.makeText(context, "비밀번호 형식이 불일치합니다.", Toast.LENGTH_SHORT).show();
            // 비밀번호 형식 불일치
            return false;
        } else {
            return true;
        }
    }

    /**
     * 이미지 세팅
     */
    public static void setImage(Context context, String url, ImageView image) {
        Glide.with(context).
                load(url).
                centerCrop().
                crossFade().
                into(image);
    }

    /**
     * 이미지 세팅
     */
    public static void setCircleImage(final Context context, String url, final ImageView image) {
        Glide.with(context).
                load(url).
                asBitmap().
                centerCrop().
                into(new BitmapImageViewTarget(image) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        image.setImageDrawable(circularBitmapDrawable);
                    }
                });
    }

    public static void setProfileImage(final Context context,final ImageView image, final StorageReference storageReference) {
        Glide.with(context)
                .using(new FirebaseImageLoader())
                .load(storageReference)
                .asBitmap()
                .centerCrop()
                .into(new BitmapImageViewTarget(image) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        image.setImageDrawable(circularBitmapDrawable);
                    }
                });
    }

}
