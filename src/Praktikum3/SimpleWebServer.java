package Praktikum3;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SimpleWebServer {
    public static void main(String[] args) throws IOException {
        int port = 8000;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server gestartet auf Port " + port);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Neue Verbindung von " + clientSocket.getInetAddress().getHostName() + " auf Port: " + port + " angefragt.");

            ClientHandler clientHandler = new ClientHandler(clientSocket);
            Thread thread = new Thread(clientHandler);
            thread.start();

            System.out.println();
            System.out.println();
        }
    }

    static class ClientHandler implements Runnable {

        private File rootDir; //davor anders
        private final Socket clientSocket;

        ClientHandler(Socket socket) {
            this.clientSocket = socket;
            this.rootDir= new File("/Users/danielmendes/Desktop/Semester_4/RB/RB_Praktikum_1/src/Praktikum3/webspace");
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                OutputStream out = clientSocket.getOutputStream();

                String request = in.readLine();
                System.out.println("Anfrage: " + request);

                String[] requestParts = request.split(" ");
                String method = requestParts[0];
                String requestedResource = requestParts[1];

                //Überprüfe, ob es sich um GET-Befehl handelt
                if (method.equals("GET")) {
                    if (requestedResource.equals("/time")) {
                        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                        String time = timeFormat.format(new Date());
                        out.write(("HTTP/1.0 200 OK\r\n" +
                                "Content-Type: text/plain\r\n" +
                                "Content-Length: " + time.length() + "\r\n\r\n").getBytes());
                        out.write(time.getBytes());
                        // date-Seite
                    } else if (requestedResource.equals("/date")) {
                        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                        String date = dateFormat.format(new Date());
                        out.write(("HTTP/1.0 200 OK\r\n" +
                                "Content-Type: text/plain\r\n" +
                                "Content-Length: " + date.length() + "\r\n\r\n").getBytes());
                        out.write(date.getBytes());
//                    //redirect zu einer anderen Seite
//                    }else if (requestedResource.equals("/")) {
//                        responseCode = 302;
//                        response = "HTTP/1.0 " + responseCode + " " + getResponseMessage(responseCode) + "\r\n" +
//                                "Location: http://localhost/index.html\r\n\r\n";
                    } else {
                        String userAgent = "";
                        String line;
                        System.out.println("Client Header:");
                        while ((line = in.readLine()) != null && !line.isEmpty()) {
                            System.out.println(line);
                            if (line.startsWith("User-Agent:")) {
                                userAgent = line.substring("User-Agent:".length()).trim();
                            }
                        }
                        System.out.println("\n");

                        if (!userAgent.contains("Firefox")) {
                            sendErrorResponse(out, 406, "Not Acceptable");
                        } else {
                            if (requestedResource.equals("/")) {
                                requestedResource = "index.html";
                            }
                            serveFile(out, rootDir, requestedResource);
                        }
                    }} else {
                    sendErrorResponse(out, 400, "Bad Request");
                }

                out.close();
                in.close();
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error handling request: " + e.getMessage());
            }
        }

        //Vom Leon:
        private static void serveFile(OutputStream out, File rootDir, String resource) throws IOException {
            File file = new File(rootDir, resource);
            String mimeType = getMimeType(file);
            if (mimeType != null && file.exists()) {
                byte[] response = new byte[4096];
                if (mimeType != null && file.exists()) {
                    if (mimeType.startsWith("image/")) {
                        // Wenn der MimeType ein Bild ist
                        FileInputStream fileInputStream = new FileInputStream(file);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                            byteArrayOutputStream.write(buffer, 0, bytesRead);
                        }
                        fileInputStream.close();
                        byteArrayOutputStream.close();
                        response = byteArrayOutputStream.toByteArray();
                    } else if (mimeType.equals("application/pdf")) {
                        // Wenn der MimeType ein PDF ist
                        FileInputStream fileInputStream = new FileInputStream(file);
                        response = new byte[(int) file.length()];
                        fileInputStream.read(response);
                        fileInputStream.close();
                    } else {
                        // Andernfalls Textinhalt der Datei
                        BufferedReader fileReader = new BufferedReader(new FileReader(file));
                        StringBuilder fileContent = new StringBuilder();
                        String line;
                        while ((line = fileReader.readLine()) != null) {
                            fileContent.append(line);
                        }
                        fileReader.close();
                        response = fileContent.toString().getBytes();
                    }
                }

                out.write(("HTTP/1.0 200 OK\r\n" +
                        "Content-Type: " + mimeType + "\r\n" +
                        "Content-Length: " + response.length + "\r\n\r\n").getBytes());
                out.write(response);

                System.out.println("Gesendete Headerzeilen:");
                System.out.println("HTTP/1.0 200 OK");
                System.out.println("Content-Type: " + mimeType);
                System.out.println("Content-Length: " + response.length + "\n");
            } else {
                // Wenn die Datei nicht gefunden wird
                sendErrorResponse(out, 404, "Not Found");
            }
        }

        //Von Leon
        private static void sendErrorResponse(OutputStream out, int errorCode, String errorMessage) throws IOException {
            if(errorCode!=200) {
                String response = errorCode + " " + errorMessage + "\r\n\r\n" + errorMessage;
                out.write(response.getBytes());
            }
        }

        //Von Leon
        private static String getMimeType(File file) {
            String fileName = file.getName();
            String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
            switch (extension) {
                case "html":
                    return "text/html";
                case "jpg":
                case "jpeg":
                    return "image/jpeg";
                case "png":
                    return "image/png";
                case "gif":
                    return "image/gif";
                case "pdf":
                    return "application/pdf";
                case "ico":
                    return "image/x-icon";
                default:
                    return null;
            }
        }
    }
}