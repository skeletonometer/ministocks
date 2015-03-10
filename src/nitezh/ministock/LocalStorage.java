/*
 The MIT License

 Copyright (c) 2013 Nitesh Patel http://niteshpatel.github.io/ministocks

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 */

package nitezh.ministock;


import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class LocalStorage implements Storage {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public LocalStorage(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public int getInt(String key, int defaultVal) {
        return this.preferences.getInt(key, defaultVal);
    }

    @Override
    public String getString(String key, String defaultVal) {
        return this.preferences.getString(key, defaultVal);
    }

    @Override
    public boolean getBoolean(String key, boolean defaultVal) {
        return this.preferences.getBoolean(key, defaultVal);
    }

    @Override
    public Storage putInt(String key, int value) {
        if (this.editor == null) this.editor = this.preferences.edit();
        this.editor.putInt(key, value);
        return this;
    }

    @Override
    public void apply() {
        if (this.editor != null) this.editor.apply();
        this.editor = null;
    }

    @Override
    public HashMap<String, ?> getAll() {
        HashMap<String, Object> items = new HashMap<>();
        for (Map.Entry<String, ?> entry : this.preferences.getAll().entrySet()) {
            items.put(entry.getKey(), entry.getValue());
        }

        return items;
    }

    @Override
    public Storage putString(String key, String value) {
        if (this.editor == null) this.editor = this.preferences.edit();
        this.editor.putString(key, value);
        return this;
    }

    @Override
    public Storage putBoolean(String key, Boolean value) {
        if (this.editor == null) this.editor = this.preferences.edit();
        this.editor.putBoolean(key, value);
        return this;
    }

    @Override
    public Storage putFloat(String key, Float value) {
        if (this.editor == null) this.editor = this.preferences.edit();
        this.editor.putFloat(key, value);
        return this;
    }

    @Override
    public Storage putLong(String key, Long value) {
        if (this.editor == null) this.editor = this.preferences.edit();
        this.editor.putFloat(key, value);
        return this;
    }
}