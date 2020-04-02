package com.example.vedit.Utils;

/**
 * @ProjectName: VEdit
 * @Package: com.example.vedit.Utils
 * @ClassName: Item
 * @Description: java类作用描述
 * @Author: yunyajie
 * @CreateDate: 2020/3/22 22:02
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/3/22 22:02
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class Item {
    private int iconId;
    private String iconName;

    public Item() {
    }

    public Item(int iconId, String iconName) {
        this.iconId = iconId;
        this.iconName = iconName;
    }

    public int getIconId() {
        return iconId;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }
}
