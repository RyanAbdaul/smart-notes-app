import { createAsyncThunk } from "@reduxjs/toolkit";
import apiClient from "../../api/apiClient";

export const getAllNotes = createAsyncThunk(
  "notes/getAllNotes",
  async (_, { getState }) => {
    const token = getState().auth.token;
    const { data } = await apiClient.get("/notes", {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return data;
  },
);
export const createNote = createAsyncThunk(
  "notes/createNote",
  async (newNote, { getState }) => {
    const token = getState().auth.token;
    const { data } = await apiClient.post("/notes", newNote, {
      headers: { Authorization: `Bearer ${token}` },
    });
    return data;
  },
);
