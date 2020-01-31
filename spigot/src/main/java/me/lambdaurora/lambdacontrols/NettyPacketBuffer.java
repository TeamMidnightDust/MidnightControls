package me.lambdaurora.lambdacontrols;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class NettyPacketBuffer extends PacketBuffer
{
    public NettyPacketBuffer(ByteBuf byteBuf)
    {
        super(byteBuf);
    }

    @Override
    public int readVarint()
    {
        int var1 = 0;
        int var2 = 0;
        byte var3;

        do {
            var3 = this.readByte();
            var1 |= (var3 & 127) << var2++ * 7;

            if (var2 > 5)
                throw new RuntimeException("VarInt too big");
        } while ((var3 & 128) == 128);

        return var1;
    }

    @Override
    public void writeVarint(int input)
    {
        while ((input & -128) != 0) {
            this.writeByte(input & 127 | 128);
            input >>>= 7;
        }

        this.writeByte(input);
    }

    @Override
    public String readString(int maxLength)
    {
        int var2 = this.readVarint();

        if (var2 > maxLength * 4)
            throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + var2 + " > " + maxLength * 4 + ")");
        else if (var2 < 0)
            throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
        else {
            String var3 = this.readCharSequence(var2, StandardCharsets.UTF_8).toString();

            if (var3.length() > maxLength)
                throw new DecoderException("The received string length is longer than maximum allowed (" + var2 + " > " + maxLength + ")");
            else
                return var3;
        }
    }

    @Override
    public void writeString(String string)
    {
        byte[] var2 = string.getBytes(Charset.forName("UTF-8"));

        if (var2.length > 32767) {
            throw new EncoderException("String too big (was " + string.length() + " data encoded, max " + 32767 + ")");
        } else {
            this.writeVarint(var2.length);
            this.writeCharSequence(string, StandardCharsets.UTF_8);
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        NettyPacketBuffer buffer = (NettyPacketBuffer) o;

        return byteBuf.equals(buffer.byteBuf);
    }

    @Override
    public int compareTo(ByteBuf buffer)
    {
        return this.byteBuf.compareTo(buffer);
    }

    @Override
    public String toString()
    {
        return this.byteBuf.toString();
    }
}
