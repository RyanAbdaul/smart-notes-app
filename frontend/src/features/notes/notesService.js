import { createAsyncThunk } from "@reduxjs/toolkit";
import apiClient from "../../api/apiClient";
// Get All Note
export const getAllNotes = createAsyncThunk(
  "notes/getAllNotes",
  async (_, { rejectWithValue }) => {
    try {
      const { data } = await apiClient.get("/notes");
      return data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.error || error.message);
    }
  },
);
// Create note
export const createNote = createAsyncThunk(
  "notes/createNote",
  async (formData, { rejectWithValue }) => {
    try {
      const { data } = await apiClient.post("/notes", formData);
      return data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.error || error.message);
    }
  },
);
// Update Note
export const updateNote = createAsyncThunk(
  "notes/updateNote",
  async ({id, title, description}, { rejectWithValue }) => {
    try {
      const { data } = await apiClient.patch(`/notes/${id}`, {title,description});
      return data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.error || error.message);
    }
  },
);
// Delete Note
export const deleteNote = createAsyncThunk(
  "notes/deleteNote",
  async (id, { rejectWithValue }) => {
    try {
      await apiClient.delete(`/notes/${id}`);
      return id;
    } catch (error) {
      return rejectWithValue(error.response?.data?.error || error.message);
    }
  },
);
