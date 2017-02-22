package com.bpcbt.sv.camel.utils;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class ResponseEncoder implements ProtocolEncoder {
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
		byte[] bytes = (byte[]) message;
		String capacity = String.format("%04d", bytes.length);
		IoBuffer ioBuffer = IoBuffer.allocate(3, false);
		ioBuffer.setAutoExpand(true);
		ioBuffer.setAutoShrink(true);

		Charset charset = Charset.forName("CP1251");
		CharsetEncoder encoder = charset.newEncoder();

		ioBuffer.putString(capacity, encoder);
		ioBuffer.put(bytes);
		ioBuffer.flip();    //Flip it or there will be nothing to send
		out.write(ioBuffer);
		out.flush();
	}

	@Override
	public void dispose(IoSession session) throws Exception {
		// TODO Auto-generated method stub

	}
}
