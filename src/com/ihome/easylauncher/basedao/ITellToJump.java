package com.ihome.easylauncher.basedao;

public interface ITellToJump {
	//��Ա�����ĺ���
   public void jumpToChooseMemActivity(int id);
   //��Աɾ����֪ͨ������Ա
   public void tellToRefreshMember();
   
   public void tellToOpenCreateDialog(int id,String name,String number);
}
