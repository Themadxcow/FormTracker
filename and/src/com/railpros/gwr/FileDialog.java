package com.railpros.gwr;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

@SuppressLint("DefaultLocale")
public class FileDialog extends ListActivity {

	private static final String ITEM_KEY = "key";
	private static final String ITEM_IMAGE = "image";
	private static final String ROOT = Environment.getExternalStorageDirectory().getPath();
	public static final String START_PATH = "START_PATH";
	public static final String FORMAT_FILTER = "FORMAT_FILTER";
	public static final String RESULT_PATH = "RESULT_PATH";
	public static final String SELECTION_MODE = "SELECTION_MODE";
	public static final String CAN_SELECT_DIR = "CAN_SELECT_DIR";

	private List<String> path = null;
	private ArrayList<HashMap<String, Object>> mList;

	private Button selectButton;
	private TextView selectFile;
	
	private LinearLayout layoutSelect;
	private InputMethodManager inputManager;
	private String parentPath;
	private String currentPath = ROOT;
	private String[] formatFilter = null;

	private boolean canSelectDir = false;

	private File selectedFile;
	private HashMap<String, Integer> lastPositions = new HashMap<String, Integer>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setResult(RESULT_CANCELED, getIntent());

		setContentView(R.layout.file_dialog_main);

		inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

		selectFile = (TextView) findViewById(R.id.fdFile);
		selectFile.setText("");
		
		selectButton = (Button) findViewById(R.id.fdButtonSelect);
		selectButton.setEnabled(false);
		selectButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (selectedFile != null) {
					
					//getIntent().putExtra(RESULT_PATH, selectedFile.getPath());
					//setResult(RESULT_OK, getIntent());
	        		//Intent intentPdfview = new Intent(FileDialog.this, PDFHandler.class);
	        		//intentPdfview.putExtra(PdfViewerActivity.EXTRA_PDFFILENAME, selectedFile.getPath());
	        		//startActivity(intentPdfview);
					finish();
				}
			}
		});

		formatFilter = getIntent().getStringArrayExtra(FORMAT_FILTER);
		canSelectDir = getIntent().getBooleanExtra(CAN_SELECT_DIR, false);

		layoutSelect = (LinearLayout) findViewById(R.id.fdLinearLayoutSelect);

		String startPath = getIntent().getStringExtra(START_PATH);
		startPath = startPath != null ? startPath : ROOT;
		//Log.d("PICKformat",formatFilter);
		if (canSelectDir) {
			File file = new File(startPath);
			selectedFile = file;
			selectButton.setEnabled(true);
		}
		getDir(startPath);
	}

	private void getDir(String dirPath) {

		boolean useAutoSelection = dirPath.length() < currentPath.length();

		Integer position = lastPositions.get(parentPath);

		getDirImpl(dirPath);

		if (position != null && useAutoSelection) {
			getListView().setSelection(position);
		}

	}

	//@SuppressLint("DefaultLocale")
	private void getDirImpl(final String dirPath) {

		currentPath = dirPath;

		final List<String> item = new ArrayList<String>();
		path = new ArrayList<String>();
		mList = new ArrayList<HashMap<String, Object>>();

		File f = new File(currentPath);
		File[] files = f.listFiles();
		if (files == null) {
			currentPath = ROOT;
			f = new File(currentPath);
			files = f.listFiles();
		}

		TreeMap<String, String> dirsMap = new TreeMap<String, String>();//
		TreeMap<String, String> dirsPathMap = new TreeMap<String, String>();//
		TreeMap<String, String> filesMap = new TreeMap<String, String>();
		TreeMap<String, String> filesPathMap = new TreeMap<String, String>();//
		for (File file : files) {
			if (file.isDirectory()) {

								String dirName = file.getName();
				dirsMap.put(dirName, dirName);
				dirsPathMap.put(dirName, file.getPath());

				break;
			} else {
				final String fileName = file.getName();
				final String fileNameLwr = fileName.toLowerCase(Locale.US);
				if (formatFilter != null) {
					boolean contains = false;
					for (int i = 0; i < formatFilter.length; i++) {
						final String formatLwr = formatFilter[i].toLowerCase(Locale.US);
						if (fileNameLwr.endsWith(formatLwr)) {
							contains = true;
							break;
						}
					}
					if (contains) {
						//val=Math.ceil(cnt/36);
						filesMap.put(fileName, fileName);
						filesPathMap.put(fileName, file.getPath());//
						contains=false;
						//cnt++;
					}
				} else {//
					filesMap.put(fileName, fileName);//
					filesPathMap.put(fileName, file.getPath());//
				}
			}
		}
		item.addAll(dirsMap.tailMap("").values());//
		item.addAll(filesMap.tailMap("").values());
		path.addAll(dirsPathMap.tailMap("").values());//
		path.addAll(filesPathMap.tailMap("").values());//


		SimpleAdapter fileList = new SimpleAdapter(this, mList, R.layout.file_dialog_row, new String[] {
				ITEM_KEY, ITEM_IMAGE }, new int[] { R.id.fdrowtext, R.id.fdrowimage });

		for (String dir : dirsMap.tailMap("").values()) {//
			addItem(dir, R.drawable.folder);//
		}//

		for (String file : filesMap.tailMap("").values()) {
			addItem(file, R.drawable.file);
		}

		fileList.notifyDataSetChanged();

		setListAdapter(fileList);

	}

	private void addItem(String fileName, int imageId) {
		HashMap<String, Object> item = new HashMap<String, Object>();
		item.put(ITEM_KEY, fileName);
		item.put(ITEM_IMAGE, imageId);
		mList.add(item);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		File file = new File(path.get(position));
		setSelectVisible(v);

		if (file.isDirectory()) {
			selectFile.setText("");
			selectButton.setEnabled(false);
			if (file.canRead()) {
				lastPositions.put(currentPath, position);
				getDir(path.get(position));
				if (canSelectDir) {
					selectedFile = file;
					v.setSelected(true);
					selectButton.setEnabled(true);
				}
			} else {
				new AlertDialog.Builder(this).setIcon(R.drawable.icon)
						.setTitle("[" + file.getName() + "] Error opening.")
						.setPositiveButton("OK", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {

							}
						}).show();
			}
		} else {
			selectedFile = file;
			v.setSelected(true);
			selectFile.setText(selectedFile.getName());
			selectButton.setEnabled(true);
		}
	}


	private void setSelectVisible(View v) {
		layoutSelect.setVisibility(View.VISIBLE);

		inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
		selectButton.setEnabled(false);
	}
}

/*
package com.railpros.gwr;

public class SelectionMode {
	public static final int MODE_CREATE = 0;

	public static final int MODE_OPEN = 1;
} */

