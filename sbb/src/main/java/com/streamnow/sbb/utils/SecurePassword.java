package com.streamnow.lindaumobile.utils;

/**
 * Created by Miguel Angel on 27/07/2016.
 */
/*
public class SecurePassword {

    public static KeyStore keyStore;
    public static SecurePassword securePassword;

    public SecurePassword() {

        securePassword = this;
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SecurePassword  getKeyStore(){
        return securePassword;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public  void createNewKeys(String alias) {
        try {
            // Create new key if needed
            if (!keyStore.containsAlias(alias)) {
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 1);
                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(this)
                        .setAlias(alias)
                        .setSubject(new X500Principal("CN=Sample Name, O=Android Authority"))
                        .setSerialNumber(BigInteger.ONE)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
                generator.initialize(spec);

               // KeyPair keyPair = generator.generateKeyPair();
                refreshKeys();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public  String encryptString(String alias , String text) {
        String encryptedText = null;
        try {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(alias, null);
            RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();

            if(text.isEmpty()) {
                return "";
            }

            Cipher inCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "AndroidOpenSSL");
            inCipher.init(Cipher.ENCRYPT_MODE, publicKey);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(
                    outputStream, inCipher);
            cipherOutputStream.write(text.getBytes("UTF-8"));
            cipherOutputStream.close();

            byte [] vals = outputStream.toByteArray();
            encryptedText = Base64.encodeToString(vals, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedText;
    }

    public String decryptString(String alias, String cipherText) {
        String decryptedText = null;
        try {
            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)keyStore.getEntry(alias, null);
            PrivateKey privateKey = privateKeyEntry.getPrivateKey();

            Cipher output = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            output.init(Cipher.DECRYPT_MODE, privateKey);



            CipherInputStream cipherInputStream = new CipherInputStream(
                    new ByteArrayInputStream(Base64.decode(cipherText, Base64.DEFAULT)), output);
            ArrayList<Byte> values = new ArrayList<>();
            int nextByte;
            while ((nextByte = cipherInputStream.read()) != -1) {
                values.add((byte)nextByte);
            }

            byte[] bytes = new byte[values.size()];
            for(int i = 0; i < bytes.length; i++) {
                bytes[i] = values.get(i).byteValue();
            }

            decryptedText = new String(bytes, 0, bytes.length, "UTF-8");

        } catch (Exception e) {
            Log.e("Exception", Log.getStackTraceString(e));
            e.printStackTrace();
        }
        return decryptedText;
    }

    private void refreshKeys(){
        List<String > keyAliases = new ArrayList<>();
        try {
            Enumeration<String> aliases = keyStore.aliases();
            System.out.println("aliases: " + aliases.nextElement());
            while (aliases.hasMoreElements()) {
                keyAliases.add(aliases.nextElement());
            }
            System.out.println("size: " + keyAliases.size());
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
*/