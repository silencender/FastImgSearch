package com.silen.android;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class FileUploadTask extends AsyncTask<String, Integer, Void> {

    private ProgressDialog dialog = null;
    HttpURLConnection connection = null;
    DataOutputStream outputStream = null;
    DataInputStream inputStream = null;
    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(MainActivity.mainActivity);
        dialog.setMessage("正在上传...");
        dialog.setIndeterminate(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setProgress(0);
        dialog.show();
    }
    @Override
    protected Void doInBackground(String... arg) {
        //the file path to upload
        String pathToOurFile =arg[0];
        //the server address to process uploaded file
        String urlServer = arg[1];
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        File uploadFile = new File(pathToOurFile);
        long totalSize = uploadFile.length(); // Get size of file, bytes


        long length = 0;
        int progress;
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 2560 * 10240;// 25600KB
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(
                    pathToOurFile));
            URL url = new URL(urlServer);
            connection = (HttpURLConnection) url.openConnection();
            // Set size of every block for post
            connection.setChunkedStreamingMode(2560 * 10240);// 25600KB
            // Allow Inputs & Outputs
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            // Enable POST method
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);
            outputStream = new DataOutputStream(
                    connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream
                    .writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
                            + pathToOurFile + "\"" + lineEnd);
            outputStream.writeBytes(lineEnd);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];
            // Read file
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                length += bufferSize;
                progress = (int) ((length * 100) / totalSize);
                publishProgress(progress);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens
                    + lineEnd);
            publishProgress(100);
            // Responses from the server (code and message)
            int serverResponseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();
            fileInputStream.close();
            outputStream.flush();
            outputStream.close();
        } catch (Exception ex) {
        }
        return null;
    }
    @Override
    protected void onProgressUpdate(Integer... progress) {
        dialog.setProgress(progress[0]);
    }
    @Override
    protected void onPostExecute(Void result) {
        Toast.makeText(MainActivity.mainActivity, "上传成功!即刻搜索！", Toast.LENGTH_SHORT).show();
        try {
            dialog.dismiss();
        } catch (Exception e) {
        }
    }
}
