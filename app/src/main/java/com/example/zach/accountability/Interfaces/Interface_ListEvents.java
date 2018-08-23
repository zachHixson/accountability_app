package com.example.zach.accountability.Interfaces;

public interface Interface_ListEvents {
    boolean removeName(int _id, boolean _markForDeletion);
    boolean openInfoBox(String _title, String _text);
    boolean selectName(int _id);
    boolean deselectName(int _id);
}
