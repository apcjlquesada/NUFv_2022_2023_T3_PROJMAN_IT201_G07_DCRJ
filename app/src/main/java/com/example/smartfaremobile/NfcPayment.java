package com.example.smartfaremobile;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.app.PendingIntent;
import android.nfc.NfcAdapter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;

import android.util.Log;
import android.widget.Toast;

public class NfcPayment extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_payment);

        // Initialize NFC Adapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        // Create a PendingIntent for NFC events
        Intent nfcIntent = new Intent(this, getClass());
        nfcIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, nfcIntent, PendingIntent.FLAG_IMMUTABLE); // Use FLAG_IMMUTABLE
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Enable foreground dispatch to handle NFC tags
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Disable foreground dispatch when the activity is paused
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            if (tag != null) {
                NdefMessage ndefMessage = getNdefMessage(tag);

                if (ndefMessage != null) {
                    NdefRecord[] ndefRecords = ndefMessage.getRecords();

                    if (ndefRecords != null && ndefRecords.length > 0) {
                        String payload = new String(ndefRecords[0].getPayload());

                        // Handle the NFC data
                        processNFCData(payload);
                    }
                }
            }
        }
    }

    private NdefMessage getNdefMessage(Tag tag) {
        // Get the Ndef (NFC Data Exchange Format) object from the tag
        Ndef ndef = Ndef.get(tag);

        if (ndef != null) {
            try {
                ndef.connect();

                // Read the Ndef message
                NdefMessage ndefMessage = ndef.getNdefMessage();
                ndef.close();

                return ndefMessage;
            } catch (Exception e) {
                Log.e("NFC", "Error reading NDEF message: " + e.getMessage());
            }
        }

        return null;
    }

    private void processNFCData(String data) {
        // Implement your logic to process the NFC data here
        // You can parse the data and simulate the NFC transaction

        // For demonstration, you can simulate a successful NFC transaction
        // Simulate the process as if it's an NFC tag tapped
        // For example, update UI or perform an action

        // Show a toast message to indicate a successful transaction
        Toast.makeText(this, "NFC Transaction Successful. Deducted: " + data, Toast.LENGTH_SHORT).show();
    }
}
