package jp.delightworks.unityplugin;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;

public class AndroidCertUtil {
    public static KeyStore getKeyStore(String preStoredCertStr)
            throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write("-----BEGIN CERTIFICATE-----\n".getBytes());
        outputStream.write(preStoredCertStr.getBytes());
        outputStream.write("\n-----END CERTIFICATE-----\n".getBytes());
        ByteArrayInputStream inStream = new ByteArrayInputStream(outputStream.toByteArray());

        KeyStore customKeyStore = getEmptyKeyStore();
        loadX509Certificates(customKeyStore, inStream);

        return customKeyStore;
    }

    private static KeyStore getEmptyKeyStore()
            throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        KeyStore ks = KeyStore.getInstance("BKS");
        ks.load(null);
        return ks;
    }

    private static void loadX509Certificates(KeyStore ks, InputStream is)
            throws KeyStoreException, CertificateException, IOException {
        try {
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            Collection<? extends Certificate> X509s = factory.generateCertificates(is);
            X509Certificate[] x509Array = X509s.toArray(new X509Certificate[X509s.size()]);

            for (int i = 0; i < x509Array.length; ++i) {
                X509Certificate x509 = x509Array[i];
                String alias = x509.getSubjectDN().getName();
                ks.setCertificateEntry(alias, x509);
            }
        } finally {
            is.close();
        }
    }
}
