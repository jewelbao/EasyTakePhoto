package com.jewel.library;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;

/**
 * @author Jewel
 * @version 1.0
 * @since 2016/8/16 0016
 */
public class UserActionImpl implements UserAction {

	// 拍照请求
	public static final int REQUEST_CODE_CAMERA = 1;
	// 选取图片请求
	public static final int REQUEST_CODE_PICK = 2;
	// 裁剪图片结果
	public static final int RESULT_CODE_CROP = 3;

	// 拍照后保存的图片URI
	private Uri uri4TakePicture;
	// 裁剪后保存的图片URI
	private Uri uri4CropPicture;

	private Context mContext;
	private ExtendAction extendAction;

	/**
	 * 默认构造器
	 *
	 * @param context      Activity上下文，切勿使用Application上下文
	 * @param extendAction 扩展接口，对失败信息和裁剪后的处理逻辑，用户自行实现。
	 */
	public UserActionImpl(Context context, @NonNull ExtendAction extendAction) {
		this.mContext = context;
		this.extendAction = extendAction;

		this.uri4TakePicture = Uri.fromFile(new File(context.getExternalCacheDir(), "takePicture.jpg"));
		this.uri4CropPicture = Uri.fromFile(new File(context.getExternalCacheDir(), "cropPicture.jpg"));
	}

	/**
	 * 扩展构造器,用户自行设置拍照后的图片保存路径和裁剪图片保存路径。设置为null则使用默认配置路径
	 *
	 * @param context        Activity上下文，切勿使用Application上下文
	 * @param takePictureUri 拍照图片保存路径Uri
	 * @param cropPictureUri 裁剪图片保存路径Uri
	 * @param extendAction   扩展接口，对失败信息和裁剪后的处理逻辑，用户自行实现。
	 */
	public UserActionImpl(Context context, @Nullable Uri takePictureUri, @Nullable Uri cropPictureUri, @NonNull ExtendAction extendAction) {
		this.mContext = context;
		this.extendAction = extendAction;

		this.uri4TakePicture = takePictureUri == null ? Uri.fromFile(new File(context.getExternalCacheDir(), "takePicture.jpg")) : takePictureUri;
		this.uri4CropPicture = cropPictureUri == null ? Uri.fromFile(new File(context.getExternalCacheDir(), "cropPicture.jpg")) : cropPictureUri;
	}

	/**
	 * 判断sdcard是否存在
	 */
	private boolean hasSdcard() {
		// 判断Sdcard是否可用
		if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else
			return false;
	}

	@Override
	public void takePicture() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if(hasSdcard()) {
			intent.putExtra("return-data", false);  // 不要返回系统的数据，大于1M的图片容易导致oom
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri4TakePicture);  // 为了解决oom，选择自己的图片保存路径
			intent.putExtra("noFaceDetection", true); // 不要人脸识别
		}
		((Activity) mContext).startActivityForResult(intent, REQUEST_CODE_CAMERA);
	}

	@Override
	public void pickPicture() {
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
		((Activity) mContext).startActivityForResult(intent, REQUEST_CODE_PICK);
	}

	@Override
	public void cropPicture(Uri uri) {
		if(uri == null) {
			// uri不存在
			if(extendAction != null) {
				extendAction.uriPictureInexistent();
			} else {
				throw new RuntimeException("Uri for Picture get null.");
			}
			return;
		}
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true"); // 设置裁剪
		// 设置宽高比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("scale", true);
		// 设置宽高
		intent.putExtra("outputX", 100);
		intent.putExtra("outputY", 100);
		// 裁剪后保存到裁剪文件中URI
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri4CropPicture);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString()); // 返回JPEG格式
		intent.putExtra("noFaceDetection", true); // 不要人脸识别
		intent.putExtra("return-data", false);  // 不要返回数据,因为数据已经另外保存到Uri中了
		((Activity) mContext).startActivityForResult(intent, RESULT_CODE_CROP);
	}

	@Override
	public void onPictureResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
			case REQUEST_CODE_CAMERA:
				if(hasSdcard()) {
					cropPicture(uri4TakePicture);
				} else {
					// 木有存储卡，无法存储图片
					if(extendAction != null) {
						extendAction.SDCardInexistent();
					} else {
						throw new RuntimeException("Uri for Picture get null.");
					}
				}
				break;
			case REQUEST_CODE_PICK:
				if(data != null && data.getData() != null) {
					cropPicture(data.getData());
				} else {
					// 选择的图片数据不存在
					if(extendAction != null) {
						extendAction.uriPictureInexistent();
					} else {
						throw new RuntimeException("Uri for Picture get null.");
					}
				}
				break;
			case RESULT_CODE_CROP:
				if(data != null) {
					if(extendAction != null && uri4CropPicture != null) {
						extendAction.cropPictureResult(uri4CropPicture);
					} else {
						throw new RuntimeException("Uri for Picture get null.");
					}
				}
				break;
		}
	}
}
