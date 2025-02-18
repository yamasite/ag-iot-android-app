package com.agora.iotlink.models.album;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.agora.iotlink.R;
import com.agora.iotlink.base.BaseViewBindingActivity;
import com.agora.iotlink.common.Constant;
import com.agora.iotlink.common.GlideApp;
import com.agora.iotlink.databinding.ActivityAlbumViewPhotoBinding;
import com.agora.iotlink.manager.PagePathConstant;
import com.agora.iotlink.widget.MakeUpZoomImage;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import kotlin.jvm.JvmField;

/**
 * 相册 截图预览
 */
@Route(path = PagePathConstant.pageAlbumViewPhoto)
public class AlbumViewPhotoActivity extends BaseViewBindingActivity<ActivityAlbumViewPhotoBinding> {
    @JvmField
    @Autowired(name = Constant.FILE_URL)
    String fileUrl;

    @JvmField
    @Autowired(name = Constant.TIME)
    String time;

    @Override
    protected ActivityAlbumViewPhotoBinding getViewBinding(@NonNull LayoutInflater inflater) {
        return ActivityAlbumViewPhotoBinding.inflate(inflater);
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        MakeUpZoomImage.attach(this);
        ARouter.getInstance().inject(this);
        GlideApp.with(this).load(fileUrl).placeholder(R.mipmap.icon_deft).into(getBinding().ivPhoto);
        getBinding().tvPhotoTime.setText(time);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MakeUpZoomImage.get().release();
    }
}
