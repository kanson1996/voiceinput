package com.example.voicetest;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

public class MainActivity extends ActionBarActivity {
	private EditText editText;
	private Button button;
	
	private Toast mToast;
	private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
	SpeechRecognizer mIat;
	RecognizerDialog iatDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		editText = (EditText) findViewById(R.id.et);
		button = (Button) findViewById(R.id.btn);
		SpeechUtility.createUtility(this, SpeechConstant.APPID +"=552e1e60");   
		
		//������ʼ�����������
		mIat= SpeechRecognizer.createRecognizer(this, null);    
		mIat.setParameter(SpeechConstant.DOMAIN, "iat");    
		mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");    
		mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");  
		
		//��������
		iatDialog = new RecognizerDialog(this, mInitListener);
		
		//toast
		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				editText.setText(null);// �����ʾ����
				mIatResults.clear();
				// ��ʾ��д�Ի���
				iatDialog.setListener(recognizerDialogListener);
				iatDialog.show();
				showTip("start voice");
			}
		});
	}
	
	//Listener for dialog
	private InitListener mInitListener = new InitListener() {

		@Override
		public void onInit(int code) {
			Log.d("TAG", "SpeechRecognizer init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
				showTip("��ʼ��ʧ�ܣ������룺" + code);
			}
		}
	};
	
	
	/**
	 * Dialog������
	 */
	private RecognizerDialogListener recognizerDialogListener = new RecognizerDialogListener() {
		public void onResult(RecognizerResult results, boolean isLast) {
			printResult(results);
		}

		/**
		 * ʶ��ص�����.
		 */
		public void onError(SpeechError error) {
			showTip(error.getPlainDescription(true));
		}

	};

	/**
	 * toastչʾ
	 * @param str
	 */
	private void showTip(final String str) {
		mToast.setText(str);
		mToast.show();
	}
	
	//EditText��ʾ���
	private void printResult(RecognizerResult results) {
		String text = JsonParser.parseIatResult(results.getResultString());

		String sn = null;
		// ��ȡjson����е�sn�ֶ�
		try {
			JSONObject resultJson = new JSONObject(results.getResultString());
			sn = resultJson.optString("sn");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		mIatResults.put(sn, text);

		StringBuffer resultBuffer = new StringBuffer();
		for (String key : mIatResults.keySet()) {
			resultBuffer.append(mIatResults.get(key));
		}

		editText.setText(resultBuffer.toString());
		editText.setSelection(editText.length());
	}
	
	// �˳�ʱ�ͷ�����	
	protected void onDestroy() {
		super.onDestroy();
		mIat.cancel();
		mIat.destroy();
	}
	
}
