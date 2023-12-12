package com.google.android.gms.games.player;

import org.json.JSONException;
import org.json.JSONObject;

public class RelationshipInfo {
    private int friendStatus;
    private String nickName;
    private String invitationNickname;

    public int getFriendStatus() {
        return friendStatus;
    }

    public void setFriendStatus(int friendStatus) {
        this.friendStatus = friendStatus;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getInvitationNickname() {
        return invitationNickname;
    }

    public void setInvitationNickname(String invitationNickname) {
        this.invitationNickname = invitationNickname;
    }

    public static String toJson(RelationshipInfo relationshipInfo) {
        JSONObject jsonObject = new JSONObject();
        if (relationshipInfo == null) return jsonObject.toString();
        try {
            jsonObject.put("friendStatus", relationshipInfo.getFriendStatus());
            jsonObject.put("nickName", relationshipInfo.getNickName());
            jsonObject.put("invitationNickname", relationshipInfo.getInvitationNickname());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
    
    public static RelationshipInfo fromJson(String json) {
        RelationshipInfo relationshipInfo = new RelationshipInfo();
        try {
            JSONObject jsonObject = new JSONObject(json);
            relationshipInfo.setFriendStatus(jsonObject.getInt("friendStatus"));
            relationshipInfo.setNickName(jsonObject.getString("nickName"));
            relationshipInfo.setInvitationNickname(jsonObject.getString("invitationNickname"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return relationshipInfo;
    }
    
}
