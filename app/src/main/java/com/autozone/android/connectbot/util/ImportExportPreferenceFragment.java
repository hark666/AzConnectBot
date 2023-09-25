/*
 * ConnectBot: simple, powerful, open-source SSH client for Android
 * Copyright 2017 Kenny Root, Jeffrey Sharkey
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import org.connectbot.R;

import com.autozone.android.connectbot.bean.HostBean;
import com.autozone.android.connectbot.data.HostStorage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceDialogFragmentCompat;

/**
 * Created by kenny on 2/20/17.
 */

public class ImportExportPreferenceFragment extends PreferenceDialogFragmentCompat {
	private Button mImportData;
	private Button mExportData;

	private Button mClearData;

	private HostStorage hostdb;

	private Gson gson;

	public ImportExportPreferenceFragment() {
	}

	public static ImportExportPreferenceFragment newInstance(Preference preference) {
		ImportExportPreferenceFragment fragment = new ImportExportPreferenceFragment();
		Bundle bundle = new Bundle(1);
		bundle.putString("key", preference.getKey());
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);

		mImportData = view.findViewById(R.id.importButton);
		mExportData = view.findViewById(R.id.exportButton);
		mClearData = view.findViewById(R.id.clearButton);

		hostdb = HostDatabase.get(this.getContext());

		gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();

		mImportData.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("ImportExportPreferenceF", "onClick: Import button");

				openFileManager();
			}
		});

		mExportData.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("ImportExportPreferenceF", "onClick: Export button");

				//if(isStoragePermissionGranted()) {
					List<HostBean> hosts = hostdb.getHosts(false);

					File dir = new File("//sdcard//Download//");
					File myExternalFile = new File(dir, "AzConnectBotBackup.json");
					FileOutputStream fos = null;
					try {
						// Instantiate the FileOutputStream object and pass myExternalFile in constructor
						fos = new FileOutputStream(myExternalFile);
						// Write to the file
						fos.write(gson.toJson(hosts).getBytes());
						// Close the stream
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				//}
			}
		});

		mClearData.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("ImportExportPreferenceF", "onClick: Clear button");

				List<HostBean> hosts = hostdb.getHosts(false);

				for (HostBean host : hosts) {
					hostdb.deleteHost(host);
				}
			}
		});
	}

	@Override
	public void onDialogClosed(boolean positiveResult) {

	}

	private boolean isStoragePermissionGranted() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
				//Permission is granted
				return true;
			} else {
				//Permission is revoked
				String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
				ActivityCompat.requestPermissions(this.getActivity(), permissions, 1);
				return false;
			}
		} else {
			// Permission is automatically granted on sdk<23 upon installation
			return true;
		}
	}

	private void openFileManager() {
		Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("*/*");
		this.getDataFromFile.launch(intent);
	}

	private String readTextFromUri(Uri uri) {
		InputStream inputStream = null;
		StringBuilder stringBuilder = new StringBuilder();
		try {
			inputStream = this.getContext().getContentResolver().openInputStream(uri);
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

			// Read a line of text.
			String line = reader.readLine();
			// Read the entire file
			while (line != null) {
				// Append the line read to StringBuilder object. Also, append a new-line
				stringBuilder.append(line).append('\n');
				// Again read the next line and store in variable line
				line = reader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stringBuilder.toString();
	}

	ActivityResultLauncher<Intent> getDataFromFile = registerForActivityResult(
			new ActivityResultContracts.StartActivityForResult(),
			new ActivityResultCallback<ActivityResult>() {
				@Override
				public void onActivityResult(ActivityResult result) {
					if (result.getResultCode() == Activity.RESULT_OK) {
						Uri uri = result.getData().getData();
						String fileContents = readTextFromUri(uri);

						Log.d("ImportExportPreferenceF", "onActivityResult: " + fileContents);

						List<HostBean> hosts = Arrays.asList(gson.fromJson(fileContents, HostBean[].class));

						for (HostBean host : hosts) {
							host.setId(-1);
							hostdb.saveHost(host);
						}

						//Toast.makeText(getContext(), fileContents, Toast.LENGTH_SHORT).show();
					}
				}
			});
}
