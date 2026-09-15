import { createAsyncThunk } from "@reduxjs/toolkit";
import apiClient from "../../api/apiClient";

export const userRegister=createAsyncThunk("auth/register",async(formData)=>{
    const {data}=await apiClient.post('/auth/register/',formData)
    return data
})