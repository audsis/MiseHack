package org.minecraft.wise.api.friends;

import java.util.Objects;

public class Friend {
    final String ign;

    public Friend(String ign) {
        this.ign = ign;
    }

    public boolean equals(Object o2) {
        if (this == o2) {
            return true;
        }
        if (o2 == null || getClass() != o2.getClass()) {
            return false;
        }
        Friend friend = (Friend) o2;
        return ign.equalsIgnoreCase(friend.ign);
    }

    public int hashCode() {
        return Objects.hash(ign);
    }

    public String toString() {
        return ign;
    }
}