package com.github.mob41.sakura.security;

public class PermNode{

	private final String alias;
	
	public PermNode(String alias){
		this.alias = alias;
	}
	
	public String getAlias(){
		return alias;
	}
	
	/**
	 * Same as <code>PermNode.getAlias()</code>
	 */
	public String toString(){
		return getAlias();
	}
	
	/**
	 * Compares the equality of two nodes<br>
	 * <br>
	 * Unlikely to the method of <code>Object</code>, this method only checks the equality of two permission alias (e.g. <code>i.am.a.perm.alias</code>)
	 * @param obj The <code>PermNode</code> instance to be compared
	 * @return The equality of two instances
	 */
	public boolean equals(PermNode obj){
		if (obj == null || alias == null || obj.getAlias() == null){
			return false;
		}
		return obj.getAlias().equals(alias);
	}
}
