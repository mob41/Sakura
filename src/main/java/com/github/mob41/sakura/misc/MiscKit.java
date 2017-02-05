package com.github.mob41.sakura.misc;

public class MiscKit {

	public static boolean isAllNotNull(Object[] arg0){
		if (arg0 == null){
			return false;
		}
		
		for (int i = 0; i < arg0.length; i++){
			if (arg0[i] == null){
				return false;
			}
		}
		
		return true;
	}
	
	public static boolean isInteger(String str){
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e){
			return false;
		}
	}
	
	public static Object[][] arrayAppend(Object[][] original, Object[] toBeAppended) throws ArrayIndexOutOfBoundsException{
		System.out.println("==================== ARRAY APPEND FUNC ==========================");
		Object[][] output = new Object[original.length + 1][];
		
		System.out.println("OrginLen: " + original.length + " OutputLen: " + output.length);
		for (int i = 0; i < output.length; i++){
			System.out.println("ForLoop: " + i + " / " + output.length);
			if (i >= original.length){
				System.out.println("Breaking: i (" + i + ") == originalLen (" + original.length + ") - 1" + (original.length - 1));
				break;
			}
			System.out.println("Filling output " + i + " from original " + i);
			output[i] = original[i];
		}
		
		System.out.println("Filling output " + (output.length - 1));
		output[output.length - 1] = toBeAppended;
		
		System.out.println("======================= END ARRAY APPEND FUNC ========================");
		return output;
	}
	
	public static Object[] arrayAppend(Object[] original, Object toBeAppended) throws ArrayIndexOutOfBoundsException{
		Object[] output = new Object[original.length + 1];
		
		for (int i = 0; i < output.length; i++){
			if (i == original.length - 1){
				break;
			}
			output[i] = original[i];
		}
		
		output[output.length - 1] = toBeAppended;
		
		return output;
	}
	
	public static Object[][] arrayInsert(int index, Object[][] original, Object[] toBeInserted) throws ArrayIndexOutOfBoundsException{
		if (index >= original.length || index < 0){
			throw new ArrayIndexOutOfBoundsException(index);
		}
		Object[][] output = new Object[original.length + 1][];
		
		int tmp = 0;
		for (int i = 0; i < output.length; i++){
			if (i == index){
				tmp = i + 1;
				break;
			}
			output[i] = original[i];
		}
		
		output[index] = toBeInserted;
		
		for (int i = tmp; i < output.length; i++){
			output[i] = original[i - 1];
		}
		
		return output;
	}
	
	public static Object[] arrayInsert(int index, Object[] original, Object toBeInserted) throws ArrayIndexOutOfBoundsException{
		if (index >= original.length || index < 0){
			throw new ArrayIndexOutOfBoundsException(index);
		}
		Object[] output = new Object[original.length + 1];
		
		int tmp = 0;
		for (int i = 0; i < output.length; i++){
			if (i == index){
				tmp = i + 1;
				break;
			}
			output[i] = original[i];
		}
		
		output[index] = toBeInserted;
		
		for (int i = tmp; i < output.length; i++){
			output[i] = original[i - 1];
		}
		
		return output;
	}

}
