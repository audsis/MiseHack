package org.minecraft.wise.api.management;

import net.minecraft.entity.Entity;
import org.minecraft.wise.api.config.ISavable;
import org.minecraft.wise.api.friends.Friend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendManager
        implements ISavable {
    public static FriendManager INSTANCE;
    final List<Friend> friends = new ArrayList<>();

    public FriendManager() {
        SavableManager.INSTANCE.getSavables().add(this);
    }

    public List<Friend> getFriends() {
        return friends;
    }

    public boolean isFriend(Entity entity) {
        Friend testFriend = new Friend(entity.getName().getString());
        return friends.contains(testFriend);
    }

    public void addFriend(Entity entity) {
        Friend friend = new Friend(entity.getName().getString());
        friends.add(friend);
    }

    public void removeFriend(Entity entity) {
        Friend friend = new Friend(entity.getName().getString());
        friends.remove(friend);
    }

    public boolean isFriend(String s) {
        Friend testFriend = new Friend(s);
        return friends.contains(testFriend);
    }

    @Override
    public void load(Map<String, Object> objects) {
        if (objects.get("friends") != null) {
            List<String> friendsList = (List<String>) objects.get("friends");
            for (String s : friendsList) {
                friends.add(new Friend(s));
            }
        }
    }

    @Override
    public Map<String, Object> save() {
        HashMap<String, Object> toSave = new HashMap<>();
        ArrayList<String> friendList = new ArrayList<>();

        for (Friend friend : this.friends) {
            friendList.add(friend.toString());
        }

        toSave.put("friends", friendList);
        return toSave;
    }

    @Override
    public String getFileName() {
        return "friends.yml";
    }

    @Override
    public String getDirName() {
        return "misc";
    }
}