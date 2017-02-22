package com.bpcbt.sv.camel.utils;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public class ResponseDecoder extends CumulativeProtocolDecoder {

	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		byte[] header = new byte[4];
		if (in.remaining() < header.length) {
			return false;
		}
		in.get(header);
		int packetLength = getIntfromByteArray(header);
		if (in.remaining() < packetLength) {
			return false;
		} else {
			byte[] dst = new byte[packetLength];
			in.get(dst);
			out.write(dst);
		}
		return true;
	}

	int getIntfromByteArray(byte[] bytes) {
		//return Integer.valueOf(Arrays.toString(bytes));
		//return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
		return Integer.valueOf(new String(bytes));
	}
}
