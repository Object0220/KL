package com.vlusi.klintelligent.utils;


public class OADDataManager {
	public final static byte OAD_SOH = 0x01; // 128数据包开始

	public final static byte OAD_STX = 0x02; // 1024数据包开始

	public final static byte OAD_ETX = 0x03; // 正文结束
	public final static byte OAD_EOT = 0x04; // 数据包结束
	public final static byte OAD_ENQ = 0x05; // 询问字符

	public final static byte OAD_ACK = 0x06; // 确认
	public final static byte OAD_NAK = 0x15; // 拒绝
	public final static byte OAD_CAN = 0x18; // 取消

	public final static byte OAD_C = 0x43;
	public final static byte OAD_D = 0x44;

	public static byte[] getFirstFrame(String fileName, String fileSize,
			int byteSize) {

		byte[] bytes = new byte[byteSize];

		bytes[0] = byteSize > 1024 ? OAD_STX : OAD_SOH;
		bytes[1] = 0x0;
		bytes[2] = (byte) ~bytes[1];

		int fileNameLenth = fileName.length();

		for (int i = 0; i < fileNameLenth; i++) {
			bytes[3 + i] = (byte) fileName.charAt(i);
		}

		bytes[fileNameLenth + 3] = 0x0;

		int fileSizelenth = fileSize.length();

		for (int i = 0; i < fileSizelenth; i++) {
			bytes[3 + fileNameLenth + 1 + i] = (byte) fileSize.charAt(i);
		}

		for (int i = 3 + fileNameLenth + 1 + fileSizelenth; i < byteSize - 2; i++) {
			bytes[i] = 0x0;
		}
		byte[] xxx = new byte[byteSize - 3];
		System.arraycopy(bytes, 3, xxx, 0, xxx.length);

		short crc = Bytecal_crc(xxx, byteSize - 5);

		bytes[byteSize - 2] = (byte) (crc / 256);
		bytes[byteSize - 1] = (byte) (crc % 256);

		return bytes;
	}

	public static void generalFrame(byte[] _data, int _dataLenth, byte _index,
			byte[] _buffer, int _bufferSize) {

		_buffer[0] = (_dataLenth == 1024 ? OAD_STX : OAD_SOH);
		_buffer[1] = _index;
		_buffer[2] = (byte) ~_index;
		for (int i = 0; i < _dataLenth; i++) {
			_buffer[3 + i] = _data[i];
		}
		for (int j = 3 + _dataLenth; j < _bufferSize - 2; j++) {
			_buffer[j] = 0x0;
		}

		byte[] xxx = new byte[_bufferSize - 3];
		System.arraycopy(_buffer, 3, xxx, 0, xxx.length);
		short crc = Bytecal_crc(xxx, _bufferSize - 5);
		_buffer[_bufferSize - 2] = (byte) (crc / 256);
		_buffer[_bufferSize - 1] = (byte) (crc % 256);
	}

	public static void lastFrame(byte[] _buffer, int _bufferSize) {
		_buffer[0] = OAD_SOH;
		_buffer[1] = 0x0;
		_buffer[2] = (byte) ~_buffer[1];
		for (int j = 3; j < _bufferSize - 2; j++) {
			_buffer[j] = 0x0;
		}

		byte[] xxx = new byte[_bufferSize - 3];
		System.arraycopy(_buffer, 3, xxx, 0, xxx.length);
		short crc = Bytecal_crc(xxx, _bufferSize - 5);
		_buffer[_bufferSize - 2] = (byte) (crc / 256);
		_buffer[_bufferSize - 1] = (byte) (crc % 256);
		
	}

	public static short Bytecal_crc(byte[] bufDate, int len) {
		short cksum = 0;
		for (int i = 0; i < len; i++) {
			char tmpBuf = (char) bufDate[i];
			cksum = (short) (cksum ^ (short) tmpBuf << 8);
			for (int j = 8; j != 0; j--) {
				if ((cksum & 0x8000) != 0) {
					cksum = (short) (cksum << 1 ^ 0x1021);
				} else {
					cksum = (short) (cksum << 1);
				}
			}
		}
		return cksum;
	}

}
