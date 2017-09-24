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

import clibom.pocnfcwriter.NfcEngine.PoCNfcEngine;
import clibom.pocnfcwriter.NfcEngine.Response;

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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            // validate that this tag can be written
            PoCNfcEngine nfcEngine = new PoCNfcEngine(mContext);
            Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            if(nfcEngine.supportedTechs(detectedTag.getTechList())) {
                // check if tag is writable (to the extent that we can
                try{
                    if(nfcEngine.writableTag(detectedTag)) {
                        //writeTagResponse here
                        String tagValue = "http://www.reptoterraclub.com";
                        Response Response = nfcEngine.writeTagResponse(tagValue, detectedTag, false);
                        //Response Response = nfcEngine.writeTagResponse(tagValue, detectedTag, false);
                        String message = (Response.getStatus() == Response.SUCCESS ? "Success: " : "Failed: ") + Response.getMessage();
                        Toast.makeText(mContext ,message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext,"This tag is not writable",Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(mContext, "Failed to get Tag", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(mContext,"This tag type is not supported",Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * @return AlertDialog
     */
    private AlertDialog getDisabledDialog() {

        mBuilder = new AlertDialog.Builder(this);

        mBuilder.setMessage(R.string.alert_nfc_disabled);

        mBuilder.setPositiveButton(
                R.string.update_settings_button,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent setnfc = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                        startActivity(setnfc);
                    }
                });

        mBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });

        return mBuilder.create();
    }

    /**
     * @param nfcAdapter {@link NfcAdapter}
     */
    private void checkNfcAdapter(NfcAdapter nfcAdapter) {
        if (nfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!nfcAdapter.isEnabled()) {
            getDisabledDialog().show();
        } else {
            mTextView1.setText(R.string.explanation);
        }
    }
}
