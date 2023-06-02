package Praktikum3;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Server {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;

        boolean listening = true;

        try {
            // Port 8088 öffnen
            serverSocket = new ServerSocket(8888);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 8888.");
            System.exit(-1);
        }

        while (listening) {
            // Auf eingehende Verbindung warten
            new WebServerThread(serverSocket.accept()).start();
        }

        serverSocket.close();
    }
}
class WebServerThread extends Thread {

    private Socket clientSocket = null;

    public WebServerThread(Socket socket) {
        super("WebServerThread");
        clientSocket = socket;
    }

    public void run() {
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine, outputLine;

            boolean isRestApiCall = false;
            String restApiPath = "";
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received: " + inputLine);
                if (inputLine.startsWith("GET /time")) {
                    isRestApiCall = true;
                    restApiPath = "time";
                } else if (inputLine.startsWith("GET /date")) {
                    isRestApiCall = true;
                    restApiPath = "date";
                }
                if (inputLine.equals("")) {
                    break;
                }
            }

            if (isRestApiCall) {
                // HTTP-Statuscode 200 OK senden
                out.println("HTTP/1.0 200 OK");
                out.println("Date: " + new Date());
                out.println("Server: WebServer");
                out.println("Content-Type: text/plain");
                out.println("Connection: close");
                out.println("");

                if (restApiPath.equals("time")) {
                    // Aktuelle Uhrzeit im Format hh:mm:ss senden
                    SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
                    out.println(sdfTime.format(new Date()));
                } else if (restApiPath.equals("date")) {
                    // Aktuelles Datum im Format dd.mm.yyyy senden
                    SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy");
                    out.println(sdfDate.format(new Date()));
                }
            } else {
                // Nur Anfragen des Firefox-Browsers als User-Agent akzeptieren
                boolean validUserAgent = false;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("Received: " + inputLine);
                    if (inputLine.startsWith("User-Agent: Mozilla/5.0")) {
                        validUserAgent = true;
                    }
                    if (inputLine.equals("")) {
                        break;
                    }
                }

                if (!validUserAgent) {
                    // HTTP-Statuscode 406 senden, wenn der User-Agent nicht unterstützt wird
                    out.println("HTTP/1.0 406 Not Acceptable");
                    out.println("Date: " + new Date());
                    out.println("Server: WebServer");
                    out.println("Connection: close");
                    out.println("");
                } else {
                    // GET-Request auswerten
                    String fileName = "index.html";
                    while ((inputLine = in.readLine()) != null) {
                        System.out.println("Received: " + inputLine);
                        if (inputLine.startsWith("GET")) {
                            fileName = inputLine.split(" ")[1];
                        }
                        if (inputLine.equals("")) {
                            break;
                        }
                    }                // Überprüfen, ob Datei vorhanden ist
                    File file = new File("." + fileName);
                    if (!file.exists()) {
                        // HTTP-Statuscode 404 Not Found senden, wenn Datei nicht gefunden wurde
                        out.println("HTTP/1.0 404 Not Found");
                        out.println("Date: " + new Date());
                        out.println("Server: WebServer");
                        out.println("Connection: close");
                        out.println("");
                    } else {
                        // MIME-Type ermitteln
                        String contentType = "text/plain";
                        if (fileName.endsWith(".html") || fileName.endsWith(".htm")) {
                            contentType = "text/html";
                        } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                            contentType = "image/jpeg";
                        } else if (fileName.endsWith(".gif")) {
                            contentType = "image/gif";
                        } else if (fileName.endsWith(".png")) {
                            contentType = "image/png";
                        }

                        if (contentType.equals("text/plain")) {
                            // HTTP-Statuscode 200 OK senden
                            out.println("HTTP/1.0 200 OK");
                            out.println("Date: " + new Date());
                            out.println("Server: WebServer");
                            out.println("Content-Type: " + contentType);
                            out.println("Connection: close");
                            out.println("");
                            // Inhalt senden
                            if (fileName.equals("time")) {
                                // Aktuelle Uhrzeit im Format hh:mm:ss senden
                                SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
                                out.println(sdfTime.format(new Date()));
                            } else if (fileName.equals("date")) {
                                // Aktuelles Datum im Format dd.mm.yyyy senden
                                SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy");
                                out.println(sdfDate.format(new Date()));
                            }
                        } else {
                            // HTTP-Statuscode 200 OK senden, wenn die Datei gefunden wurde
                            out.println("HTTP/1.0 200 OK");
                            out.println("Date: " + new Date());
                            out.println("Server: WebServer");
                            out.println("Content-Type: " + contentType);
                            out.println("Content-Length: " + file.length());
                            out.println("Connection: close");
                            out.println("");

                            // Dateiinhalt senden
                            BufferedOutputStream outputStream = new BufferedOutputStream(clientSocket.getOutputStream());
                            FileInputStream fileInputStream = new FileInputStream(file);
                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, bytesRead);
                            }
                            fileInputStream.close();
                            outputStream.flush();
                        }
                    }
                    // Verbindung schließen
                    out.close();
                    in.close();
                    clientSocket.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}