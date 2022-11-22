package com.ahgaff_projects.mygoals;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ahgaff_projects.mygoals.folder.Folder;
import com.ahgaff_projects.mygoals.folder.FolderRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private final Context mContext;
    private final List<ExpandedMenuModel> mListDataHeader; // header titles
    public static final int FoldersPos = 1;
    // child data in format of header title, child title
    private final HashMap<ExpandedMenuModel, List<String>> mListDataChild;
    ExpandableListView expandList;

    public ExpandableListAdapter(Context context, ExpandableListView mView, FolderRecyclerViewAdapter.EventFoldersChanged handler) {
        this.mContext = context;
        this.mListDataHeader = new ArrayList<>();
        this.mListDataChild = new HashMap<>();
        mListDataHeader.add(new ExpandedMenuModel(context.getString(R.string.home_title), R.drawable.home));
        mListDataHeader.add(new ExpandedMenuModel(context.getString(R.string.folders_title), R.drawable.folder));//NOTE: changing the order REQUIRE change ExpandableListAdapter.FoldersPos value!!!
        mListDataHeader.add(new ExpandedMenuModel(context.getString(R.string.all_files), R.drawable.file_description));
        update();

        FolderRecyclerViewAdapter.foldersChangedCallback = handler;
        this.expandList = mView;
    }

    @Override
    public int getGroupCount() {
        return this.mListDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (groupPosition == FoldersPos)
            return Objects.requireNonNull(this.mListDataChild.get(this.mListDataHeader.get(groupPosition)))
                    .size();
        return 0;
    }

    /**
     * update child of Folders with user's folders retrieved from DB
     */
    public void update(){
        List<String> headerFolders = new ArrayList<>();
        List<Folder> folders = new DB(mContext).getAllFolders();
        for (Folder f : folders)
            headerFolders.add(f.getName());
        mListDataChild.put(mListDataHeader.get(ExpandableListAdapter.FoldersPos), headerFolders);
        notifyDataSetChanged();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.mListDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return Objects.requireNonNull(this.mListDataChild.get(this.mListDataHeader.get(groupPosition)))
                .get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ExpandedMenuModel headerTitle = (ExpandedMenuModel) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.nav_list_header, null);
        }
        TextView lblListHeader = convertView.findViewById(R.id.submenu);
        ImageView headerIcon = convertView.findViewById(R.id.iconimage);
        lblListHeader.setText(headerTitle.getIconName());
        headerIcon.setImageResource(headerTitle.getIconImg());
            ImageView arrowicon = convertView.findViewById(R.id.arrowicon);
        if (groupPosition == FoldersPos) {//hard coded second item which is the Folders item
            if (isExpanded)
                arrowicon.setImageResource(R.drawable.expand_less);
            else arrowicon.setImageResource(R.drawable.expand_more);
        }
        if(parent.getChildAt(0)!=null)
            convertView.setSelected(false);
        return convertView;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.nav_list_submenu, null);
        }

        TextView txtListChild = convertView.findViewById(R.id.submenu);

        txtListChild.setText(childText);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}

class ExpandedMenuModel {
    private final String iconName;
    private final int iconImg; // menu icon resource id


    public ExpandedMenuModel(String iconName, int iconImg) {
        this.iconName = iconName;
        this.iconImg = iconImg;
    }

    public String getIconName() {
        return iconName;
    }

    public int getIconImg() {
        return iconImg;
    }
}

