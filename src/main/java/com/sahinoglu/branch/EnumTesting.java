package com.sahinoglu.branch;

public enum EnumTesting{
	S, B, C;

	int a = 5;

	private EnumTesting() {
		System.out.println("enumConst");
	}
	public  void f() {
		System.out.println(this+"in f()");
	}
	
}
