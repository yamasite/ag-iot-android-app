package com.agora.iotlink.models.album;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.agora.baselibrary.base.BaseDialog;
import com.agora.baselibrary.utils.ToastUtils;
import com.agora.iotlink.R;
import com.agora.iotlink.api.bean.AlbumBean;
import com.agora.iotlink.base.BaseViewBindingActivity;
import com.agora.iotlink.databinding.ActivityAlbumBinding;
import com.agora.iotlink.dialog.DeleteMediaTipDialog;
import com.agora.iotlink.manager.PagePathConstant;
import com.agora.iotlink.manager.PagePilotManager;
import com.agora.iotlink.utils.FileUtils;
import com.alibaba.android.arouter.facade.annotation.Route;

import java.util.ArrayList;
import java.util.List;

/**
 * 相册
 */
@Route(path = PagePathConstant.pageAlbum)
public class AlbumActivity extends BaseViewBindingActivity<ActivityAlbumBinding> {
    private AlbumAdapter albumAdapter;
    private ArrayList<AlbumBean> albumData = new ArrayList<>();

    /**
     * 删除对话框
     */
    private DeleteMediaTipDialog deleteMediaTipDialog;

    @Override
    protected ActivityAlbumBinding getViewBinding(@NonNull LayoutInflater inflater) {
        return ActivityAlbumBinding.inflate(inflater);
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        albumAdapter = new AlbumAdapter(albumData);
        getBinding().rvMediaList.setAdapter(albumAdapter);
        GridLayoutManager glm = new GridLayoutManager(this, 3);
        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int type = albumData.get(position).itemType;
                if (type == 0) {
                    return glm.getSpanCount();
                }
                return 1;
            }
        });
        getBinding().rvMediaList.setLayoutManager(glm);
    }

    @Override
    public void initListener() {
        getBinding().titleView.setRightIconClick(view -> {
            changeEditStatus(!albumAdapter.isEdit);
        });
        getBinding().cbAllSelect.setOnCheckedChangeListener((compoundButton, b) -> {
            for (AlbumBean albumBean : albumAdapter.getDatas()) {
                albumBean.isSelect = b;
            }
            albumAdapter.notifyDataSetChanged();
        });
        albumAdapter.setMRVItemClickListener((view, position, data) -> {
            if (data.mediaType == 0) {
                PagePilotManager.pageAlbumViewPhoto(data.mediaCover, data.date + " " + data.time);
            }
        });
        getBinding().btnDoDelete.setOnClickListener(view -> {
            showDeleteMediaTipDialog();
        });
    }

    private void showDeleteMediaTipDialog() {
        if (deleteMediaTipDialog == null) {
            deleteMediaTipDialog = new DeleteMediaTipDialog(this);
            deleteMediaTipDialog.setOnButtonClickListener(new BaseDialog.OnButtonClickListener() {
                @Override
                public void onLeftButtonClick() {

                }

                @Override
                public void onRightButtonClick() {
                    List<AlbumBean> deletes = new ArrayList<>();
                    for (AlbumBean albumBean : albumAdapter.getDatas()) {
                        if (albumBean.isSelect) {
                            deletes.add(albumBean);
                        }
                    }
                    FileUtils.deleteFiles(deletes, (type, data) -> {
                        if (type == 0) {
                            getWindow().getDecorView().post(() -> {
                                ToastUtils.INSTANCE.showToast(getString(R.string.delete_success));
                                requestData();
                                changeEditStatus(false);
                            });

                        }
                    });
                }
            });
        }
        deleteMediaTipDialog.show();
    }

    private void changeEditStatus(boolean toEdit) {
        if (!toEdit) {
            getBinding().bgBottomDel.setVisibility(View.GONE);
            getBinding().titleView.setRightText(getString(R.string.edit));
        } else {
            getBinding().bgBottomDel.setVisibility(View.VISIBLE);
            getBinding().titleView.setRightText(getString(R.string.finish));
        }
        albumAdapter.isEdit = !albumAdapter.isEdit;
        albumAdapter.notifyDataSetChanged();
    }

    @Override
    public void requestData() {
        albumData.clear();
        FileUtils.readMediaFilesFromSD((type, data) -> {
            if (type == 0 && data != null) {
                albumData.addAll(data);
                getBinding().rvMediaList.post(() -> {
                    albumAdapter.notifyItemInserted(data.size());
                });
            }
        });
    }
}
