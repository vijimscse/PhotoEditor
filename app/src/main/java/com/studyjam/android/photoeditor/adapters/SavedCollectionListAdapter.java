package com.studyjam.android.photoeditor.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.studyjam.android.photoeditor.R;

import java.io.File;

/**
 * Shows the file list
 */
public class SavedCollectionListAdapter extends RecyclerView.Adapter<SavedCollectionListAdapter.ViewHolder> {

    private File[] mFileList;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(File file);
    }

    public SavedCollectionListAdapter(Context mContext, File[] files, OnItemClickListener listener) {
        this.mContext = mContext;
        this.mFileList = files;
        mOnItemClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.saved_file_item, viewGroup , false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder mViewHolder, final int position) {
        mViewHolder.mFileName.setText(mFileList[position].getName());
        mViewHolder.mItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onListItemHeaderClick(mViewHolder, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (mFileList != null ? mFileList.length : 0);
    }

    private void onListItemHeaderClick(ViewHolder viewHolder, int position) {
        mOnItemClickListener.onItemClick(mFileList[position]);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mFileName;
        public View mItemLayout;

        public ViewHolder(View view) {
            super(view);

            mItemLayout = view;
            mFileName = (TextView) view.findViewById(R.id.file_name);
        }
    }
}
