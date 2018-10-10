package com.example.yehyunee.tagapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.yehyunee.tagapplication.util.CommandUtil;
import com.example.yehyunee.tagapplication.util.PreshareUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileActivity extends Activity implements View.OnClickListener {

    private FirebaseStorage storage;
    private StorageReference storageReference;

    private Context mContext;
    private Activity mActivity;

    private RelativeLayout mProfilePhotoAdd;
    private ImageView mProfilePhoto;
    private EditText mTagName;
    private RelativeLayout mTagStart;
    private RelativeLayout mProfilePhotoAddText;

    private Uri downloadUri;
    private Uri photoUri;
    private String currentPhotoPath;//실제 사진 파일 경로
    String mImageCaptureName;//이미지 이름

    public final int GALLERY_CODE = 1111;
    public final int CAMERA_CODE = 2222;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        mContext = this;
        mActivity = this;

        mProfilePhotoAdd = (RelativeLayout) findViewById(R.id.tag_profile_photo_layout);
        mProfilePhotoAddText = (RelativeLayout) findViewById(R.id.photo_add_text);
        mProfilePhoto = (ImageView) findViewById(R.id.tag_profile_photo_in);
        mTagName = (EditText) findViewById(R.id.tag_name_text);
        mTagStart = (RelativeLayout) findViewById(R.id.tag_main_start_layout);

        mTagStart.setOnClickListener(this);
        mProfilePhotoAdd.setOnClickListener(this);
    }

    /**
     * 갤러리에서 사진 가져오기
     */
    private void selectGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_CODE);
    }

    /**
     * 카메라에서 사진 가져오기
     */
    private void selectPhoto() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {

                }
                if (photoFile != null) {
                    photoUri = FileProvider.getUriForFile(this, getPackageName(), photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(intent, CAMERA_CODE);
                }
            }

        }
    }

    /**
     * 카메라로 찍은 파일을 실제 파일로 생성한다.
     * (상단에 지정한 path이름과 일치해야 에러발생하지 않음)
     */
    private File createImageFile() throws IOException {
        File dir = new File(Environment.getExternalStorageDirectory() + "/path/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        mImageCaptureName = timeStamp + ".png";

        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/path/"

                + mImageCaptureName);
        currentPhotoPath = storageDir.getAbsolutePath();

        return storageDir;

    }

    private void sendPicture(Uri imgUri) {

        String imagePath = getRealPathFromURI(imgUri); // path 경로
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Uri file = Uri.fromFile(new File(imagePath));
        final StorageReference riversRef = storageReference.child("user/"+user.getUid()+"/profile/"+file.getLastPathSegment());

        UploadTask uploadTask = riversRef.putFile(file);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, "이미지 업로드 실패", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                downloadUri = Uri.parse(riversRef.getPath());
            }
        });

//        ExifInterface exif = null;
//        try {
//            exif = new ExifInterface(imagePath);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//        int exifDegree = exifOrientationToDegrees(exifOrientation);
//
//        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);//경로를 통해 비트맵으로 전환

        CommandUtil.setCircleImage(mContext, imagePath, mProfilePhoto);
        mProfilePhotoAddText.setVisibility(View.GONE);
        mProfilePhoto.setVisibility(View.VISIBLE);
        mProfilePhoto.bringToFront();

    }

    /**
     * 사진의 회전값 가져오기
     *
     * @param exifOrientation
     * @return
     */
    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    /**
     * 사진의 절대경로 구하기
     *
     * @param contentUri
     * @return
     */

    private String getRealPathFromURI(Uri contentUri) {
        int column_index = 0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }

        return cursor.getString(column_index);
    }

    /**
     * 사진을 정방향대로 회전하기
     *
     * @param src, degree
     * @return
     */

    private Bitmap rotate(Bitmap src, float degree) {

        // Matrix 객체 생성
        Matrix matrix = new Matrix();
        // 회전 각도 셋팅
        matrix.postRotate(degree);
        // 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(),
                src.getHeight(), matrix, true);
    }

    private void getPictureForPhoto() {
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(currentPhotoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation;
        int exifDegree;

        if (exif != null) {
            exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            exifDegree = exifOrientationToDegrees(exifOrientation);
        } else {
            exifDegree = 0;
        }
        mProfilePhoto.setImageBitmap(rotate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기
        mProfilePhotoAdd.setVisibility(View.GONE);
        mProfilePhoto.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case GALLERY_CODE:
                    sendPicture(data.getData()); //갤러리에서 가져오기
                    break;
                case CAMERA_CODE:
                    getPictureForPhoto(); //카메라에서 가져오기
                    break;
                default:
                    break;
            }

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tag_profile_photo_layout:
                // 프로필 사진 추가하기
                if (ContextCompat.checkSelfPermission(mActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(mActivity,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(mActivity,
                            new String[]{
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            },
                            1052);

                } else {
                    selectGallery();
                }
                break;
            case R.id.tag_main_start_layout:
                // 시작하기
                if(mTagName.getText().toString().equals("")){
                    Toast.makeText(ProfileActivity.this, "태그네임을 설정해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    PreshareUtil.getInstance().setStringPref(mContext,"tagName", mTagName.getText().toString());

                    //db 연결 후 태그네임 중복검사
                    // 레퍼런스 -> /names/$uid:"name"
                    // names 테이블에서 name 중복검사 후 중복되지 않으면 names 테이블에 등록
                    // 데이터구조 -> $uid:"name"
                    // FirebaseUser user = mAuth.getcurrentUser()
                    // user.getUid()

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                            .setDisplayName(mTagName.getText().toString())
                            .setPhotoUri(downloadUri)
                            .build();

                    user.updateProfile(profileChangeRequest);

                    Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.ani_slide_in_left, R.anim.ani_slide_out_right);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ProfileActivity.this, EmailInitLoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.ani_slide_in_left, R.anim.ani_slide_out_right);
    }
}
