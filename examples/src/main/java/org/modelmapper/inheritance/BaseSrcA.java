package org.modelmapper.inheritance;

public class BaseSrcA extends BaseSrc {
	private String property1;

	public BaseSrcA(String property1) {
		super();
		this.property1 = property1;
	}

	public String getProperty1() {
		return property1;
	}
}
