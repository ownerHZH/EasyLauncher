package com.ihome.easylauncher.basedao;

public interface ITellToJump {
	//人员更换的函数
   public void jumpToChooseMemActivity(int id);
   //人员删除后通知更新人员
   public void tellToRefreshMember();
   
   public void tellToOpenCreateDialog(int id,String name,String number);
}
