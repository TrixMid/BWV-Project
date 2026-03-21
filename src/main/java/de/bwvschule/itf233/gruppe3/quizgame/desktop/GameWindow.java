package de.bwvschule.itf233.gruppe3.quizgame.desktop;

import me.friwi.jcefmaven.CefAppBuilder;
import me.friwi.jcefmaven.CefInitializationException;
import me.friwi.jcefmaven.MavenCefAppHandlerAdapter;
import me.friwi.jcefmaven.UnsupportedPlatformException;
import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.handler.CefAppHandlerAdapter;

import javax.net.ssl.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.security.cert.X509Certificate;

public class GameWindow extends JFrame {
    private final CefApp cefApp;
    private final CefClient client;
    private final CefBrowser browser;

    public GameWindow(CefApp cefApp) {
        this.cefApp = cefApp; // Use the already-initialized app
        this.client = cefApp.createClient();
        this.browser = client.createBrowser("http://localhost:8076/levels2", false, false);

        getContentPane().add(browser.getUIComponent(), BorderLayout.CENTER);

        setSize(1024, 768);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Proper disposal
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                CefApp.getInstance().dispose();
                System.exit(0);
            }
        });
    }
    // Static helper to prepare everything safely
    public static void launch() {
        disableSSLCertificateChecking();

        try {
            CefAppBuilder builder = new CefAppBuilder();
            builder.setInstallDir(new File("jcef-bundle"));
            builder.getCefSettings().windowless_rendering_enabled = false;

            // 1. Build the app OUTSIDE the Swing thread
            CefApp cefApp = builder.build();

            // 2. NOW open the window on the Swing thread
            SwingUtilities.invokeLater(() -> {
                GameWindow window = new GameWindow(cefApp);
                window.setVisible(true);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void disableSSLCertificateChecking() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return null; }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                    }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Also ignore hostname mismatches
            HostnameVerifier allHostsValid = (hostname, session) -> true;
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            System.out.println("SSL Certificate checking disabled for native download...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
