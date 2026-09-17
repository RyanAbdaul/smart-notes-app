import { createSlice } from "@reduxjs/toolkit";
import { userLogin, userRegister } from "./authService";
const initialState = {
  error: null,
  status: "idle",
  token: localStorage.getItem("token"),
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
        state.error = null;
        state.status = "succeeded";
      })
      .addCase(userRegister.rejected, (state, action) => {
        state.status = "failed";
        state.error = action.payload || action.error.message;
      })
      // Login Reducer
      .addCase(userLogin.pending, (state, action) => {
        state.error = null;
        state.status = "loading";
      })
      .addCase(userLogin.fulfilled, (state, action) => {
        state.error = null;
        state.status = "succeeded";
        state.token = action.payload.token;
        localStorage.setItem("token", action.payload.token);
      })
      .addCase(userLogin.rejected, (state, action) => {
        state.error = action.payload || action.error.message;
        state.status = "failed";
      });
  
  },
    reducers: {
      logout: (state) => {
        state.token = null;
        state.error = null;
        state.status = "idle";

        localStorage.removeItem("token");
      }
    }
});
export const { logout } = authSlice.actions;
export const authReducer = authSlice.reducer;
