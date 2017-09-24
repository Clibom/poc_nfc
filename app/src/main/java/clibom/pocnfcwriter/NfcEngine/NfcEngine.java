package clibom.pocnfcwriter.NfcEngine;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * Created by valen on 24/09/2017.
 */

public abstract class NfcEngine {
    static final int WIFI_PROVISIONNING = 24;
    static final int TEXT_PROVISIONNING = 671;

    private boolean mWriteProtect = false;
    private Context mContext;

    /**
     * @param mContext Context
     */
    public NfcEngine(Context mContext) {
        this.mContext = mContext;
    }

    abstract Response writeTagResponse(String tagValue, Tag tag, boolean readOnly);

    public static boolean supportedTechs(String[] techs) {
        boolean nfcATechnology = false;
        boolean ndefTechnology = false;

        for(String tech:techs) {
            if(tech.equals("android.nfc.tech.NfcA")) {
                nfcATechnology=true;
            } else if(tech.equals("android.nfc.tech.Ndef") || tech.equals("android.nfc.tech.NdefFormatable")) {
                ndefTechnology=true;
            }
        }

        return nfcATechnology && ndefTechnology;
    }
    /**
     * Check if NFC Support is writable
     *
     * @param detectedTag Tag
     * @return boolean
     */
    public boolean writableTag(Tag detectedTag) {

        try {
            Ndef ndefSupport = Ndef.get(detectedTag);

            if (ndefSupport != null) {
                ndefSupport.connect();

                if (!ndefSupport.isWritable()) {
                    ndefSupport.close();
                    return false;
                } else {
                    Toast.makeText(mContext,"This tag is not writable",Toast.LENGTH_SHORT).show();
                }

                ndefSupport.close();
                return true;
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            Toast.makeText(mContext, "Failed to read tag", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    /**
     * Get Tag as an Ndef Message
     *
     * @return NdefMessage
     */
    protected NdefMessage formatToNdefMessage(int tagInternalType, String tagValue, boolean addAar, String packageName) {
        byte[] uriField = tagValue.getBytes(Charset.forName("US-ASCII"));

        //add 1 for the URI Prefix
        byte[] payload = new byte[uriField.length + 1];

        //appends URI to payload
        System.arraycopy(uriField, 0, payload, 1, uriField.length);

        NdefRecord rtdUriRecord = new NdefRecord(
                NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_URI, new byte[0], payload);
        if(tagInternalType == TEXT_PROVISIONNING){
            if(addAar) {
                // note: returns AAR for different app (nfcreadtag)
                return new NdefMessage(new NdefRecord[] {
                        rtdUriRecord, NdefRecord.createApplicationRecord(packageName)
                });
            } else {
                return new NdefMessage(new NdefRecord[] {
                        rtdUriRecord});
            }
        }
        return null;
    }

    /**
     * Get protected writer option
     *
     * @return boolean
     */
    public boolean isWriteProtected() {
        return mWriteProtect;
    }

    /**
     * Enable protected writer option
     *
     * @param mWriteProtect boolean
     */
    public void setWriteProtect(boolean mWriteProtect) {
        this.mWriteProtect = mWriteProtect;
    }
}
