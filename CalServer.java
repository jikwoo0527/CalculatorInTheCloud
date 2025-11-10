import java.io.*;
import java.net.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class CalServer {
    public static void main(String[] args) throws IOException {
        ServerSocket listener;

        int nPort;

        nPort = 6789;
        listener = new ServerSocket(nPort);
        System.out.println("Server start.. (port#=" + nPort + ")\n");

        // Multithreading
        ExecutorService pool = Executors.newFixedThreadPool(20);
        while (true) {
            Socket connectionSocket = listener.accept();
            pool.execute(new Calculator(connectionSocket));
        }
    }

    private static class Calculator implements Runnable {
        private Socket socket;

        Calculator(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            System.out.println("Connected: " + socket);
            BufferedReader inFromClient = null;
            DataOutputStream outToClient = null;
            try {
                String input;
                String operator = "";
                String result = "";
                String answer;
                int num1 = 0;
                int num2 = 0;

                // execution
                inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                outToClient = new DataOutputStream(socket.getOutputStream());

                // client input
                input = inFromClient.readLine();
                if (input == null) {
                    writeError(outToClient, 400, "BAD_REQUEST", "empty input");
                    return;
                }
                System.out.println("FROM CLIENT: " + input);

                // Split
                input = input.trim().toLowerCase();
                String[] split = input.split("\\s+");
                if (split.length != 3) {
                    writeError(outToClient, 400, "BAD_REQUEST",
                            "expected '<op> <a> <b>'");
                    return;
                } else {
                    operator = split[0];
                    try {
                        num1 = Integer.parseInt(split[1]);
                        num2 = Integer.parseInt(split[2]);
                    } catch (NumberFormatException nfe) {
                        writeError(outToClient, 400, "BAD_REQUEST", "operands must be integers");
                        return;
                    }
                }

                // Calculate
                switch (operator) {
                    case "add":
                        result = Integer.toString(num1 + num2);
                        break;
                    case "sub":
                        result = Integer.toString(num1 - num2);
                        break;
                    case "div":
                        if (num2 == 0) {
                            writeError(outToClient, 422, "UNPROCESSABLE_ENTITY", "divide by zero");
                            return;
                        }
                        result = Integer.toString(num1 / num2);
                        break;
                    case "mul":
                        result = Integer.toString(num1 * num2);
                        break;
                    default:
                        writeError(outToClient, 422, "UNPROCESSABLE_ENTITY",
                                "unsupported operator '" + operator + "' (allowed: add, sub, mul, div)");
                        return;
                }

                answer = "STATUS 200 OK\n" + "Result: " + result + "\n";
                outToClient.writeBytes(answer);

            } catch (Exception e) {
                try {
                    if (outToClient != null) {
                        writeError(outToClient, 500, "INTERNAL_ERROR", "unexpected server error");
                    }
                } catch (Exception ignore) {}
                System.out.println("Error: " + socket + " msg=" + e.getMessage());
            } finally {
                try { if (inFromClient != null) inFromClient.close(); } catch (IOException ignore) {}
                try { if (outToClient != null) outToClient.close(); } catch (IOException ignore) {}
                try { socket.close(); } catch (IOException ignore) {}
                System.out.println("Closed: " + socket);
            }
        }

        private void writeError(DataOutputStream out, int code, String status, String message) throws IOException {
            String resp = "STATUS " + code + " " + status + "\n" +
                    "Message: " + message + "\n";
            out.writeBytes(resp);
        }
    }
}