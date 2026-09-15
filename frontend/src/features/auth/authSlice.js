import { createSlice } from "@reduxjs/toolkit";
import { userRegister } from "./authService";
const initialState = {
  user: null,
  error: null,
  status: "idle",
  isAuthenticated: false,
  token: null,
};
const authSlice = createSlice({
  name: "auth",
  initialState,
  extraReducers: (builder) => {
    builder
      .addCase(userRegister.pending, (state, action) => {
        state.status = "loading";
        state.error = null;
      })
      .addCase(userRegister.fulfilled, (state, action) => {
        state.user = action.payload.user;
        state.token = action.payload.token;
        state.error = null;
        state.status = "succeeded";
        state.isAuthenticated = true;
      })
      .addCase(userRegister.rejected, (state, action) => {
        state.status = "failed";
        state.error = action.payload || action.error.message;
        state.isAuthenticated = false;
      });
  },
});
export const authReducer = authSlice.reducer;
