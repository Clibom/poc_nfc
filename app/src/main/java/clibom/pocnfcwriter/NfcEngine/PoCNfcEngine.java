package clibom.pocnfcwriter.NfcEngine;

import android.content.Context;
import android.nfc.NdefMessage;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;

import java.io.IOException;

/**
 * Created by valen on 24/09/2017.
 */
public class PoCNfcEngine extends NfcEngine {

    /**
     * @param mContext Context
     */
    public PoCNfcEngine(Context mContext) {
        super(mContext);
    }

    /**
     * Write TagMessage on Tag
     *
     * @param tag {@link Tag}
     * @return Response
     */
    public Response writeTagResponse(String tagValue, Tag tag, boolean readOnly) {
        NdefMessage nfcMessage = formatToNdefMessage(TEXT_PROVISIONNING, tagValue, false, null);

        int size = nfcMessage.toByteArray().length;
        String mess = "";
        try {
            //Get Ndef support object from the tag
            Ndef ndefSupport = Ndef.get(tag);

            if (ndefSupport != null) {
                //Try to connect the tag
                ndefSupport.connect();

                //Check if Ndef Support is not read-only
                if (!ndefSupport.isWritable()) {
                    String response = "Tag is read-only";
                    return new Response.getFailResponse(response);
                }

                //Check if message is not too long
                if (ndefSupport.getMaxSize() < size) {
                    String response = "Tag capacity is " + ndefSupport.getMaxSize() + " bytes, message is " + size
                            + " bytes.";
                    return new Response.getFailResponse(response);
                }

                /**
                 * Write the message on ndef support
                 */
                ndefSupport.writeNdefMessage(nfcMessage);

                /**
                 * If message is configured to be written ReadOnly
                 */
                if(readOnly) ndefSupport.makeReadOnly();

                String response = "Tag is updated !";

                return new Response.getSuccessResponse(response);
            } else {
                //Get Formatted Ndef Support if not in android known implementation
                NdefFormatable ndefFormattedSupport = NdefFormatable.get(tag);

                if (ndefFormattedSupport != null) {
                    try {
                        //Connect to the formatted support
                        ndefFormattedSupport.connect();
                        //Write formatted format
                        ndefFormattedSupport.format(nfcMessage);

                        String response = "Nfc Formatted Tag is Updated !";
                        return new Response.getSuccessResponse(response);
                    } catch (IOException e) {
                        String response = "Failed to format tag.";
                        return new Response.getFailResponse(response);
                    }
                } else {
                    String response = "Tag doesn't support NDEF Message.";
                    return new Response.getFailResponse(response);
                }
            }
        } catch (Exception e) {
            String response = "Failed to write Tag with error : " + e.getMessage();
            return new Response.getFailResponse(response);
        }
    }
}
