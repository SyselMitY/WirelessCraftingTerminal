/*
 * This file is part of Wireless Crafting Terminal. Copyright (c) 2017, p455w0rd
 * (aka TheRealp455w0rd), All rights reserved unless otherwise stated.
 *
 * Wireless Crafting Terminal is free software: you can redistribute it and/or
 * modify it under the terms of the MIT License.
 *
 * Wireless Crafting Terminal is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the MIT License for
 * more details.
 *
 * You should have received a copy of the MIT License along with Wireless
 * Crafting Terminal. If not, see <https://opensource.org/licenses/MIT>.
 */
package p455w0rd.wct.sync;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import p455w0rd.wct.init.ModNetworking;
import p455w0rd.wct.sync.network.INetworkInfo;

@SuppressWarnings("rawtypes")
public abstract class WCTPacket implements Packet {

	private PacketBuffer p;
	private PacketCallState caller;

	public void serverPacketData(final INetworkInfo manager, final WCTPacket packet, final EntityPlayer player) {
		throw new UnsupportedOperationException("This packet ( " + getPacketID() + " does not implement a server side handler.");
	}

	public final int getPacketID() {
		return WCTPacketHandlerBase.PacketTypes.getID(this.getClass()).ordinal();
	}

	public void clientPacketData(final INetworkInfo network, final WCTPacket packet, final EntityPlayer player) {
		throw new UnsupportedOperationException("This packet ( " + getPacketID() + " does not implement a client side handler.");
	}

	protected void configureWrite(final ByteBuf data) {
		data.capacity(data.readableBytes());
		p = new PacketBuffer(data);
	}

	public FMLProxyPacket getProxy() {
		if (p.array().length > 2 * 1024 * 1024) // 2k walking room :)
		{
			throw new IllegalArgumentException("Sorry AE2 made a " + p.array().length + " byte packet by accident!");
		}

		final FMLProxyPacket pp = new FMLProxyPacket(p, ModNetworking.instance().getChannel());

		return pp;
	}

	@Override
	public void readPacketData(final PacketBuffer buf) throws IOException {
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public void writePacketData(final PacketBuffer buf) throws IOException {
		throw new RuntimeException("Not Implemented");
	}

	public ByteArrayInputStream getPacketByteArray(ByteBuf stream, int readerIndex, int readableBytes) {
		final ByteArrayInputStream bytes;
		if (stream.hasArray()) {
			bytes = new ByteArrayInputStream(stream.array(), readerIndex, readableBytes);
		}
		else {
			byte[] data = new byte[stream.capacity()];
			stream.getBytes(readerIndex, data, 0, readableBytes);
			bytes = new ByteArrayInputStream(data);
		}
		return bytes;
	}

	public ByteArrayInputStream getPacketByteArray(ByteBuf stream) {
		return this.getPacketByteArray(stream, 0, stream.readableBytes());
	}

	public void setCallParam(final PacketCallState call) {
		caller = call;
	}

	@Override
	public void processPacket(final INetHandler handler) {
		caller.call(this);
	}

}
