package com.katbutler.encore.client;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.katbutler.encore.dataaccess.common.AsyncCallback;
import com.katbutler.encore.model.EncoreError;
import com.katbutler.encore.model.EncoreImageFile;
import com.katbutler.encore.xml.XmlParser;

public class ImageUploader {

	/**
	 * Upload an Image file to the server
	 * 
	 * @param imagePath path to the image on the android device
	 * 
	 * @return the servers path to the image file to save within the current user
	 */
	public void uploadImage(final String imagePath, final AsyncCallback<EncoreImageFile> callback) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {

				HttpURLConnection connection = null;
				DataOutputStream outputStream = null;

				String urlServer = SessionManager.getInstance().getEncoreServerUrl() + "doUpload";
				String lineEnd = "\r\n";
				String twoHyphens = "--";
				String boundary = "*****";

				int bytesRead, bytesAvailable, bufferSize;
				byte[] buffer;
				int maxBufferSize = 1 * 1024 * 1024;

				try {
					FileInputStream fileInputStream = new FileInputStream(new File(imagePath));

					URL url = new URL(urlServer);
					connection = (HttpURLConnection) url.openConnection();

					// Allow Inputs & Outputs
					connection.setDoInput(true);
					connection.setDoOutput(true);
					connection.setUseCaches(false);

					// Enable POST method
					connection.setRequestMethod("POST");

					connection.setRequestProperty("Connection", "Keep-Alive");
					connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

					outputStream = new DataOutputStream(connection.getOutputStream());
					outputStream.writeBytes(twoHyphens + boundary + lineEnd);
					
					outputStream.writeBytes("Content-Disposition: form-data; name=\"fileUpload\"; filename=\"" + imagePath + "\"" + lineEnd);
					outputStream.writeBytes("Content-Type: image/png,image/gif,image/jpeg" + lineEnd);
					outputStream.writeBytes(lineEnd);
					

					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					buffer = new byte[bufferSize];

					System.out.println("About to Write " + bytesAvailable + " to connection output stream");
					
					// Read file
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);

					while (bytesRead > 0) {
						outputStream.write(buffer, 0, bufferSize);
						bytesAvailable = fileInputStream.available();
						bufferSize = Math.min(bytesAvailable, maxBufferSize);
						bytesRead = fileInputStream.read(buffer, 0, bufferSize);
						System.out.println("About to Write " + bytesRead + " to connection output stream");
					}

					outputStream.writeBytes(lineEnd);
					outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

					// Responses from the server (code and message)
					int serverResponseCode = connection.getResponseCode();
					String serverResponseMessage = connection.getResponseMessage();

					
					if (serverResponseCode == 200) {
						
						// To print the resulting URL to the console
//						BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//						String inputLine;
//						while ((inputLine = br.readLine()) != null)
//						    System.out.println(inputLine);
//						br.close();

						// Parse the User XML
						XmlParser<EncoreImageFile> xmlParser = new XmlParser<EncoreImageFile>(EncoreImageFile.class);
						EncoreImageFile image = xmlParser.parseXmlDocument(connection.getInputStream());
						
						System.out.println(image.getFilePath());
						
						callback.onSuccess(image);
						
					} else if (serverResponseCode == EncoreError.ERROR_CODE) { // Status Code 207 is an EncoreError
						// Parse the EncoreError
						XmlParser<EncoreError> xmlParser = new XmlParser<EncoreError>(EncoreError.class);
						final EncoreError error = xmlParser.parseXmlDocument(connection.getInputStream());
						
						System.err.println(error.getErrorMessage());
						
						callback.onFailure(error);
						
					} else  {
						System.out.println("Server Response Code: " + serverResponseCode);
						System.out.println("Server Response Message: " + serverResponseMessage);
						callback.onFailure(new EncoreError(serverResponseCode + ": " + serverResponseMessage));
					}
					
					
					fileInputStream.close();
					outputStream.flush();
					outputStream.close();
					
					
				} catch (Exception ex) {
					// Exception handling
					ex.printStackTrace();
					callback.onFailure(new EncoreError("A problem occured while trying to upload the image to the server."));
				}

			}
		}).start();
	}

}
