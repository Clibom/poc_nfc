package clibom.pocnfcwriter;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    /**
     * System Fields
     */
    private NfcAdapter mNfcAdapter;
    private PendingIntent mNfcPendingIntent;
    private Context mContext;

    /**
     * GUI fields
     */
    private TextView mTextView1;
    private Button mButton1;
    private AlertDialog.Builder mBuilder;
    private AlertDialog mAlertDialog;

    /**
     * Intent Filters
     */
    private IntentFilter[] mWriteTagFilters;
    private IntentFilter mDiscovery;
    private IntentFilter mNDEFDetected;
    private IntentFilter mTechDetected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView1 = (TextView)findViewById(R.id.tw1);
        mButton1 = (Button)findViewById(R.id.button1);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        mContext = getApplicationContext();

        checkNfcAdapter(mNfcAdapter);

        mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TOP), 0);

        mDiscovery = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        mNDEFDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        mTechDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);

        // Intent filters for writing to a tag
        mWriteTagFilters = new IntentFilter[] { mDiscovery };
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mNfcAdapter != null) {
            checkNfcAdapter(mNfcAdapter);

            mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mWriteTagFilters, null);

        } else {
            Toast.makeText(mContext, R.string.nfc_unavailable, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mNfcAdapter != null) mNfcAdapter.disableForegroundDispatch(this);
    }
    }
}
