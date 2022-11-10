package taskone;

import java.util.List;
import java.util.ArrayList;

class StringList {
    
    List<String> strings = new ArrayList<String>();

    public void add(String str) {
        int pos = strings.indexOf(str);
        if (pos < 0) {
            strings.add(str);
        }
    }

    public void addAtIndex(int index, String str) {
        strings.add(index, str);
    }

    public void remove(int index) {
        if (strings.size() > 0 && index <= strings.size()) {
            strings.remove(index);
        } else {
            System.out.println("Error, cannot remove index: " + index);
        }
    }

    public String getElement(int index) {
        if (strings.size() > 0 && index < strings.size()) {
            return strings.get(index);
        } else {
            System.out.println("Out of bounds");
            return null;
        }
    }

    public boolean contains(String str) {
        return strings.indexOf(str) >= 0;
    }

    public int size() {
        return strings.size();
    }

    public String toString() {
        return strings.toString();
    }
}