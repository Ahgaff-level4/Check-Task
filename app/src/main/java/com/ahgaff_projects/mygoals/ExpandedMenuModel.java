package com.ahgaff_projects.mygoals;

public class ExpandedMenuModel {
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

