import { createAsyncThunk } from "@reduxjs/toolkit";
import apiClient from "../../api/apiClient";

export const userRegister = createAsyncThunk(
  "auth/register",
  async (formData,{rejectWithValue}) => {
    try{
      const { data } = await apiClient.post("/auth/register", formData);
      return data;

    }catch(error){
      return rejectWithValue(error.response?.data?.message||error.message)
    }
  },
);
export const userLogin = createAsyncThunk("auth/login", async (formData,{rejectWithValue}) => {
  try{

    const { data } = await apiClient.post("/auth/login", formData);
    return data;
  }catch(error){
    return rejectWithValue(error.response?.data?.message||error.message)
  }
});
