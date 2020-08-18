package com.chilke.Sokoban;

public class Utility {
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String bytesToLurd(byte[] bytes, int moves) {
        StringBuilder sb = new StringBuilder(bytes.length*4);

        int index = 0;

        for (int i = 0; i < bytes.length; i++) {
            int cur = bytes[i] & 0xFF;
            for (int j = 0; j < 4; j++) {
                if (index >= moves) {
                    break;
                }
                int cb = cur & 0b11000000;

                //In Scores.Sol, binary data represents LURD move list with the following 2 bit sets
                //00 - Up
                //01 - Right
                //10 - Down
                //11 - Left
                switch (cb) {
                    case 0:
                        sb.append('U');
                        break;
                    case 0b1000000:
                        sb.append('R');
                        break;
                    case 0b10000000:
                        sb.append('D');
                        break;
                    case 0b11000000:
                        sb.append('L');
                        break;
                }
                cur <<= 2;
                index++;
            }
        }

        return sb.toString();
    }

    public static byte[] lurdToBytes(String lurdSolution) {
        byte[] bytes = new byte[(lurdSolution.length()+3)/4];
        int index = 0;
        for (int i = 0; i < bytes.length; i++) {
            int cur = 0;
            //In Scores.Sol, binary data represents LURD move list with the following 2 bit sets
            //00 - Up
            //01 - Right
            //10 - Down
            //11 - Left
            for (int j = 0; j < 4; j++) {
                cur <<= 2;
                if (index < lurdSolution.length()) {
                    switch (lurdSolution.charAt(index)) {
                        case 'R':
                            cur |= 1;
                            break;
                        case 'D':
                            cur |= 2;
                            break;
                        case 'L':
                            cur |= 3;
                            break;
                    }
                }
                index++;
            }
            bytes[i] = (byte)cur;
        }

        return bytes;
    }
}
