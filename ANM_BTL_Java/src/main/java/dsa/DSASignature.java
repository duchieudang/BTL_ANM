package dsa;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class DSASignature {

    // Hàm băm SHA-1 cho thông điệp
    public static BigInteger hashMessage(String message) throws Exception {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        byte[] hashBytes = sha1.digest(message.getBytes("UTF-8"));
        return new BigInteger(1, hashBytes);
    }

    // Hàm băm SHA-1 từ r và s (kết hợp)
    public static String hashSignature(BigInteger r, BigInteger s) throws Exception {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        String input = r.toString() + s.toString();
        byte[] hashBytes = sha1.digest(input.getBytes("UTF-8"));

        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }

    // Tính g = h^((p-1)/q) mod p
    public static BigInteger calculateG(BigInteger p, BigInteger q, BigInteger h) {
        return h.modPow(p.subtract(BigInteger.ONE).divide(q), p);
    }

    // Sinh ngẫu nhiên k trong [1, q-1]
    public static BigInteger generateK(BigInteger q) {
        SecureRandom random = new SecureRandom();
        BigInteger k;
        do {
            k = new BigInteger(q.bitLength(), random);
        } while (k.compareTo(BigInteger.ONE) < 0 || k.compareTo(q.subtract(BigInteger.ONE)) > 0);
        return k;
    }

    // Ký DSA
    public static BigInteger[] sign(String message, BigInteger p, BigInteger q, BigInteger g,
                                    BigInteger x, BigInteger k) throws Exception {
        BigInteger r = g.modPow(k, p).mod(q);
        if (r.equals(BigInteger.ZERO)) {
            throw new IllegalArgumentException("r = 0, chọn k khác.");
        }

        BigInteger h = hashMessage(message);
        BigInteger kInv = k.modInverse(q);
        BigInteger s = (kInv.multiply(h.add(x.multiply(r)))).mod(q);
        if (s.equals(BigInteger.ZERO)) {
            throw new IllegalArgumentException("s = 0, chọn k khác.");
        }

        return new BigInteger[]{r, s};
    }

    // Chứng thực DSA truyền thống
    public static boolean verify(String message, BigInteger p, BigInteger q, BigInteger g,
                                 BigInteger y, BigInteger r, BigInteger s) throws Exception {
        if (r.compareTo(BigInteger.ONE) < 0 || r.compareTo(q.subtract(BigInteger.ONE)) > 0) return false;
        if (s.compareTo(BigInteger.ONE) < 0 || s.compareTo(q.subtract(BigInteger.ONE)) > 0) return false;

        BigInteger w = s.modInverse(q);
        BigInteger h = hashMessage(message);
        BigInteger u1 = h.multiply(w).mod(q);
        BigInteger u2 = r.multiply(w).mod(q);

        BigInteger v = g.modPow(u1, p).multiply(y.modPow(u2, p)).mod(p).mod(q);

        return v.equals(r);
    }

    // Chứng thực thêm bước so sánh hash chữ ký
    public static boolean verifyWithSignatureHash(String message, BigInteger p, BigInteger q, BigInteger g,
                                                  BigInteger y, BigInteger r, BigInteger s,
                                                  String expectedSignatureHash) throws Exception {
        boolean verified = verify(message, p, q, g, y, r, s);
        if (!verified) return false;

        String hash = hashSignature(r, s);
        return hash.equals(expectedSignatureHash);
    }

    public static void main(String[] args) throws Exception {
        // Tham số mẫu (nhỏ, không dùng thực tế)
        BigInteger p = new BigInteger("7879");
        BigInteger q = new BigInteger("101");
        BigInteger h = new BigInteger("2");
        BigInteger g = calculateG(p, q, h);

        if (g.compareTo(BigInteger.ONE) <= 0) throw new Exception("g không hợp lệ, chọn lại h.");

        BigInteger x = new BigInteger("45"); // private key
        BigInteger y = g.modPow(x, p);       // public key
        BigInteger k = generateK(q);         // random k

        String message = "hello DSA";

        // Ký
        BigInteger[] signature = sign(message, p, q, g, x, k);
        BigInteger r = signature[0];
        BigInteger s = signature[1];

        System.out.println("Chữ ký:");
        System.out.println("r = " + r);
        System.out.println("s = " + s);

        // Băm chữ ký
        String signatureHash = hashSignature(r, s);
        System.out.println("Hash của chữ ký (r||s): " + signatureHash);

        // Chứng thực bình thường
        boolean valid = verify(message, p, q, g, y, r, s);
        System.out.println("Chữ ký hợp lệ (kiểu chuẩn)? " + valid);

        // Chứng thực có kiểm tra hash chữ ký
        boolean validHash = verifyWithSignatureHash(message, p, q, g, y, r, s, signatureHash);
        System.out.println("Chữ ký hợp lệ và trùng hash? " + validHash);
    }
}
