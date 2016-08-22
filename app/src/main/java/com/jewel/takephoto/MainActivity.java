package com.jewel.takephoto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.jewel.library.ExtendAction;
import com.jewel.library.UserAction;
import com.jewel.library.UserActionImpl;

import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ExtendAction {


	UserAction action;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		action = new UserActionImpl(this, this);

		findViewById(R.id.btn_takePhoto).setOnClickListener(this);
		findViewById(R.id.btn_pickPhoto).setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btn_pickPhoto:
				action.pickPicture();
				break;
			case R.id.btn_takePhoto:
				action.takePicture();
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		action.onPictureResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void SDCardInexistent() {
		Toast.makeText(MainActivity.this, "SD卡不存在,无法存储图片", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void uriPictureInexistent() {
		Toast.makeText(MainActivity.this, "存储图片找不到,请重试", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void cropPictureResult(Uri cropUri) {
		try {
			Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(cropUri));
			((ImageView)findViewById(R.id.img_crop)).setImageBitmap(bitmap);
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
