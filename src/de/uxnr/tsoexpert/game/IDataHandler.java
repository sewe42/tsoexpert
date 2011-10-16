package de.uxnr.tsoexpert.game;

import java.io.IOException;

import de.uxnr.amf.AMF_Type;

public interface IDataHandler<T extends AMF_Type> {
	public void handleData(T data) throws IOException;
}