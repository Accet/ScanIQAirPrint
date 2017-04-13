/*
 * (C) Copyright 2016 HP Development Company, L.P.
 * All Rights Reserved Worldwide
 * 08/17/2016 SMKAB
 *
 * Setting.java
 */

package helperClasses;

import java.util.ArrayList;
import java.util.List;

public abstract class Setting<T> {
    private String mKey;
    private String mName;
    private List<SettingChangedListener<T>> mListeners = new ArrayList<>();

    public Setting(String aKey, String aName) {
        mKey = aKey;
        mName = aName;
    }

    public String getKey() {
        return mKey;
    }

    public String getName() {
        return mName;
    }

    public abstract T getValue();

    /**
     * @param aValue value to set or null to remove choice
     * @return true if value allowed and was applied, null value is allowed
     */
    public abstract boolean setValue(T aValue);

    public void addSettingChangedListener(SettingChangedListener<T> aListener) {
        mListeners.add(aListener);
    }

    public void removeSettingChangedListener(SettingChangedListener<T> aListener) {
        mListeners.remove(aListener);
    }

    protected void notifySettingChanged(T aOld, T aNew) {
        if (Utils.isNotEmpty(mListeners)) {
            for (SettingChangedListener<T> theListener : mListeners) {
                theListener.onSettingChanged(aOld, aNew);
            }
        }
    }

    public interface SettingChangedListener<T> {
        void onSettingChanged(T aOld, T aNew);
    }
}
