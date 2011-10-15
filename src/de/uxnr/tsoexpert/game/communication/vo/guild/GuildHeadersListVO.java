package de.uxnr.tsoexpert.game.communication.vo.guild;

import java.util.Collection;

import de.uxnr.amf.v3.AMF3_Object;

public class GuildHeadersListVO extends AMF3_Object {
	private Collection<Object> list;
	private int page;
	private int maxPages;

	public Collection<Object> getList() {
		return this.list;
	}

	public int getPage() {
		return this.page;
	}

	public int getMaxPages() {
		return this.maxPages;
	}
}
