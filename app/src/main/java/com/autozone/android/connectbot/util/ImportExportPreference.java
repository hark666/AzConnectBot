/*
 * ConnectBot: simple, powerful, open-source SSH client for Android
 * Copyright 2007 Kenny Root, Jeffrey Sharkey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.autozone.android.connectbot.util;

import org.connectbot.R;

import android.content.Context;
import android.util.AttributeSet;
import androidx.preference.DialogPreference;

/**
 * @author Kenny Root
 *
 */
public class ImportExportPreference extends DialogPreference {

	public ImportExportPreference(Context context) {
		this(context, null);
	}

	public ImportExportPreference(Context context, AttributeSet attrs) {
		this(context, attrs, androidx.preference.R.attr.dialogPreferenceStyle);
	}

	public ImportExportPreference(Context context, AttributeSet attrs, int defStyleAttr) {
		this(context, attrs, defStyleAttr, defStyleAttr);
	}

	public ImportExportPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);

		setPersistent(true);
	}

	@Override
	public int getDialogLayoutResource() {
		return R.layout.import_export_preference_dialog_layout;
	}
}
