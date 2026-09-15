import { createAsyncThunk } from "@reduxjs/toolkit";
import apiClient from "../../api/apiClient"


export const getAllNotes=createAsyncThunk("notes/getAllNotes",async()=>{
    const {data}= await apiClient.get('/notes') ;
    return data
})


