package speedatacom.a3310libs.des;

public class Pboc3DesMACUtils {
    public static void main(String[] args) {
        // EF433E7F5E7909DB628A639652353817
        // getMac();
    }

    /**
     * 1.�ȶԿ���ȡ�����ָ�4�ֽں�00000000�ճ�8�ֽڳ�ʼ������
     * 2.��Ӧ��ά����Կ��8418000004��5���ֽڼ���һ��3desmac����ʼ����Ϊ��һ���õ���8�ֽڣ�
     * 3.��Ƭ����Ӧ�ý������8418000004+mac��macΪ�ڶ����õ��Ľ��
     * @param icv
     * @return mac
     */

    public static byte[] getMac(byte[] icv) {
        byte[] key = {(byte) 0xEF, 0x43, 0x3E, 0x7F, 0x5E, 0x79, 0x09,
                (byte) 0xDB, 0x62, (byte) 0x8A, 0x63, (byte) 0x96, 0x52, 0x35,
                0x38, 0x17};
        byte[] data = {(byte) 0x84, 0x18, 0x00, 0x00, 0x04};
//        byte[] icv = {0x011, 0x022, 0x33, 0x44, 0x00, 0x00, 0x00, 0x00};
        try {
            byte[] calculatePboc3desMAC = calculatePboc3desMAC(data, key, icv);
            return calculatePboc3desMAC;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static final byte[] ZERO_IVC = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};

    /**
     * ����MAC(hex) PBOC_3DES_MAC(����ISO9797Alg3Mac��׼) (16��������8000000000000000)
     * ǰn-1��ʹ�õ�����ԿDES ʹ����Կ����Կ����8�ֽڣ� ���1��ʹ��˫����Կ3DES ��ʹ��ȫ��16�ֽ���Կ��
     * <p>
     * �㷨���裺��ʼ����ΪD����ʼ����ΪI��3DES��ԿΪK0����Կ��8�ֽ�DES��ԿK1��
     * 1������D���鲢����䣺���ֽ�����D���з��飬ÿ��8���ֽڣ�
     * �����Ŵ�0��ʼ,�ֱ�ΪD0...Dn�����һ�����鲻��8�ֽڵģ������һ���ֽ�80������ȫ�����00
     * ����8�ֽڵģ�����һ��8�ֽڷ��飨80000000 00000000����
     * 2������desѭ�����ܣ���1��D0�ͳ�ʼ����I���а�λ���õ����O0;(
     * 2)ʹ����ԿK1��DES���ܽ��O0�õ����I1,��I1��D1��λ���õ����D1��(3)ѭ���ڶ�����õ����Dn��
     * 3����Dnʹ��16�ֽ���ԿK0����3DES���ܣ��õ��Ľ����������Ҫ��MAC��
     *
     * @param data �����������
     * @param key  16�ֽ���Կ
     * @param icv  �㷨����
     * @return macǩ��
     * @throws Exception
     */
    public static byte[] calculatePboc3desMAC(byte[] data, byte[] key,
                                              byte[] icv) throws Exception {

        if (key == null || data == null)
            throw new RuntimeException("data or key is null.");
        if (key.length != 16)
            throw new RuntimeException("key length is not 16 byte.");

        byte[] leftKey = new byte[8];
        System.arraycopy(key, 0, leftKey, 0, 8);

        // ������ݣ�8�ֽڿ�/Block��
        final int dataLength = data.length;
        final int blockCount = dataLength / 8 + 1;
        final int lastBlockLength = dataLength % 8;

        byte[][] dataBlock = new byte[blockCount][8];
        for (int i = 0; i < blockCount; i++) {
            int copyLength = i == blockCount - 1 ? lastBlockLength : 8;
            System.arraycopy(data, i * 8, dataBlock[i], 0, copyLength);
        }
        dataBlock[blockCount - 1][lastBlockLength] = (byte) 0x80;

        byte[] desXor = DesUtils.xOr(dataBlock[0], icv);
        for (int i = 1; i < blockCount; i++) {
            byte[] des = DesUtils.encryptByDesCbc(desXor, leftKey);
            desXor = DesUtils.xOr(dataBlock[i], des);
        }
        desXor = DesUtils.encryptBy3DesCbc(desXor, key);
        return desXor;
    }
}
