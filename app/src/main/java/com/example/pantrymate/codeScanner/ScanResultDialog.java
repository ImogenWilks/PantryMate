package com.example.pantrymate.codeScanner;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;
import com.example.pantrymate.R;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class ScanResultDialog extends AppCompatDialog {
    public ScanResultDialog(@NonNull Context context, @NonNull Result result) {
        super(context, resolveDialogTheme(context));
        setTitle(R.string.scan_result);
        setContentView(R.layout.dialog_scan_result);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //noinspection ConstantConditions
        ((TextView) findViewById(R.id.result)).setText(result.getText());
        //noinspection ConstantConditions
        ((TextView) findViewById(R.id.format)).setText(String.valueOf(result.getBarcodeFormat()));

        TextView parsed = findViewById(R.id.jsonParse);
        String barcodeNum = result.getText();
        String productName = "";

        try {
            URL url = new URL("https://world.openfoodfacts.org/api/v0/product/" + barcodeNum + ".json");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String data = "";
            String line = "";

            while (line != null){
                line = bufferedReader.readLine();
                data = data + line;
            }


            JSONObject item = new JSONObject(data);
            final JSONObject product = item.getJSONObject("product");
            final Map<String, Object> map =
                    product.keySet()
                            .stream()
                            .collect(Collectors.toMap(
                                    Function.identity(),
                                    product::get
                            ));
            //final Map<String, Object> objectMap = product.toMap();
            //Map product = ((Map)item.get("product"));

            /*
            Iterator<Map.Entry> itr = product.entrySet().iterator();
            while (itr.hasNext()){
                Map.Entry pair = itr.next();
                if (pair.getKey() == "product_name"){
                    productName = (String) pair.getValue();
                }
            }*/

            parsed.setText(productName);

        } catch(MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
