/* From http://java.sun.com/docs/books/tutorial/index.html */

/*
 * Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 * -Redistribution in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class EchoClient {
    public static void main(String[] args) throws IOException {

        Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            echoSocket = new Socket("taranis", 7);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(
                                        echoSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: taranis.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                               + "the connection to: taranis.");
            System.exit(1);
        }

  BufferedReader stdIn = new BufferedReader(
                                   new InputStreamReader(System.in));
  String userInput;

  while ((userInput = stdIn.readLine()) != null) {
      out.println(userInput);
      System.out.println("echo: " + in.readLine());
  }

  out.close();
  in.close();
  stdIn.close();
  echoSocket.close();
    }
}

////////////////////////////////////////////////////////////


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class KnockKnockServer {
  public static void main(String[] args) throws IOException {

    ServerSocket serverSocket = null;
    try {
      serverSocket = new ServerSocket(4444);
    } catch (IOException e) {
      System.err.println("Could not listen on port: 4444.");
      System.exit(1);
    }

    Socket clientSocket = null;
    try {
      clientSocket = serverSocket.accept();
    } catch (IOException e) {
      System.err.println("Accept failed.");
      System.exit(1);
    }

    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
    BufferedReader in = new BufferedReader(new InputStreamReader(
        clientSocket.getInputStream()));
    String inputLine, outputLine;
    KnockKnockProtocol kkp = new KnockKnockProtocol();

    outputLine = kkp.processInput(null);
    out.println(outputLine);

    while ((inputLine = in.readLine()) != null) {
      outputLine = kkp.processInput(inputLine);
      out.println(outputLine);
      if (outputLine.equals("Bye."))
        break;
    }
    out.close();
    in.close();
    clientSocket.close();
    serverSocket.close();
  }
}

class KnockKnockProtocol {
  private static final int WAITING = 0;

  private static final int SENTKNOCKKNOCK = 1;

  private static final int SENTCLUE = 2;

  private static final int ANOTHER = 3;

  private static final int NUMJOKES = 5;

  private int state = WAITING;

  private int currentJoke = 0;

  private String[] clues = { "Turnip", "Little Old Lady", "Atch", "Who",
      "Who" };

  private String[] answers = { "Turnip the heat, it's cold in here!",
      "I didn't know you could yodel!", "Bless you!",
      "Is there an owl in here?", "Is there an echo in here?" };

  public String processInput(String theInput) {
    String theOutput = null;

    if (state == WAITING) {
      theOutput = "Knock! Knock!";
      state = SENTKNOCKKNOCK;
    } else if (state == SENTKNOCKKNOCK) {
      if (theInput.equalsIgnoreCase("Who's there?")) {
        theOutput = clues[currentJoke];
        state = SENTCLUE;
      } else {
        theOutput = "You're supposed to say \"Who's there?\"! "
            + "Try again. Knock! Knock!";
      }
    } else if (state == SENTCLUE) {
      if (theInput.equalsIgnoreCase(clues[currentJoke] + " who?")) {
        theOutput = answers[currentJoke] + " Want another? (y/n)";
        state = ANOTHER;
      } else {
        theOutput = "You're supposed to say \"" + clues[currentJoke]
            + " who?\"" + "! Try again. Knock! Knock!";
        state = SENTKNOCKKNOCK;
      }
    } else if (state == ANOTHER) {
      if (theInput.equalsIgnoreCase("y")) {
        theOutput = "Knock! Knock!";
        if (currentJoke == (NUMJOKES - 1))
          currentJoke = 0;
        else
          currentJoke++;
        state = SENTKNOCKKNOCK;
      } else {
        theOutput = "Bye.";
        state = WAITING;
      }
    }
    return theOutput;
  }
}

////////////////////////////////////////////////////////////


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class KKMultiServer {
  public static void main(String[] args) throws IOException {
    ServerSocket serverSocket = null;
    boolean listening = true;

    try {
      serverSocket = new ServerSocket(4444);
    } catch (IOException e) {
      System.err.println("Could not listen on port: 4444.");
      System.exit(-1);
    }

    while (listening)
      new KKMultiServerThread(serverSocket.accept()).start();

    serverSocket.close();
  }
}

class KKMultiServerThread extends Thread {
  private Socket socket = null;

  public KKMultiServerThread(Socket socket) {
    super("KKMultiServerThread");
    this.socket = socket;
  }

  public void run() {

    try {
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
      BufferedReader in = new BufferedReader(new InputStreamReader(socket
          .getInputStream()));

      String inputLine, outputLine;
      KnockKnockProtocol kkp = new KnockKnockProtocol();
      outputLine = kkp.processInput(null);
      out.println(outputLine);

      while ((inputLine = in.readLine()) != null) {
        outputLine = kkp.processInput(inputLine);
        out.println(outputLine);
        if (outputLine.equals("Bye"))
          break;
      }
      out.close();
      in.close();
      socket.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

class KnockKnockProtocol {
  private static final int WAITING = 0;

  private static final int SENTKNOCKKNOCK = 1;

  private static final int SENTCLUE = 2;

  private static final int ANOTHER = 3;

  private static final int NUMJOKES = 5;

  private int state = WAITING;

  private int currentJoke = 0;

  private String[] clues = { "Turnip", "Little Old Lady", "Atch", "Who",
      "Who" };

  private String[] answers = { "Turnip the heat, it's cold in here!",
      "I didn't know you could yodel!", "Bless you!",
      "Is there an owl in here?", "Is there an echo in here?" };

  public String processInput(String theInput) {
    String theOutput = null;

    if (state == WAITING) {
      theOutput = "Knock! Knock!";
      state = SENTKNOCKKNOCK;
    } else if (state == SENTKNOCKKNOCK) {
      if (theInput.equalsIgnoreCase("Who's there?")) {
        theOutput = clues[currentJoke];
        state = SENTCLUE;
      } else {
        theOutput = "You're supposed to say \"Who's there?\"! "
            + "Try again. Knock! Knock!";
      }
    } else if (state == SENTCLUE) {
      if (theInput.equalsIgnoreCase(clues[currentJoke] + " who?")) {
        theOutput = answers[currentJoke] + " Want another? (y/n)";
        state = ANOTHER;
      } else {
        theOutput = "You're supposed to say \"" + clues[currentJoke]
            + " who?\"" + "! Try again. Knock! Knock!";
        state = SENTKNOCKKNOCK;
      }
    } else if (state == ANOTHER) {
      if (theInput.equalsIgnoreCase("y")) {
        theOutput = "Knock! Knock!";
        if (currentJoke == (NUMJOKES - 1))
          currentJoke = 0;
        else
          currentJoke++;
        state = SENTKNOCKKNOCK;
      } else {
        theOutput = "Bye.";
        state = WAITING;
      }
    }
    return theOutput;
  }
}

////////////////////////////////////////////////////////////


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class KnockKnockClient {
  public static void main(String[] args) throws IOException {

    Socket kkSocket = null;
    PrintWriter out = null;
    BufferedReader in = null;

    try {
      kkSocket = new Socket("taranis", 4444);
      out = new PrintWriter(kkSocket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(kkSocket
          .getInputStream()));
    } catch (UnknownHostException e) {
      System.err.println("Don't know about host: taranis.");
      System.exit(1);
    } catch (IOException e) {
      System.err
          .println("Couldn't get I/O for the connection to: taranis.");
      System.exit(1);
    }

    BufferedReader stdIn = new BufferedReader(new InputStreamReader(
        System.in));
    String fromServer;
    String fromUser;

    while ((fromServer = in.readLine()) != null) {
      System.out.println("Server: " + fromServer);
      if (fromServer.equals("Bye."))
        break;

      fromUser = stdIn.readLine();
      if (fromUser != null) {
        System.out.println("Client: " + fromUser);
        out.println(fromUser);
      }
    }

    out.close();
    in.close();
    stdIn.close();
    kkSocket.close();
  }
}

