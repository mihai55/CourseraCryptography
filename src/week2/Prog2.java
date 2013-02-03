package week2;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Prog2 {
	public static void main(String[] args) throws NoSuchAlgorithmException,
			NoSuchProviderException, NoSuchPaddingException,
			InvalidKeyException, InvalidAlgorithmParameterException,
			IOException, ShortBufferException, IllegalBlockSizeException,
			BadPaddingException {
		BigInteger skey = new BigInteger("140b41b22a29beb4061bda66b6747e14", 16);
		BigInteger ct = new BigInteger(
				"4ca00ff4c898d61e1edbf1800618fb2828a226d160dad07883d04e008a7897ee2e4b7465d5290d0c0e6c6822236e1daafb94ffe0c5da05d9476be028ad7c1d81",
				16);
		System.out
				.println(decryptCBCMode(skey.toByteArray(), ct.toByteArray()));
		ct = new BigInteger(
				"5b68629feb8606f9a6667670b75b38a5b4832d0f26e1ab7da33249de7d4afc48e713ac646ace36e872ad5fb8a512428a6e21364b0c374df45503473c5242a253",
				16);
		System.out
				.println(decryptCBCMode(skey.toByteArray(), ct.toByteArray()));

		skey = new BigInteger("36f18357be4dbd77f050515c73fcf9f2", 16);
		ct = new BigInteger(
				"69dda8455c7dd4254bf353b773304eec0ec7702330098ce7f7520d1cbbb20fc388d1b0adb5054dbd7370849dbf0b88d393f252e764f1f5f7ad97ef79d59ce29f5f51eeca32eabedd9afa9329",
				16);
		System.out
				.println(decryptCTRMode2(skey.toByteArray(), ct.toByteArray()));
		ct = new BigInteger(
				"770b80259ec33beb2561358a9f2dc617e46218c0a53cbeca695ae45faa8952aa0e311bde9d4e01726d3184c34451",
				16);
		// System.out.println(decryptCTRMode(skey.toByteArray(),
		// ct.toByteArray()));
		System.out
				.println(decryptCTRMode2(skey.toByteArray(), ct.toByteArray()));
	}

	private static String decryptCBCMode(byte[] keyBytes, byte[] cipherText)
			throws NoSuchAlgorithmException, NoSuchProviderException,
			NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IOException,
			ShortBufferException, IllegalBlockSizeException,
			BadPaddingException {
		byte[] ivBytes = Arrays.copyOf(cipherText, 16);

		IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
		SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");

		// decryption pass
		cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
		int ctLength = cipherText.length;
		byte[] plainText = new byte[cipher.getOutputSize(ctLength)];

		int ptLength = cipher.update(cipherText, 0, ctLength, plainText, 0);

		ptLength += cipher.doFinal(plainText, ptLength);
		String str = Utils.toHex(plainText);
		StringBuilder sb = new StringBuilder();
		for (int i = 16; i < ptLength; i++) {
			char c = (char) Integer.parseInt(str.substring(i * 2, (i + 1) * 2),
					16);
			sb.append(c);
		}
		return sb.toString();
	}

	// private static String decryptCTRMode(byte[] keyBytes, byte[] cipherText)
	// throws NoSuchAlgorithmException, NoSuchProviderException,
	// NoSuchPaddingException, InvalidKeyException,
	// InvalidAlgorithmParameterException, IOException,
	// ShortBufferException, IllegalBlockSizeException,
	// BadPaddingException {
	// byte[] ivBytes = Arrays.copyOf(cipherText, 16);
	// IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
	// SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
	//
	// Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding", "BC");
	//
	// // decryption pass
	// cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
	// int ctLength = cipherText.length;
	// byte[] plainText = new byte[cipher.getOutputSize(ctLength)];
	//
	// int ptLength = cipher.update(cipherText, 0, ctLength, plainText, 0);
	//
	// ptLength += cipher.doFinal(plainText, ptLength);
	// String str = Utils.toHex(plainText);
	// StringBuilder sb = new StringBuilder();
	// for (int i = 16; i < ptLength; i++) {
	// char c = (char) Integer.parseInt(str.substring(i * 2, (i + 1) * 2),
	// 16);
	// sb.append(c);
	// }
	// // System.out.println("plain text : " + str + " bytes: " + ptLength);
	// return sb.toString();
	//
	// }

	private static String decryptCTRMode2(byte[] keyBytes, byte[] cipherText)
			throws NoSuchAlgorithmException, NoSuchProviderException,
			NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException, IOException,
			ShortBufferException, IllegalBlockSizeException,
			BadPaddingException {
		byte[] ivBytes = Arrays.copyOf(cipherText, 16);

		for (int i = 16; i < cipherText.length; i += 16) {
			SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding", "BC");
			byte[] ct = new byte[ivBytes.length];
			cipher.init(Cipher.ENCRYPT_MODE, key);
			int ctLength = cipher.update(ivBytes, 0, ivBytes.length, ct, 0);
			ctLength += cipher.doFinal(cipherText, ctLength);
			int length = Math.min(ct.length, cipherText.length - i);
			for (int j = 0; j < length; j++) {
				System.out.print((char) (ct[j] ^ cipherText[i + j]));
			}
			BigInteger next = new BigInteger(ivBytes).add(BigInteger.ONE);
			ivBytes = next.toByteArray();
		}
		return "";

	}
}