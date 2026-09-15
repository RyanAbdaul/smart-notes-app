import { createSlice } from "@reduxjs/toolkit";
import { getAllNotes,
   } from "./notesService";
const initialState = {
  data: [],
  activeNoteId: null,
  isLoading: false,
  error: null,
};
export const notesSlice = createSlice({
  name: "note",
  initialState,
  extraReducers: (builder) => {
    builder.addCase(getAllNotes.fulfilled, (state, action) => {
      state.data = action.payload;
      if (action.payload.length > 0 && !state.activeNoteId) {
        state.activeNoteId = action.payload[0].id;
      }
    })

  },
  reducers: {
    setActiveNote: (state, action) => {
      state.activeNoteId = action.payload;
    },
  
  },
});
export const noteReducer = notesSlice.reducer;
export const {setActiveNote}=notesSlice.actions