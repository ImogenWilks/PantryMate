package com.example.pantrymate.codeScanner;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.TypedValue;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;
import com.example.pantrymate.R;
import com.google.zxing.Result;

public class ScanResultDialog extends AppCompatDialog {
    public ScanResultDialog(@NonNull Context context, @NonNull Result result) {
        super(context, resolveDialogTheme(context));
        setTitle(R.string.scan_result);
        setContentView(R.layout.dialog_scan_result);
        //noinspection ConstantConditions
        ((TextView) findViewById(R.id.result)).setText(result.getText());
        //noinspection ConstantConditions
        ((TextView) findViewById(R.id.format)).setText(String.valueOf(result.getBarcodeFormat()));
        //noinspection ConstantConditions
        findViewById(R.id.copy).setOnClickListener(v -> {
            //noinspection ConstantConditions
            ((ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE))
                    .setPrimaryClip(ClipData.newPlainText(null, result.getText()));
            Toast.makeText(context, R.string.copied_to_clipboard, Toast.LENGTH_LONG).show();
            dismiss();
        });
        //noinspection ConstantConditions
        findViewById(R.id.close).setOnClickListener(v -> dismiss());
    }

    private static int resolveDialogTheme(@NonNull Context context) {
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(androidx.appcompat.R.attr.alertDialogTheme, outValue, true);
        return outValue.resourceId;
    }
}
