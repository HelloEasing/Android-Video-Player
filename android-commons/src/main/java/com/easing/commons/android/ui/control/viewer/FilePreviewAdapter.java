package com.easing.commons.android.ui.control.viewer;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.easing.commons.android.R;
import com.easing.commons.android.manager.FileUtil;
import com.easing.commons.android.view.ViewManager;
import com.easing.commons.android.ui.adapter.RecyclerAdapter;
import com.easing.commons.android.ui.app.CommonActivity;
import com.easing.commons.android.manager.UriUtil;

import java.util.ArrayList;
import java.util.List;

public class FilePreviewAdapter extends RecyclerAdapter<FilePreviewBean, FilePreviewAdapter.Holder> {

    private CommonActivity context;

    public FilePreviewAdapter(RecyclerView rv, int itemLayout) {
        super(rv, itemLayout);
        context = (CommonActivity) rv.getContext();
    }

    public FilePreviewAdapter(RecyclerView rv, int itemLayout, List<FilePreviewBean> beans) {
        super(rv, itemLayout, beans);
        context = (CommonActivity) rv.getContext();
    }

    public List<String> getPaths() {
        List<String> paths = new ArrayList();
        for (FilePreviewBean bean : getDatas())
            paths.add(bean.getPath());
        return paths;
    }

    @Override
    public Holder createHolder(View root) {
        return new Holder(root);
    }

    @Override
    public void bindHolder(Holder holder, FilePreviewBean bean) {
        holder.fv.load(bean.getPath(), bean.getName());
        holder.bt.setOnClickListener((v) -> {
            super.remove(super.index(bean));
        });
        holder.iv.setOnClickListener((v) -> {
            FileUtil.openFile(context, bean.getPath(), UriUtil.AUTHORITY_FILE_PROVIDER);
        });
    }

    public static class Holder extends RecyclerView.ViewHolder {

        private FilePreviewer fv;
        private ImageView bt;
        private ImageView iv;
        private TextView lab;

        public Holder(View root) {
            super(root);
            fv = ViewManager.findView(root, R.id.fv);
            bt = ViewManager.findView(root, R.id.bt);
            iv = ViewManager.findView(root, R.id.iv);
            lab = ViewManager.findView(root, R.id.lab);
        }
    }
}
