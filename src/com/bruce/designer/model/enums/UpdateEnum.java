package com.bruce.designer.model.enums;

/**
 * 检查更新的enum 
 * @author liqian
 *
 */
public enum UpdateEnum {
	
	LATEST(0), NEED_UPDATE(1), FORCE_UPDATE(2);
	
	private int updateStatus;

	private UpdateEnum(int updateStatus){
		this.updateStatus = updateStatus;
	}
	
	public int getValue(){
		return this.updateStatus;		
	}
	
	public UpdateEnum valueOf(int value){
		 for(UpdateEnum item :UpdateEnum.values()){ 
			 if(item.getValue()==value){
				 return item;
			 }
		 }
		 return LATEST;
	}
}
