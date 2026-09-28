import { createSlice } from "@reduxjs/toolkit";
import {
  createNote,
  deleteNote,
  getAllNotes,
  updateNote,
} from "./notesService";
const initialState = {
  data: [],
  mode: "view",
  activeNoteId: null,
  isLoading: false,
  isDeleting:false,
  error: null,
};
export const notesSlice = createSlice({
  name: "note",
  initialState,
  extraReducers: (builder) => {
    builder
      // Get ALL Notes
      .addCase(getAllNotes.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(getAllNotes.fulfilled, (state, action) => {
        state.isLoading = false;

        state.data = action.payload;
        if (action.payload.length > 0 && !state.activeNoteId) {
          state.activeNoteId = action.payload[0].id;
        }
      })
      .addCase(getAllNotes.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload || action.error.message;
      })
      // create Note
      .addCase(createNote.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(createNote.fulfilled, (state, action) => {
        state.isLoading = false;

        state.data.push(action.payload);
        state.activeNoteId = action.payload.id;
      })
      .addCase(createNote.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload || action.error.message;
      })
      // Update Note
      .addCase(updateNote.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(updateNote.fulfilled, (state, action) => {
        state.isLoading = false;

        const updatedNote = state.data.find(
          (ele) => ele.id === action.payload.id,
        );
        if (updatedNote) {
          Object.assign(updatedNote, action.payload);
        }
      })
      .addCase(updateNote.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload || action.error.message;
      })
      // Delete Note
      .addCase(deleteNote.pending, (state) => {
        state.isDeleting = true;
        state.error = null;
      })
      .addCase(deleteNote.fulfilled, (state, action) => {
                state.isDeleting = false;

        const deletedIndex = state.data.findIndex(
          (ele) => ele.id === action.payload,
        );
        state.data = state.data.filter((ele) => ele.id != action.payload);
        if (state.data.length === 0) {
          state.activeNoteId = null;
          return;
        }
        if (deletedIndex < state.data.length) {
          state.activeNoteId = state.data[deletedIndex].id;
        } else {
          state.activeNoteId = state.data[state.data.length - 1].id;
        }
      })
      .addCase(deleteNote.rejected, (state, action) => {
        state.isDeleting = false;
        state.error = action.payload || action.error.message;
      });
  },
  reducers: {
    setActiveNote: (state, action) => {
      state.activeNoteId = action.payload;
    },
    setMode: (state, action) => {
      state.mode = action.payload;
    },
  },
});
export const noteReducer = notesSlice.reducer;
export const { setActiveNote, setMode } = notesSlice.actions;
