package com.jewel.library;

import android.content.Intent;
import android.net.Uri;

/**
 * @author Jewel
 * @version 1.0
 * @since 2016/8/16 0016
 *
 * 用户行为
 */
public interface UserAction {
	/**
	 * 拍照
	 */
	void takePicture();

	/**
	 * 选择图片
	 */
	void pickPicture();

	/**
	 * 裁剪图片
	 */
	void cropPicture(Uri uri);

	/**
	 * {@link UserAction#takePicture()}、{@link UserAction#pickPicture()}执行完的结果回调<p>
	 * 对应{@link android.app.Activity#onActivityResult(int, int, Intent)}
	 */
	void onPictureResult(int requestCode, int resultCode, Intent data);
}
