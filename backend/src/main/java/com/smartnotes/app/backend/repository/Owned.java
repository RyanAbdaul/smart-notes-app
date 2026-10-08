package com.smartnotes.app.backend.repository;


import com.smartnotes.app.backend.entity.User;

public interface Owned {
    User getOwner();
}
