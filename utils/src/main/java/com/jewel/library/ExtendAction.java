package com.jewel.library;

import android.net.Uri;

/**
 * @author Jewel
 * @version 1.0
 * @since 2016/8/16 0016
 *
 * 扩展接口。对拍照或选取图片过程的逻辑扩展，可继承该接口扩展自己的逻辑，用户必须实现这个接口
 */
public interface ExtendAction {

	/**
	 * 拍照后发现SD卡不存在的回调
	 */
	void SDCardInexistent();

	/**
	 * 选择图片后发现Uri不存在的回调
	 */
	void uriPictureInexistent();

	/**
	 * 裁剪图片后的回调
	 */
	void cropPictureResult(Uri cropUri);
}
