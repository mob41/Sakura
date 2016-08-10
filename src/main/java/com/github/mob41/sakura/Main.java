package com.github.mob41.sakura;

import com.github.mob41.sakura.security.PermManager;

public class Main {

	public static void main(String[] args) throws Exception {
		PermManager m = new PermManager();
		m.addPerm("aa", "abcdef");
	}

}
